package com.vi.teamconnect.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vi.teamconnect.domain.model.AccountStatus
import com.vi.teamconnect.domain.model.Branch
import com.vi.teamconnect.domain.model.Employee
import com.vi.teamconnect.domain.model.Role
import com.vi.teamconnect.domain.repository.AuthRepository
import com.vi.teamconnect.domain.repository.BranchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val employeeId: String = "",
    val employeeName: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val selectedBranch: Branch? = null,
    val branches: List<Branch> = emptyList(),
    val isOtpSent: Boolean = false,
    val otpCode: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isRegistrationSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val branchRepository: BranchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    init {
        loadBranches()
    }

    private fun loadBranches() {
        viewModelScope.launch {
            branchRepository.getAllBranches().collect { branchList ->
                val finalBranches = if (branchList.isEmpty()) {
                    listOf(
                        Branch("BRANCH-DEL-01", "Connaught Place Branch", "CP01"),
                        Branch("BRANCH-MUM-01", "Andheri West Branch", "AW01"),
                        Branch("BRANCH-BLR-01", "MG Road Branch", "MG01"),
                        Branch("BRANCH-CHE-01", "Chennai Anna Nagar Branch", "AN01")
                    )
                } else {
                    branchList
                }

                _uiState.update { it.copy(branches = finalBranches) }
            }
        }
    }

    fun onEmployeeIdChanged(id: String) { _uiState.update { it.copy(employeeId = id, errorMessage = null) } }
    fun onEmployeeNameChanged(name: String) { _uiState.update { it.copy(employeeName = name, errorMessage = null) } }
    fun onPhoneNumberChanged(phone: String) { _uiState.update { it.copy(phoneNumber = phone, errorMessage = null) } }
    fun onPasswordChanged(pwd: String) { _uiState.update { it.copy(password = pwd, errorMessage = null) } }
    fun onConfirmPasswordChanged(pwd: String) { _uiState.update { it.copy(confirmPassword = pwd, errorMessage = null) } }
    fun onBranchSelected(branch: Branch) { _uiState.update { it.copy(selectedBranch = branch, errorMessage = null) } }
    fun onOtpCodeChanged(code: String) { _uiState.update { it.copy(otpCode = code, errorMessage = null) } }
    fun togglePasswordVisibility() { _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) } }

    fun register() = sendOtp()

    fun sendOtp() {
        val state = _uiState.value
        if (state.employeeId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Employee ID is required.") }
            return
        }
        if (state.employeeName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Full Name is required.") }
            return
        }
        if (state.phoneNumber.isBlank() || state.phoneNumber.length < 10) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid 10-digit Phone Number.") }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Password is required.") }
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Password and Confirm Password do not match.") }
            return
        }
        if (state.selectedBranch == null) {
            _uiState.update { it.copy(errorMessage = "Please select your Branch from the dropdown.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val existingPhoneUser = authRepository.getEmployeeByPhone(state.phoneNumber.trim())
            if (existingPhoneUser != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Phone number ${state.phoneNumber.trim()} is already registered."
                    )
                }
                return@launch
            }

            delay(800) // Simulate Phone Auth SMS OTP trigger
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isOtpSent = true,
                    errorMessage = null,
                    successMessage = "OTP sent successfully to +91 ${state.phoneNumber.takeLast(4).padStart(10, '*')}. Enter 6-digit OTP code below (Test OTP: 123456)."
                )
            }
        }
    }

    fun verifyOtpAndRegister() {
        val state = _uiState.value
        if (state.otpCode.isBlank() || state.otpCode.length < 4) {
            _uiState.update { it.copy(errorMessage = "Please enter the complete OTP code.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val newEmployee = Employee(
                employeeUid = "",
                employeeId = state.employeeId.trim(),
                employeeName = state.employeeName.trim(),
                phoneNumber = state.phoneNumber.trim(),
                branchId = state.selectedBranch?.branchId ?: "BRANCH-HQ-01",
                branchName = state.selectedBranch?.branchName ?: "Central HQ",
                role = Role.EMPLOYEE,
                accountStatus = AccountStatus.PENDING_APPROVAL,
                createdAt = System.currentTimeMillis()
            )

            val result = authRepository.registerEmployeeProfile(newEmployee)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegistrationSuccess = true,
                            successMessage = "Phone Number Verified & Registration Submitted Successfully!\n\nStatus: PENDING ADMIN APPROVAL\n\nAn administrator must approve your account before access is granted."
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.localizedMessage) }
                }
            )
        }
    }

    fun resetOtpStep() {
        _uiState.update { it.copy(isOtpSent = false, otpCode = "", errorMessage = null) }
    }
}
