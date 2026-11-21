package com.vi.teamconnect.presentation.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.presentation.components.DatePickerTextField
import com.vi.teamconnect.presentation.components.VIDropdownField
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.ViRedPrimary

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val reportTypes = listOf(
        "Daily Customer Visit Report",
        "Weekly Customer Visit Report",
        "Monthly Visit Report",
        "CRE-wise Report",
        "Operator-wise Report",
        "Purpose of Visit Report",
        "Happy Calling Report",
        "PRE/PP Report",
        "Referral Report"
    )

    Scaffold(
        topBar = {
            VIHeader(
                title = "Reports & Analytics",
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            VIDropdownField(
                label = "Select Report Category",
                selectedValue = uiState.selectedReportType,
                options = reportTypes,
                onValueChange = viewModel::onReportTypeChanged
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                DatePickerTextField(
                    label = "From Date",
                    dateValue = uiState.startDate,
                    onDateSelected = viewModel::onStartDateChanged,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                )

                DatePickerTextField(
                    label = "To Date",
                    dateValue = uiState.endDate,
                    onDateSelected = viewModel::onEndDateChanged,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Stats Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Summary Metrics", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBox("Total Visits", uiState.totalCount.toString(), Modifier.weight(1f))
                        MetricBox("PRE", uiState.preCount.toString(), Modifier.weight(1f))
                        MetricBox("PP", uiState.ppCount.toString(), Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Breakdown Sections
            Text(text = "CRE-wise Distribution", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            BreakdownCard(map = uiState.creBreakdown)

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = "Operator-wise Distribution", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            BreakdownCard(map = uiState.operatorBreakdown)

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = "Purpose of Visit Distribution", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            BreakdownCard(map = uiState.purposeBreakdown)

            Spacer(modifier = Modifier.height(24.dp))

            // Export Button preserving all 15 columns
            Button(
                onClick = { viewModel.exportToCsv(context) },
                colors = ButtonDefaults.buttonColors(containerColor = ViRedPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = "Export")
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text("EXPORT EXCEL / CSV (15 COLUMNS)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MetricBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = ViRedPrimary)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun BreakdownCard(map: Map<String, Int>) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (map.isEmpty()) {
                Text(text = "No data available", fontSize = 13.sp, color = Color.Gray)
            } else {
                map.forEach { (key, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = key, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text(text = "$count visits", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ViRedPrimary)
                    }
                }
            }
        }
    }
}
