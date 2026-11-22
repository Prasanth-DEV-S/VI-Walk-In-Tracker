package com.vi.teamconnect.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vi.teamconnect.domain.model.AppSettings
import com.vi.teamconnect.domain.model.Store
import com.vi.teamconnect.domain.repository.BranchRepository
import com.vi.teamconnect.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val branchRepository: BranchRepository
) : ViewModel() {

    val appSettings: StateFlow<AppSettings> = settingsRepository.getAppSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    val stores: StateFlow<List<Store>> = branchRepository.getAllStores().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSettings(settings: AppSettings) {
        viewModelScope.launch {
            settingsRepository.updateAppSettings(settings)
        }
    }

    fun addStore(storeName: String, branchName: String = "Central HQ") {
        if (storeName.isBlank()) return
        viewModelScope.launch {
            branchRepository.addStore(
                Store(
                    storeName = storeName.trim(),
                    branchName = branchName.ifBlank { "Central HQ" },
                    branchId = "BRANCH-HQ-01"
                )
            )
        }
    }
}
