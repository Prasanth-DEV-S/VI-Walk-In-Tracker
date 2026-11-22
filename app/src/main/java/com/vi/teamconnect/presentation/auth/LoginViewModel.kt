package com.vi.teamconnect.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vi.teamconnect.domain.model.AccountStatus
import com.vi.teamconnect.domain.model.Employee
import com.vi.teamconnect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isOtpSent: Boolean = false,
    val email: String = "",
    val password: String = "",
    val isPasswordLoginMode: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val loggedInUser: Employee? = null,
    val isPendingApproval: Boolean = false,
    val isNotRegistered: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPhoneNumberChanged(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone, errorMessage = null, infoMessage = null, isNotRegistered = false) }
    }

    fun onOtpCodeChanged(otp: String) {
        _uiState.update { it.copy(otpCode = otp, errorMessage = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(pwd: String) {
        _uiState.update { it.copy(password = pwd, errorMessage = null) }
    }

    fun toggleLoginMode() {
        _uiState.update { it.copy(isPasswordLoginMode = !it.isPasswordLoginMode, errorMessage = null, infoMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun sendOtp() {
        val phone = _uiState.value.phoneNumber.trim()
        if (phone.length < 10) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid 10-digit registered Phone Number.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }

            // Check if phone number is registered in Firestore
            val employee = authRepository.getEmployeeByPhone(phone)
            if (employee == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isNotRegistered = true,
                        errorMessage = "Phone number $phone is not registered. Please register first for Admin approval."
                    )
                }
                return@launch
            }

            // Check account status
            when (employee.accountStatus) {
                AccountStatus.PENDING_APPROVAL -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPendingApproval = true,
                            infoMessage = "Account for ${employee.employeeName} is PENDING ADMIN APPROVAL. You will be allowed access after Admin approves your registration."
                        )
                    }
                    return@launch
                }
                AccountStatus.REJECTED -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Registration for $phone was REJECTED by Admin.")
                    }
                    return@launch
                }
                AccountStatus.SUSPENDED -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Account for $phone is SUSPENDED by Admin.")
                    }
                    return@launch
                }
                AccountStatus.ACTIVE -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isOtpSent = true,
                            infoMessage = "OTP sent to +91 $phone. Enter 6-digit OTP code."
                        )
                    }
                }
            }
        }
    }

    fun verifyOtp() {
        val otp = _uiState.value.otpCode.trim()
        val phone = _uiState.value.phoneNumber.trim()

        if (otp.length < 4) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid 6-digit OTP code.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val employee = authRepository.getEmployeeByPhone(phone)

            if (employee != null && employee.accountStatus == AccountStatus.ACTIVE) {
                _uiState.update { it.copy(isLoading = false, loggedInUser = employee) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Verification failed. User profile not found or inactive.") }
            }
        }
    }

    fun loginWithPassword() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter Email/Phone and Password.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.login(state.email, state.password)
            result.fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(isLoading = false, loggedInUser = user) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.localizedMessage) }
                }
            )
        }
    }

    fun resetOtpState() {
        _uiState.update { it.copy(isOtpSent = false, otpCode = "", errorMessage = null, infoMessage = null) }
    }
}
