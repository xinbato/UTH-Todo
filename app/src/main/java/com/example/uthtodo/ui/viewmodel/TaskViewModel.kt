package com.example.uthtodo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uthtodo.data.model.AppNotification
import com.example.uthtodo.data.model.Subtask
import com.example.uthtodo.data.model.Task
import com.example.uthtodo.data.repository.AuthRepository
import com.example.uthtodo.data.repository.FirebaseAuthRepositoryImpl
import com.example.uthtodo.data.repository.FirestoreNotificationRepositoryImpl
import com.example.uthtodo.data.repository.FirestoreTaskRepositoryImpl
import com.example.uthtodo.data.repository.NotificationRepository
import com.example.uthtodo.data.repository.TaskRepository
import com.example.uthtodo.data.repository.initials
import com.example.uthtodo.model.CategorySummaryModel
import com.example.uthtodo.model.HomeScreenModel
import com.example.uthtodo.model.HomeTaskItem
import com.example.uthtodo.model.TodayProgressModel
import com.example.uthtodo.model.UserHeaderModel
import com.example.uthtodo.notification.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias HomeUiState = HomeScreenModel

/**
 * UI State for Task creation operations.
 */
data class TaskUiState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel responsible for Task business logic with Cloud Firestore.
 */
class TaskViewModel @JvmOverloads constructor(
    application: Application,
    private val taskRepository: TaskRepository = FirestoreTaskRepositoryImpl(),
    private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl(),
    private val notificationRepository: NotificationRepository = FirestoreNotificationRepositoryImpl()
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private val _manualUserId = MutableStateFlow<String?>(null)

    val currentUserIdFlow: Flow<String> = authRepository.currentUserFlow
        .map { it?.uid ?: "guest_user" }
        .combine(_manualUserId) { authUid, manualUid ->
            manualUid ?: authUid
        }
        .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = currentUserIdFlow
        .flatMapLatest { uid ->
            taskRepository.getTasksFlow(uid)
                .catch { e ->
                    emit(emptyList())
                    _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Lỗi tải công việc") }
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val homeUiState: StateFlow<HomeScreenModel> = combine(
        tasks,
        authRepository.currentUserFlow
    ) { taskList, user ->
        val isGuest = user == null || user.isAnonymous
        val displayName = if (!isGuest && !user?.displayName.isNullOrBlank()) user!!.displayName!! else "UTH"
        val avatarUrl = if (!isGuest) user?.photoUrl else null
        val initials = user.initials

        val totalCount = taskList.size
        val completedCount = taskList.count { it.isCompleted }
        val percentage = if (totalCount > 0) (completedCount * 100 / totalCount) else 0

        val categorySummaries = listOf(
            CategorySummaryModel(1, "Học tập", taskList.count { it.category == "Học tập" && !it.isCompleted }, true),
            CategorySummaryModel(2, "Cuộc sống", taskList.count { it.category == "Cuộc sống" && !it.isCompleted }, false)
        )

        val homeTaskItems = taskList.map { t ->
            HomeTaskItem(
                id = t.id,
                title = t.title,
                categoryName = t.category,
                timeText = if (t.timeText.isNotBlank()) t.timeText else "Hôm nay",
                isChecked = t.isCompleted,
                isPrimaryCategory = t.category == "Học tập"
            )
        }

        val motivationText = when {
            totalCount == 0 -> "Bắt đầu thêm công việc đầu tiên nhé!"
            completedCount == totalCount -> "Bạn đã hoàn thành tất cả công việc!"
            percentage >= 50 -> "Đã hoàn thành hơn nửa rồi, cố lên!"
            else -> "Bắt đầu từ việc nhỏ nhất nhé."
        }

        val todayFormatted = SimpleDateFormat("EEEE, d 'tháng' M", Locale.forLanguageTag("vi-VN"))
            .format(Date())
            .replaceFirstChar { it.uppercase() }

        HomeScreenModel(
            userHeader = UserHeaderModel(
                userName = displayName,
                dateText = todayFormatted,
                avatarInitials = initials,
                avatarUrl = avatarUrl
            ),
            progress = TodayProgressModel(
                completedCount = completedCount,
                totalCount = totalCount,
                percentage = percentage,
                motivationText = motivationText
            ),
            categories = categorySummaries,
            todayTasks = homeTaskItems
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeScreenModel()
    )

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

    fun setUserId(uid: String) {
        _manualUserId.value = uid
    }

    fun selectTask(task: Task?) {
        _selectedTask.value = task
    }

    fun addTask(
        title: String,
        note: String,
        category: String,
        priority: String,
        dueDate: java.util.Date? = null,
        hasReminder: Boolean = false,
        timeText: String = "",
        onTaskCreated: ((taskId: String, task: Task) -> Unit)? = null
    ) {
        if (title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Tiêu đề công việc không được để trống") }
            return
        }

        val formattedTime = if (dueDate != null) {
            SimpleDateFormat("HH:mm, dd/MM", Locale.getDefault()).format(dueDate)
        } else timeText

        val currentUid = authRepository.getCurrentUser()?.uid ?: _manualUserId.value ?: "guest_user"

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSaved = false) }
            val task = Task(
                userId = currentUid,
                title = title.trim(),
                note = note.trim(),
                category = category,
                priority = priority,
                timeText = formattedTime,
                dueDate = dueDate,
                hasReminder = hasReminder,
                isCompleted = false
            )
            taskRepository.addTask(task)
                .onSuccess { docId ->
                    _uiState.update { it.copy(isLoading = false, isSaved = true) }

                    if (hasReminder && dueDate != null) {
                        val triggerTime = dueDate.time - (15 * 60 * 1000)
                        val actualTrigger = if (triggerTime > System.currentTimeMillis()) triggerTime else dueDate.time

                        NotificationHelper.scheduleTaskReminder(
                            context = getApplication(),
                            taskId = docId,
                            title = "Nhắc việc: ${task.title}",
                            message = "Sắp đến hạn cho công việc (${task.category})",
                            triggerAtMillis = actualTrigger
                        )

                        notificationRepository.addNotification(
                            AppNotification(
                                userId = currentUid,
                                taskId = docId,
                                title = "Nhắc việc: ${task.title}",
                                message = "Công việc '${task.title}' sắp đến hạn chót.",
                                type = "REMINDER",
                                isRead = false
                            )
                        )
                    }
                    onTaskCreated?.invoke(docId, task.copy(id = docId))
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.localizedMessage ?: "Lưu công việc thất bại."
                        )
                    }
                }
        }
    }

    fun toggleTask(taskId: String, isCompleted: Boolean) {
        if (taskId.isBlank()) return
        viewModelScope.launch {
            taskRepository.updateTaskStatus(taskId, isCompleted)
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(errorMessage = exception.localizedMessage ?: "Cập nhật trạng thái thất bại.")
                    }
                }
        }
    }

    fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) = toggleTask(taskId, isCompleted)

    fun updateTask(taskId: String, isCompleted: Boolean) = toggleTask(taskId, isCompleted)

    fun deleteTask(taskId: String) {
        if (taskId.isBlank()) return
        NotificationHelper.cancelTaskReminder(getApplication(), taskId)
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(errorMessage = exception.localizedMessage ?: "Xóa công việc thất bại.")
                    }
                }
        }
    }

    fun addSubtask(taskId: String, title: String) {
        if (taskId.isBlank() || title.isBlank()) return
        val task = tasks.value.find { it.id == taskId } ?: return
        val newSubtask = Subtask(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            isCompleted = false
        )
        val updatedSubtasks = task.subtasks + newSubtask
        viewModelScope.launch {
            taskRepository.updateSubtasks(taskId, updatedSubtasks)
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(errorMessage = exception.localizedMessage ?: "Cập nhật việc nhỏ thất bại.")
                    }
                }
        }
    }

    fun toggleSubtask(taskId: String, subtaskId: String, isCompleted: Boolean) {
        if (taskId.isBlank() || subtaskId.isBlank()) return
        val task = tasks.value.find { it.id == taskId } ?: return
        val updatedSubtasks = task.subtasks.map { sub ->
            if (sub.id == subtaskId) sub.copy(isCompleted = isCompleted) else sub
        }
        viewModelScope.launch {
            taskRepository.updateSubtasks(taskId, updatedSubtasks)
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(errorMessage = exception.localizedMessage ?: "Cập nhật việc nhỏ thất bại.")
                    }
                }
        }
    }

    fun deleteSubtask(taskId: String, subtaskId: String) {
        if (taskId.isBlank() || subtaskId.isBlank()) return
        val task = tasks.value.find { it.id == taskId } ?: return
        val updatedSubtasks = task.subtasks.filter { it.id != subtaskId }
        viewModelScope.launch {
            taskRepository.updateSubtasks(taskId, updatedSubtasks)
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(errorMessage = exception.localizedMessage ?: "Xóa việc nhỏ thất bại.")
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetState() {
        _uiState.update { TaskUiState() }
    }
}
