package com.vi.teamconnect.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.presentation.components.DatePickerTextField
import com.vi.teamconnect.presentation.components.GoldGradientButton
import com.vi.teamconnect.presentation.components.GoldOutlinedTextField
import com.vi.teamconnect.presentation.components.VIDropdownField
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.DarkCharcoal
import com.vi.teamconnect.presentation.theme.DeepGold
import com.vi.teamconnect.presentation.theme.PrimaryGold

@Composable
fun AddEditCustomerScreen(
    viewModel: AddEditCustomerViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSavedSuccess) {
        if (uiState.isSavedSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            VIHeader(
                title = if (uiState.isEditMode) "Edit Customer Record" else "Walk-In Customer Form",
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Walk-In Customer Entry",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DeepGold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 1. Store Name
            VIDropdownField(
                label = "1. Store Name *",
                selectedValue = uiState.storeName.ifEmpty { "Vi Store Main" },
                options = listOf("Vi Store Main", "Vi Store CP", "Vi Store Andheri", "Vi Store MG Road", "Vi Store Anna Nagar"),
                onValueChange = { name -> viewModel.onStoreChanged("STORE-${name.takeLast(4)}", name) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Date
            DatePickerTextField(
                label = "2. Date *",
                dateValue = uiState.date,
                onDateSelected = viewModel::onDateChanged
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. CRE Name
            GoldOutlinedTextField(
                value = uiState.creName,
                onValueChange = viewModel::onCreNameChanged,
                label = "3. CRE Name *"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. CUSTOMER NAME
            GoldOutlinedTextField(
                value = uiState.cusName,
                onValueChange = viewModel::onCusNameChanged,
                label = "4. CUSTOMER NAME *"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 5. MOB NUMBER & 7. ALT NUMBER
            Row(modifier = Modifier.fillMaxWidth()) {
                GoldOutlinedTextField(
                    value = uiState.mobNo,
                    onValueChange = viewModel::onMobNoChanged,
                    label = "5. MOB NUMBER *",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                )

                GoldOutlinedTextField(
                    value = uiState.alterNo,
                    onValueChange = viewModel::onAlterNoChanged,
                    label = "7. ALT NUMBER",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 6. PRE/POST
            Text("6. PRE / POST *", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkCharcoal)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.prePp.equals("PREPAID", ignoreCase = true) || uiState.prePp.equals("PRE", ignoreCase = true),
                    onClick = { viewModel.onPrePpChanged("PREPAID") },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                )
                Text("PREPAID", fontSize = 14.sp)

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = uiState.prePp.equals("POSTPAID", ignoreCase = true) || uiState.prePp.equals("PP", ignoreCase = true),
                    onClick = { viewModel.onPrePpChanged("POSTPAID") },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                )
                Text("POSTPAID", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 8. REASON FOR VISIT
            VIDropdownField(
                label = "8. REASON FOR VISIT *",
                selectedValue = uiState.purposeOfVisit,
                options = listOf("SIM Replacement", "SIM Swap", "Bill Payment", "New Connection", "MNP Port-in", "Plan Upgrade", "VAS Inquiry", "Other"),
                onValueChange = viewModel::onPurposeOfVisitChanged
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Remarks / Notes
            GoldOutlinedTextField(
                value = uiState.remarks,
                onValueChange = viewModel::onRemarksChanged,
                label = "Remarks / Notes"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 9. GMB Status
            Text("9. GMB *", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkCharcoal)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.gmb.equals("YES", ignoreCase = true),
                    onClick = { viewModel.onGmbChanged("YES") },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                )
                Text("YES", fontSize = 14.sp)

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = uiState.gmb.equals("NO", ignoreCase = true),
                    onClick = { viewModel.onGmbChanged("NO") },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                )
                Text("NO", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 10. TAGGING Status
            Text("10. TAGGING *", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkCharcoal)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.tagging.equals("YES", ignoreCase = true),
                    onClick = { viewModel.onTaggingChanged("YES") },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                )
                Text("YES", fontSize = 14.sp)

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = uiState.tagging.equals("NO", ignoreCase = true),
                    onClick = { viewModel.onTaggingChanged("NO") },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                )
                Text("NO", fontSize = 14.sp)
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            GoldGradientButton(
                text = if (uiState.isEditMode) "UPDATE RECORD" else "SAVE CUSTOMER RECORD",
                onClick = viewModel::saveCustomerRecord,
                isLoading = uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
