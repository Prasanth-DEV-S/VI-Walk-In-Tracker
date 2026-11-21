package com.vi.teamconnect.presentation.customer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.presentation.components.DatePickerTextField
import com.vi.teamconnect.presentation.components.VIDropdownField
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.ViRedPrimary

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
                title = if (uiState.isEditMode) "Edit Customer Record" else "Add Customer Record",
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "15 Field Customer Visit Form",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 1. DATE
            DatePickerTextField(
                label = "1. DATE",
                dateValue = uiState.date,
                onDateSelected = viewModel::onDateChanged
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. TOKEN & 3. CRE NAME
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.token,
                    onValueChange = viewModel::onTokenChanged,
                    label = { Text("2. TOKEN") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                )

                OutlinedTextField(
                    value = uiState.creName,
                    onValueChange = viewModel::onCreNameChanged,
                    label = { Text("3. CRE NAME") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. CUS NAME
            OutlinedTextField(
                value = uiState.cusName,
                onValueChange = viewModel::onCusNameChanged,
                label = { Text("4. CUS NAME *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 5. MOB NO & 8. ALTER NO
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.mobNo,
                    onValueChange = viewModel::onMobNoChanged,
                    label = { Text("5. MOB NO *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                )

                OutlinedTextField(
                    value = uiState.alterNo,
                    onValueChange = viewModel::onAlterNoChanged,
                    label = { Text("8. ALTER NO") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 6. PURPOSE OF VISIT
            VIDropdownField(
                label = "6. PURPOSE OF VISIT",
                selectedValue = uiState.purposeOfVisit,
                options = uiState.settings.purposeOptions,
                onValueChange = viewModel::onPurposeOfVisitChanged
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 7. PRE/PP & 10. OPERATOR
            Row(modifier = Modifier.fillMaxWidth()) {
                VIDropdownField(
                    label = "7. PRE/PP",
                    selectedValue = uiState.prePp,
                    options = uiState.settings.prePpOptions,
                    onValueChange = viewModel::onPrePpChanged,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                )

                VIDropdownField(
                    label = "10. OPERATOR",
                    selectedValue = uiState.operator,
                    options = uiState.settings.operatorOptions,
                    onValueChange = viewModel::onOperatorChanged,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 9. SR NO & 11. GBP
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.srNo,
                    onValueChange = viewModel::onSrNoChanged,
                    label = { Text("9. SR NO") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                )

                OutlinedTextField(
                    value = uiState.gbp,
                    onValueChange = viewModel::onGbpChanged,
                    label = { Text("11. GBP") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 13. Happy calling & 14. DID
            Row(modifier = Modifier.fillMaxWidth()) {
                VIDropdownField(
                    label = "13. Happy calling",
                    selectedValue = uiState.happyCalling,
                    options = uiState.settings.happyCallingOptions,
                    onValueChange = viewModel::onHappyCallingChanged,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                )

                OutlinedTextField(
                    value = uiState.did,
                    onValueChange = viewModel::onDidChanged,
                    label = { Text("14. DID") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 15. Refferel
            VIDropdownField(
                label = "15. Refferel",
                selectedValue = uiState.refferel,
                options = uiState.settings.referralOptions,
                onValueChange = viewModel::onRefferelChanged
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 12. Remarks
            OutlinedTextField(
                value = uiState.remarks,
                onValueChange = viewModel::onRemarksChanged,
                label = { Text("12. Remarks") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = viewModel::saveCustomerRecord,
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = ViRedPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.padding(4.dp))
                } else {
                    Text(
                        text = if (uiState.isEditMode) "UPDATE RECORD" else "SAVE CUSTOMER RECORD",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
