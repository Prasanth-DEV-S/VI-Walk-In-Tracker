package com.vi.teamconnect.presentation.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vi.teamconnect.domain.model.BranchPerformance
import com.vi.teamconnect.domain.model.CustomerFilter
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.model.DashboardStats
import com.vi.teamconnect.domain.model.DateRangeOption
import com.vi.teamconnect.domain.model.EmployeePerformance
import com.vi.teamconnect.domain.repository.CustomerRepository
import com.vi.teamconnect.utils.ExportUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.File
import javax.inject.Inject

data class ReportsUiState(
    val startDate: String = "",
    val endDate: String = "",
    val dateRangeOption: DateRangeOption = DateRangeOption.ALL,
    val selectedBranchId: String = "",
    val selectedEmployeeUid: String = "",
    val stats: DashboardStats = DashboardStats(),
    val branchPerformances: List<BranchPerformance> = emptyList(),
    val employeePerformances: List<EmployeePerformance> = emptyList(),
    val visits: List<CustomerVisit> = emptyList(),
    val exportFile: File? = null,
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(CustomerFilter())

    val uiState: StateFlow<ReportsUiState> = _filter.flatMapLatest { f ->
        val visitsFlow = customerRepository.getCustomerVisits(f)
        val statsFlow = customerRepository.getDashboardStats(f.branchId, f)
        val branchFlow = customerRepository.getBranchPerformances(f.startDate, f.endDate)
        val empFlow = customerRepository.getEmployeePerformances(f.branchId, f.startDate, f.endDate)

        kotlinx.coroutines.flow.combine(visitsFlow, statsFlow, branchFlow, empFlow) { visits, stats, branchStats, empStats ->
            ReportsUiState(
                startDate = f.startDate,
                endDate = f.endDate,
                dateRangeOption = f.dateRange,
                selectedBranchId = f.branchId,
                selectedEmployeeUid = f.employeeUid,
                stats = stats,
                branchPerformances = branchStats,
                employeePerformances = empStats,
                visits = visits,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportsUiState(isLoading = true)
    )

    fun onDateRangeChanged(option: DateRangeOption) {
        _filter.update { it.copy(dateRange = option, startDate = "", endDate = "") }
    }

    fun onStartDateChanged(date: String) {
        _filter.update { it.copy(startDate = date, dateRange = DateRangeOption.CUSTOM) }
    }

    fun onEndDateChanged(date: String) {
        _filter.update { it.copy(endDate = date, dateRange = DateRangeOption.CUSTOM) }
    }

    fun onBranchSelected(branchId: String) {
        _filter.update { it.copy(branchId = branchId) }
    }

    fun onEmployeeSelected(empUid: String) {
        _filter.update { it.copy(employeeUid = empUid) }
    }

    fun exportToCsv(context: Context) {
        val currentVisits = uiState.value.visits
        ExportUtils.shareCsvFile(context, currentVisits, "VI_Performance_Report")
    }
}
