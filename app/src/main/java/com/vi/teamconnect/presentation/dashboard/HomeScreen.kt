package com.vi.teamconnect.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.presentation.components.GoldCard
import com.vi.teamconnect.presentation.components.GoldEditCustomerDialog
import com.vi.teamconnect.presentation.components.StatusChip
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.DarkCharcoal
import com.vi.teamconnect.presentation.theme.DeepGold
import com.vi.teamconnect.presentation.theme.GoldButtonGradient
import com.vi.teamconnect.presentation.theme.LightGold
import com.vi.teamconnect.presentation.theme.LightGoldBorder
import com.vi.teamconnect.presentation.theme.PrimaryGold
import com.vi.teamconnect.presentation.theme.SecondaryGrey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddCustomer: () -> Unit,
    onNavigateToCustomerDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var editingVisit by remember { mutableStateOf<CustomerVisit?>(null) }
    val currentDateStr = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date())

    if (editingVisit != null) {
        GoldEditCustomerDialog(
            visit = editingVisit!!,
            onDismiss = { editingVisit = null },
            onSave = { updated ->
                viewModel.updateCustomerVisit(updated)
                editingVisit = null
            }
        )
    }

    Scaffold(
        topBar = {
            VIHeader(
                title = "Employee Dashboard"
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(GoldButtonGradient)
                    .clickable { onNavigateToAddCustomer() }
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Customer",
                        tint = DarkCharcoal
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Customer",
                        color = DarkCharcoal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Header Profile & Branch Card
            item {
                GoldCard(modifier = Modifier.padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Welcome, ${uiState.currentUser?.employeeName?.ifEmpty { "Employee" } ?: "Employee"}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkCharcoal
                                )
                                Text(
                                    text = "ID: ${uiState.currentUser?.employeeId?.ifEmpty { "N/A" } ?: "N/A"}",
                                    fontSize = 13.sp,
                                    color = SecondaryGrey
                                )
                            }
                            Text(
                                text = currentDateStr,
                                fontSize = 12.sp,
                                color = DeepGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightGold.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                                .border(1.dp, LightGoldBorder, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Branch",
                                tint = DeepGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Branch: ${uiState.currentUser?.branchName?.ifEmpty { "Main Branch" } ?: "Main Branch"}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkCharcoal
                            )
                        }
                    }
                }
            }

            // Quick Stats Summary
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GoldStatSummaryCard(
                        title = "Today's Visits",
                        value = uiState.stats.todaysCustomerVisits.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    GoldStatSummaryCard(
                        title = "Total Entries",
                        value = uiState.stats.totalCustomerVisits.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WALK-IN CUSTOMER DETAILS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                }
            }

            // Customer Records List
            if (uiState.recentVisits.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No customer records found for this branch.",
                            color = SecondaryGrey,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(uiState.recentVisits) { visit ->
                    EmployeeCustomerCard(
                        visit = visit,
                        onClick = { onNavigateToCustomerDetail(visit.recordId) },
                        onEditClick = { editingVisit = visit }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun GoldStatSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, LightGoldBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 12.sp, color = SecondaryGrey, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DeepGold)
        }
    }
}

@Composable
fun EmployeeCustomerCard(
    visit: CustomerVisit,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    GoldCard(modifier = Modifier.clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = visit.customerName.ifEmpty { "Customer" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkCharcoal
                    )
                    Text(
                        text = "Mob: ${visit.mobileNumber}",
                        fontSize = 13.sp,
                        color = SecondaryGrey
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(text = visit.prePost)
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Customer",
                            tint = DeepGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Date: ${visit.date}", fontSize = 12.sp, color = SecondaryGrey)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "GMB: ${visit.gmb}", fontSize = 12.sp, color = DeepGold, fontWeight = FontWeight.SemiBold)
                    Text(text = "Tagging: ${visit.tagging}", fontSize = 12.sp, color = DeepGold, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
