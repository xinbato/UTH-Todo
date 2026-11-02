package com.example.uthtodo.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uthtodo.data.repository.AuthRepository
import com.example.uthtodo.data.repository.AuthUser
import com.example.uthtodo.data.repository.FirebaseAuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Immutable OOP State representation for Login Screen.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val user: AuthUser? = null,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

/**
 * ViewModel managing Firebase Authentication & Google Sign-In state.
 */
class LoginViewModel @JvmOverloads constructor(
    private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            _uiState.update { it.copy(user = currentUser, isSuccess = true) }
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.signInWithGoogle(context)
            result.onSuccess { user ->
                _uiState.update {
                    it.copy(isLoading = false, user = user, isSuccess = true)
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.localizedMessage ?: "Đăng nhập với Google thất bại."
                    )
                }
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.signInAnonymously()
            result.onSuccess { user ->
                _uiState.update {
                    it.copy(isLoading = false, user = user, isSuccess = true)
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.localizedMessage ?: "Đăng nhập ẩn danh thất bại."
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.update { LoginUiState() }
    }
}
