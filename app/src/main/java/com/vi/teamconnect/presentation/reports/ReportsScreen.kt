package com.vi.teamconnect.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.domain.model.DateRangeOption
import com.vi.teamconnect.presentation.components.DatePickerTextField
import com.vi.teamconnect.presentation.components.GoldCard
import com.vi.teamconnect.presentation.components.GoldGradientButton
import com.vi.teamconnect.presentation.components.VIDropdownField
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.DarkCharcoal
import com.vi.teamconnect.presentation.theme.DeepGold
import com.vi.teamconnect.presentation.theme.LightGoldBorder
import com.vi.teamconnect.presentation.theme.SecondaryGrey
import com.vi.teamconnect.presentation.theme.SoftGoldHighlight

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val dateRangeOptions = DateRangeOption.entries.map { it.label }

    Scaffold(
        topBar = {
            VIHeader(
                title = "Performance & Analytics",
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Filter Header
            VIDropdownField(
                label = "Date Range Filter",
                selectedValue = uiState.dateRangeOption.label,
                options = dateRangeOptions,
                onValueChange = { label ->
                    val opt = DateRangeOption.entries.firstOrNull { it.label == label } ?: DateRangeOption.ALL
                    viewModel.onDateRangeChanged(opt)
                }
            )

            if (uiState.dateRangeOption == DateRangeOption.CUSTOM) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    DatePickerTextField(
                        label = "Start Date",
                        dateValue = uiState.startDate,
                        onDateSelected = viewModel::onStartDateChanged,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 6.dp)
                    )

                    DatePickerTextField(
                        label = "End Date",
                        dateValue = uiState.endDate,
                        onDateSelected = viewModel::onEndDateChanged,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Overall Summary Metrics Grid (8 Cards)
            Text(
                text = "Overall Performance Summary",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftGoldHighlight),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightGoldBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBox("Total Visits", uiState.stats.totalCustomerVisits.toString(), Modifier.weight(1f))
                        MetricBox("Today's Visits", uiState.stats.todaysCustomerVisits.toString(), Modifier.weight(1f))
                        MetricBox("Branches", uiState.stats.totalBranches.toString(), Modifier.weight(1f))
                        MetricBox("Employees", uiState.stats.totalEmployees.toString(), Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBox("PREPAID", uiState.stats.prepaidCount.toString(), Modifier.weight(1f))
                        MetricBox("POSTPAID", uiState.stats.postpaidCount.toString(), Modifier.weight(1f))
                        MetricBox("GMB YES", uiState.stats.gmbYesCount.toString(), Modifier.weight(1f))
                        MetricBox("TAGGING YES", uiState.stats.taggingYesCount.toString(), Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Branch-wise Performance Section
            Text(
                text = "Branch-wise Performance",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (uiState.branchPerformances.isEmpty()) {
                GoldCard {
                    Text(
                        text = "No branch statistics recorded yet.",
                        fontSize = 13.sp,
                        color = SecondaryGrey,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                uiState.branchPerformances.forEach { bp ->
                    GoldCard(modifier = Modifier.padding(bottom = 8.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = bp.branchName.ifEmpty { "Branch ${bp.branchId}" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = DarkCharcoal
                                )
                                Text(
                                    text = "${bp.totalVisits} Visits",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = DeepGold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Prepaid: ${bp.prepaidCount} | Postpaid: ${bp.postpaidCount}", fontSize = 12.sp, color = SecondaryGrey)
                                Text(text = "GMB: ${bp.gmbYesCount} | Tagging: ${bp.taggingYesCount}", fontSize = 12.sp, color = SecondaryGrey)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Employee-wise Performance Section
            Text(
                text = "Employee-wise Performance",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (uiState.employeePerformances.isEmpty()) {
                GoldCard {
                    Text(
                        text = "No employee statistics recorded yet.",
                        fontSize = 13.sp,
                        color = SecondaryGrey,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                uiState.employeePerformances.forEach { ep ->
                    GoldCard(modifier = Modifier.padding(bottom = 8.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = ep.employeeName.ifEmpty { "Employee" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = DarkCharcoal
                                    )
                                    Text(
                                        text = "Branch: ${ep.branchName}",
                                        fontSize = 12.sp,
                                        color = SecondaryGrey
                                    )
                                }
                                Text(
                                    text = "${ep.totalEntries} Total (${ep.todaysEntries} Today)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DeepGold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Prepaid: ${ep.prepaidCount} | Postpaid: ${ep.postpaidCount}", fontSize = 12.sp, color = SecondaryGrey)
                                Text(text = "GMB: ${ep.gmbYesCount} | Tagging: ${ep.taggingYesCount}", fontSize = 12.sp, color = SecondaryGrey)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Export Reports Button
            GoldGradientButton(
                text = "EXPORT PERFORMANCE CSV",
                onClick = { viewModel.exportToCsv(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )

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
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = DeepGold)
        Text(text = label, fontSize = 11.sp, color = SecondaryGrey, fontWeight = FontWeight.Medium)
    }
}
