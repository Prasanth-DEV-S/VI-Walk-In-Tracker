package com.vi.teamconnect.presentation.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vi.teamconnect.domain.model.AppSettings
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.repository.AuthRepository
import com.vi.teamconnect.domain.repository.CustomerRepository
import com.vi.teamconnect.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class AddEditCustomerUiState(
    val recordId: String? = null,
    val isEditMode: Boolean = false,

    // Fields
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val token: String = "",
    val storeId: String = "",
    val storeName: String = "",
    val branchId: String = "",
    val branchName: String = "",
    val creName: String = "",
    val creUid: String = "",
    val creEmployeeId: String = "",
    val cusName: String = "",
    val mobNo: String = "",
    val purposeOfVisit: String = "SIM Swap",
    val prePp: String = "PREPAID",
    val alterNo: String = "",
    val srNo: String = "",
    val operator: String = "Vi",
    val gbp: String = "",
    val remarks: String = "",
    val happyCalling: String = "Pending",
    val did: String = "",
    val refferel: String = "Self",
    val gmb: String = "NO",
    val tagging: String = "NO",

    // Dropdown Options
    val settings: AppSettings = AppSettings(),
    val isLoading: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddEditCustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recordId: String? = savedStateHandle["recordId"]

    private val _uiState = MutableStateFlow(AddEditCustomerUiState())
    val uiState: StateFlow<AddEditCustomerUiState> = _uiState.asStateFlow()

    init {
        loadSettingsAndUser()
        if (!recordId.isNullOrBlank() && recordId != "new") {
            loadExistingRecord(recordId)
        }
    }

    private fun loadSettingsAndUser() {
        viewModelScope.launch {
            settingsRepository.getAppSettings().collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }

        viewModelScope.launch {
            val user = authRepository.getCurrentEmployee()
            val token = customerRepository.generateToken()
            if (user != null && !_uiState.value.isEditMode) {
                _uiState.update {
                    it.copy(
                        creName = user.employeeName,
                        creUid = user.employeeUid,
                        creEmployeeId = user.employeeId,
                        branchId = user.branchId,
                        branchName = user.branchName,
                        token = token
                    )
                }
            }
        }
    }

    private fun loadExistingRecord(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val visit = customerRepository.getCustomerVisitById(id).firstOrNull()
            if (visit != null) {
                _uiState.update {
                    it.copy(
                        recordId = visit.recordId,
                        isEditMode = true,
                        date = visit.date,
                        storeId = visit.storeId,
                        storeName = visit.storeName,
                        branchId = visit.branchId,
                        branchName = visit.branchName,
                        creName = visit.creName,
                        creUid = visit.creUid,
                        cusName = visit.customerName,
                        mobNo = visit.mobileNumber,
                        purposeOfVisit = visit.reasonForVisit,
                        prePp = visit.prePost,
                        alterNo = visit.altNumber,
                        gmb = visit.gmb,
                        tagging = visit.tagging,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Customer record not found.") }
            }
        }
    }

    // Field Updaters
    fun onDateChanged(value: String) { _uiState.update { it.copy(date = value) } }
    fun onTokenChanged(value: String) { _uiState.update { it.copy(token = value) } }
    fun onCreNameChanged(value: String) { _uiState.update { it.copy(creName = value) } }
    fun onCusNameChanged(value: String) { _uiState.update { it.copy(cusName = value) } }
    fun onMobNoChanged(value: String) { _uiState.update { it.copy(mobNo = value) } }
    fun onPurposeOfVisitChanged(value: String) { _uiState.update { it.copy(purposeOfVisit = value) } }
    fun onPrePpChanged(value: String) { _uiState.update { it.copy(prePp = value) } }
    fun onAlterNoChanged(value: String) { _uiState.update { it.copy(alterNo = value) } }
    fun onSrNoChanged(value: String) { _uiState.update { it.copy(srNo = value) } }
    fun onOperatorChanged(value: String) { _uiState.update { it.copy(operator = value) } }
    fun onGbpChanged(value: String) { _uiState.update { it.copy(gbp = value) } }
    fun onRemarksChanged(value: String) { _uiState.update { it.copy(remarks = value) } }
    fun onHappyCallingChanged(value: String) { _uiState.update { it.copy(happyCalling = value) } }
    fun onDidChanged(value: String) { _uiState.update { it.copy(did = value) } }
    fun onRefferelChanged(value: String) { _uiState.update { it.copy(refferel = value) } }
    fun onGmbChanged(value: String) { _uiState.update { it.copy(gmb = value) } }
    fun onTaggingChanged(value: String) { _uiState.update { it.copy(tagging = value) } }
    fun onStoreChanged(storeId: String, storeName: String) { _uiState.update { it.copy(storeId = storeId, storeName = storeName) } }

    fun saveCustomerRecord() {
        val s = _uiState.value
        if (s.cusName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Customer Name is required.") }
            return
        }
        if (s.mobNo.isBlank() || s.mobNo.length < 10) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid 10-digit Mobile Number.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val currentUser = authRepository.getCurrentEmployee()
            val userUid = currentUser?.employeeUid ?: ""

            val visit = CustomerVisit(
                recordId = s.recordId ?: "",
                storeId = s.storeId,
                storeName = s.storeName,
                branchId = if (s.branchId.isNotBlank()) s.branchId else (currentUser?.branchId ?: ""),
                branchName = if (s.branchName.isNotBlank()) s.branchName else (currentUser?.branchName ?: ""),
                date = s.date,
                creUid = if (s.creUid.isNotBlank()) s.creUid else userUid,
                creName = s.creName,
                customerName = s.cusName.trim(),
                mobileNumber = s.mobNo.trim(),
                prePost = if (s.prePp.equals("PP", ignoreCase = true) || s.prePp.equals("POSTPAID", ignoreCase = true)) "POSTPAID" else "PREPAID",
                altNumber = s.alterNo.trim(),
                reasonForVisit = s.purposeOfVisit,
                gmb = s.gmb,
                tagging = s.tagging,
                createdBy = userUid,
                updatedBy = userUid
            )

            val result = if (s.isEditMode) {
                customerRepository.updateCustomerVisit(visit)
            } else {
                customerRepository.addCustomerVisit(visit)
            }

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSavedSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.localizedMessage) }
                }
            )
        }
    }
}
