package com.vi.teamconnect.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vi.teamconnect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val email: String = "",
    val employeeId: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isRegistrationSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) { _uiState.update { it.copy(email = email, errorMessage = null) } }
    fun onEmployeeIdChanged(id: String) { _uiState.update { it.copy(employeeId = id, errorMessage = null) } }
    fun onFullNameChanged(name: String) { _uiState.update { it.copy(fullName = name, errorMessage = null) } }
    fun onPhoneNumberChanged(phone: String) { _uiState.update { it.copy(phoneNumber = phone, errorMessage = null) } }
    fun onPasswordChanged(pwd: String) { _uiState.update { it.copy(password = pwd, errorMessage = null) } }
    fun onConfirmPasswordChanged(pwd: String) { _uiState.update { it.copy(confirmPassword = pwd, errorMessage = null) } }
    fun togglePasswordVisibility() { _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) } }

    fun register() {
        val state = _uiState.value
        if (state.email.isBlank() || state.employeeId.isBlank() || state.fullName.isBlank() || state.phoneNumber.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All fields are required.") }
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.register(
                email = state.email,
                employeeId = state.employeeId,
                fullName = state.fullName,
                phoneNumber = state.phoneNumber,
                password = state.password
            )
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegistrationSuccess = true,
                            successMessage = "Registration submitted successfully! Your account is pending Admin approval."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.localizedMessage) }
                }
            )
        }
    }
}
