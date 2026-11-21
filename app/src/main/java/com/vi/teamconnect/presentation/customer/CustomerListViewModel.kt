package com.vi.teamconnect.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vi.teamconnect.domain.model.CustomerFilter
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.model.SortBy
import com.vi.teamconnect.domain.repository.CustomerRepository
import com.vi.teamconnect.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CustomerListUiState(
    val filter: CustomerFilter = CustomerFilter(),
    val visits: List<CustomerVisit> = emptyList(),
    val isLoading: Boolean = false,
    val purposeOptions: List<String> = emptyList(),
    val operatorOptions: List<String> = emptyList(),
    val isFilterSheetVisible: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CustomerListViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(CustomerFilter())
    val filter: StateFlow<CustomerFilter> = _filter.asStateFlow()

    private val _isFilterSheetVisible = MutableStateFlow(false)
    val isFilterSheetVisible: StateFlow<Boolean> = _isFilterSheetVisible.asStateFlow()

    val visits: StateFlow<List<CustomerVisit>> = _filter.flatMapLatest { currentFilter ->
        customerRepository.getCustomerVisits(currentFilter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _filter.update { it.copy(searchQuery = query) }
    }

    fun updateFilter(newFilter: CustomerFilter) {
        _filter.value = newFilter
    }

    fun resetFilter() {
        _filter.value = CustomerFilter(searchQuery = _filter.value.searchQuery)
    }

    fun toggleFilterSheet(visible: Boolean) {
        _isFilterSheetVisible.value = visible
    }

    fun setSortBy(sortBy: SortBy) {
        _filter.update { it.copy(sortBy = sortBy) }
    }
}
