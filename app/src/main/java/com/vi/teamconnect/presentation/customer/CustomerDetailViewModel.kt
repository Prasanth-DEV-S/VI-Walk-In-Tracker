package com.vi.teamconnect.presentation.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.model.CustomerVisitHistory
import com.vi.teamconnect.domain.model.Employee
import com.vi.teamconnect.domain.model.Role
import com.vi.teamconnect.domain.repository.AuthRepository
import com.vi.teamconnect.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerDetailUiState(
    val visit: CustomerVisit? = null,
    val history: List<CustomerVisitHistory> = emptyList(),
    val currentUser: Employee? = null,
    val isLoading: Boolean = false,
    val isDeletedSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recordId: String = checkNotNull(savedStateHandle["recordId"])

    private val _uiState = MutableStateFlow(CustomerDetailUiState(isLoading = true))
    val uiState: StateFlow<CustomerDetailUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val user = authRepository.getCurrentEmployee()
            _uiState.update { it.copy(currentUser = user) }
        }

        viewModelScope.launch {
            customerRepository.getCustomerVisitById(recordId).collect { visit ->
                _uiState.update { it.copy(visit = visit, isLoading = false) }
            }
        }

        viewModelScope.launch {
            customerRepository.getVisitHistory(recordId).collect { history ->
                _uiState.update { it.copy(history = history) }
            }
        }
    }

    fun deleteVisit() {
        val user = _uiState.value.currentUser
        if (user?.role != Role.ADMIN) {
            _uiState.update { it.copy(errorMessage = "Only Admins can delete customer records.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = customerRepository.deleteCustomerVisit(recordId)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isDeletedSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.localizedMessage) }
                }
            )
        }
    }
}
