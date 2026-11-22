package com.vi.teamconnect.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vi.teamconnect.domain.model.CustomerFilter
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.model.DashboardStats
import com.vi.teamconnect.domain.model.Employee
import com.vi.teamconnect.domain.repository.AuthRepository
import com.vi.teamconnect.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val currentUser: Employee? = null,
    val stats: DashboardStats = DashboardStats(),
    val recentVisits: List<CustomerVisit> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        authRepository.currentUserFlow,
        customerRepository.getDashboardStats(),
        customerRepository.getCustomerVisits(CustomerFilter())
    ) { user, stats, visits ->
        HomeUiState(
            currentUser = user,
            stats = stats,
            recentVisits = visits.take(10), // Recent 10 visits
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun updateCustomerVisit(visit: CustomerVisit) {
        viewModelScope.launch {
            val user = authRepository.getCurrentEmployee()
            val updated = visit.copy(updatedBy = user?.employeeUid ?: "")
            customerRepository.updateCustomerVisit(updated)
        }
    }
}
