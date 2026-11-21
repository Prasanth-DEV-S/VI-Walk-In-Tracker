package com.vi.teamconnect.presentation.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vi.teamconnect.domain.model.CustomerFilter
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.repository.CustomerRepository
import com.vi.teamconnect.utils.ExportUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ReportsUiState(
    val startDate: String = "",
    val endDate: String = "",
    val selectedReportType: String = "Monthly Visit Report",
    val visits: List<CustomerVisit> = emptyList(),
    val totalCount: Int = 0,
    val preCount: Int = 0,
    val ppCount: Int = 0,
    val happyCallingYesCount: Int = 0,
    val creBreakdown: Map<String, Int> = emptyMap(),
    val operatorBreakdown: Map<String, Int> = emptyMap(),
    val purposeBreakdown: Map<String, Int> = emptyMap(),
    val exportFile: File? = null,
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _startDate = MutableStateFlow("")
    val startDate: StateFlow<String> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow("")
    val endDate: StateFlow<String> = _endDate.asStateFlow()

    private val _reportType = MutableStateFlow("Monthly Visit Report")
    val reportType: StateFlow<String> = _reportType.asStateFlow()

    private val _filter = MutableStateFlow(CustomerFilter())

    val uiState: StateFlow<ReportsUiState> = _filter.flatMapLatest { currentFilter ->
        customerRepository.getCustomerVisits(currentFilter)
    }.flatMapLatest { visits ->
        val total = visits.size
        val pre = visits.count { it.prePp.equals("PRE", ignoreCase = true) }
        val pp = visits.count { it.prePp.equals("PP", ignoreCase = true) }
        val happyYes = visits.count { it.happyCalling.equals("Yes", ignoreCase = true) }

        val creMap = visits.groupBy { it.creName.ifEmpty { "Unknown" } }.mapValues { it.value.size }
        val opMap = visits.groupBy { it.operator.ifEmpty { "Other" } }.mapValues { it.value.size }
        val purpMap = visits.groupBy { it.purposeOfVisit.ifEmpty { "Other" } }.mapValues { it.value.size }

        MutableStateFlow(
            ReportsUiState(
                startDate = _startDate.value,
                endDate = _endDate.value,
                selectedReportType = _reportType.value,
                visits = visits,
                totalCount = total,
                preCount = pre,
                ppCount = pp,
                happyCallingYesCount = happyYes,
                creBreakdown = creMap,
                operatorBreakdown = opMap,
                purposeBreakdown = purpMap,
                isLoading = false
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportsUiState(isLoading = true)
    )

    fun onStartDateChanged(date: String) {
        _startDate.value = date
        _filter.update { it.copy(startDate = date) }
    }

    fun onEndDateChanged(date: String) {
        _endDate.value = date
        _filter.update { it.copy(endDate = date) }
    }

    fun onReportTypeChanged(type: String) {
        _reportType.value = type
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        when (type) {
            "Daily Customer Visit Report" -> {
                _startDate.value = today
                _endDate.value = today
                _filter.update { it.copy(startDate = today, endDate = today) }
            }
            "Weekly Customer Visit Report" -> {
                // Last 7 days
                val sevenDaysAgo = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(System.currentTimeMillis() - 7L * 24 * 3600 * 1000))
                _startDate.value = sevenDaysAgo
                _endDate.value = today
                _filter.update { it.copy(startDate = sevenDaysAgo, endDate = today) }
            }
            "Monthly Visit Report" -> {
                val startOfMonth = SimpleDateFormat("yyyy-MM-01", Locale.getDefault()).format(Date())
                _startDate.value = startOfMonth
                _endDate.value = today
                _filter.update { it.copy(startDate = startOfMonth, endDate = today) }
            }
            else -> {
                _startDate.value = ""
                _endDate.value = ""
                _filter.update { it.copy(startDate = "", endDate = "") }
            }
        }
    }

    fun exportToCsv(context: Context) {
        val currentVisits = uiState.value.visits
        ExportUtils.shareCsvFile(context, currentVisits, "VI_Visit_Report")
    }
}
