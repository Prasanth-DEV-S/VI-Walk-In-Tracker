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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val email: String = "",
    val employeeId: String = "",
    val employeeName: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val selectedBranch: Branch? = null,
    val branches: List<Branch> = emptyList(),
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

    fun onEmailChanged(email: String) { _uiState.update { it.copy(email = email, errorMessage = null) } }
    fun onEmployeeIdChanged(id: String) { _uiState.update { it.copy(employeeId = id, errorMessage = null) } }
    fun onEmployeeNameChanged(name: String) { _uiState.update { it.copy(employeeName = name, errorMessage = null) } }
    fun onPhoneNumberChanged(phone: String) { _uiState.update { it.copy(phoneNumber = phone, errorMessage = null) } }
    fun onPasswordChanged(pwd: String) { _uiState.update { it.copy(password = pwd, errorMessage = null) } }
    fun onConfirmPasswordChanged(pwd: String) { _uiState.update { it.copy(confirmPassword = pwd, errorMessage = null) } }
    fun onBranchSelected(branch: Branch) { _uiState.update { it.copy(selectedBranch = branch, errorMessage = null) } }
    fun togglePasswordVisibility() { _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) } }

    fun register() {
        val state = _uiState.value
        val cleanEmpId = state.employeeId.trim()
        val cleanEmail = if (state.email.isNotBlank()) state.email.trim() else "${cleanEmpId.lowercase()}@viteamconnect.internal"

        if (cleanEmpId.isBlank()) {
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

            val result = authRepository.register(
                email = cleanEmail,
                employeeId = cleanEmpId,
                fullName = state.employeeName.trim(),
                phoneNumber = state.phoneNumber.trim(),
                password = state.password
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegistrationSuccess = true,
                            successMessage = "Registration Submitted Successfully!\n\nStatus: PENDING ADMIN APPROVAL\n\nAn administrator must approve your account before access is granted."
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
