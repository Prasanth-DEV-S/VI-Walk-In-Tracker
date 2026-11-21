package com.vi.teamconnect.presentation.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vi.teamconnect.domain.model.AccountStatus
import com.vi.teamconnect.domain.model.Employee
import com.vi.teamconnect.domain.model.Role
import com.vi.teamconnect.domain.repository.AuthRepository
import com.vi.teamconnect.domain.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployeeManagementUiState(
    val pendingEmployees: List<Employee> = emptyList(),
    val allEmployees: List<Employee> = emptyList(),
    val selectedTab: Int = 0, // 0: Pending Approvals, 1: All Employees
    val isLoading: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class EmployeeManagementViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val uiState: StateFlow<EmployeeManagementUiState> = combine(
        employeeRepository.getPendingApprovalEmployees(),
        employeeRepository.getAllEmployees(),
        _selectedTab
    ) { pending, all, tab ->
        EmployeeManagementUiState(
            pendingEmployees = pending,
            allEmployees = all,
            selectedTab = tab
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EmployeeManagementUiState()
    )

    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    fun approveEmployee(uid: String) {
        viewModelScope.launch {
            val adminUser = authRepository.getCurrentEmployee()
            val adminName = adminUser?.fullName ?: "Admin"
            val result = employeeRepository.approveEmployee(uid, adminName)
            result.fold(
                onSuccess = { _message.value = "Employee registration approved!" },
                onFailure = { error -> _errorMessage.value = error.localizedMessage }
            )
        }
    }

    fun rejectEmployee(uid: String) {
        viewModelScope.launch {
            val result = employeeRepository.rejectEmployee(uid)
            result.fold(
                onSuccess = { _message.value = "Employee registration rejected." },
                onFailure = { error -> _errorMessage.value = error.localizedMessage }
            )
        }
    }

    fun updateRole(uid: String, role: Role) {
        viewModelScope.launch {
            val result = employeeRepository.updateEmployeeRole(uid, role)
            result.fold(
                onSuccess = { _message.value = "Employee role updated to ${role.name}." },
                onFailure = { error -> _errorMessage.value = error.localizedMessage }
            )
        }
    }

    fun updateStatus(uid: String, status: AccountStatus) {
        viewModelScope.launch {
            val result = employeeRepository.updateEmployeeStatus(uid, status)
            result.fold(
                onSuccess = { _message.value = "Employee status updated to ${status.name}." },
                onFailure = { error -> _errorMessage.value = error.localizedMessage }
            )
        }
    }

    fun clearMessages() {
        _message.value = null
        _errorMessage.value = null
    }
}
