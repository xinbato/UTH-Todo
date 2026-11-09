package com.example.uthtodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uthtodo.data.model.AppNotification
import com.example.uthtodo.data.repository.AuthRepository
import com.example.uthtodo.data.repository.FirebaseAuthRepositoryImpl
import com.example.uthtodo.data.repository.FirestoreNotificationRepositoryImpl
import com.example.uthtodo.data.repository.NotificationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for Notification operations.
 */
data class NotificationUiState(
    val notifications: List<AppNotification> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel managing notification state and Firestore syncing.
 */
class NotificationViewModel @JvmOverloads constructor(
    private val notificationRepository: NotificationRepository = FirestoreNotificationRepositoryImpl(),
    private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _manualUserId = MutableStateFlow<String?>(null)

    val currentUserIdFlow: Flow<String> = authRepository.currentUserFlow
        .map { it?.uid ?: "guest_user" }
        .combine(_manualUserId) { authUid, manualUid ->
            manualUid ?: authUid
        }
        .distinctUntilChanged()

    val userId: StateFlow<String> = currentUserIdFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = authRepository.getCurrentUser()?.uid ?: "guest_user"
        )

    fun setUserId(uid: String) {
        _manualUserId.value = uid
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val notifications: StateFlow<List<AppNotification>> = currentUserIdFlow
        .flatMapLatest { uid ->
            notificationRepository.getNotificationsFlow(uid)
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Lỗi tải thông báo") }
                    emit(emptyList())
                }
        }
        .onEach { list ->
            _uiState.update { it.copy(notifications = list) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val unreadCount: StateFlow<Int> = notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun markAsRead(notificationId: String) {
        if (notificationId.isBlank()) return
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Không thể cập nhật thông báo") }
                }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUser()?.uid ?: _manualUserId.value ?: "guest_user"
            notificationRepository.markAllAsRead(uid)
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Không thể đánh dấu tất cả đã đọc") }
                }
        }
    }

    fun deleteNotification(notificationId: String) {
        if (notificationId.isBlank()) return
        viewModelScope.launch {
            notificationRepository.deleteNotification(notificationId)
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Không thể xóa thông báo") }
                }
        }
    }

    fun createNotification(
        title: String,
        message: String,
        taskId: String? = null,
        type: String = "REMINDER"
    ) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUser()?.uid ?: _manualUserId.value ?: "guest_user"
            val item = AppNotification(
                userId = uid,
                taskId = taskId,
                title = title,
                message = message,
                type = type,
                isRead = false
            )
            notificationRepository.addNotification(item)
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Không thể tạo thông báo") }
                }
        }
    }

    fun createWelcomeNotificationIfEmpty(uid: String? = null) {
        viewModelScope.launch {
            val targetUid = uid ?: authRepository.getCurrentUser()?.uid ?: _manualUserId.value ?: "guest_user"
            val currentList = _uiState.value.notifications
            if (currentList.isEmpty()) {
                val item = AppNotification(
                    userId = targetUid,
                    title = "Chào mừng bạn đến với UTH Todo",
                    message = "Dữ liệu công việc của bạn được lưu trữ an toàn trên đám mây.",
                    type = "SYSTEM",
                    isRead = false
                )
                notificationRepository.addNotification(item)
                    .onFailure { e ->
                        _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Không thể tạo thông báo chào mừng") }
                    }
            }
        }
    }

    fun addWelcomeNotificationIfEmpty(uid: String? = null) = createWelcomeNotificationIfEmpty(uid)

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
