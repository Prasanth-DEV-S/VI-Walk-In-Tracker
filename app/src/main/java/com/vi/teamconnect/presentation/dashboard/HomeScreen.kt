package com.vi.teamconnect.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.presentation.components.StatusChip
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.ViPurpleHeader
import com.vi.teamconnect.presentation.theme.ViRedPrimary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddCustomer: () -> Unit,
    onNavigateToCustomerDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            VIHeader(
                title = "VI Dashboard"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddCustomer,
                containerColor = ViRedPrimary,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Customer")
                    Spacer(modifier = Modifier.padding(start = 4.dp))
                    Text("Add Customer", fontWeight = FontWeight.Bold)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Welcome, ${uiState.currentUser?.fullName ?: "Employee"}!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ViPurpleHeader
                )
                Text(
                    text = "Role: ${uiState.currentUser?.role?.name ?: "EMPLOYEE"} | ID: ${uiState.currentUser?.employeeId ?: ""}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // 6 Summary Cards
            item {
                Column {
                    Text(
                        text = "Dashboard Overview",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val statsList = listOf(
                        StatCardData("Total Records", uiState.stats.totalCustomerRecords.toString(), Icons.Default.Group, Color(0xFF1976D2)),
                        StatCardData("Today's Visits", uiState.stats.todaysVisits.toString(), Icons.Default.DateRange, Color(0xFF388E3C)),
                        StatCardData("Total CREs", uiState.stats.totalCres.toString(), Icons.Default.People, Color(0xFF7B1FA2)),
                        StatCardData("Happy Calling - Yes", uiState.stats.happyCallingYes.toString(), Icons.Default.CheckCircle, Color(0xFF00796B)),
                        StatCardData("Pending Follow-ups", uiState.stats.pendingFollowups.toString(), Icons.Default.HourglassEmpty, Color(0xFFE65100)),
                        StatCardData("Monthly Records", uiState.stats.monthlyRecords.toString(), Icons.Default.Call, Color(0xFFC2185B))
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        items(statsList) { card ->
                            StatCardItem(card)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Recent Customer Visits",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            if (uiState.recentVisits.isEmpty()) {
                item {
                    Text(
                        text = "No customer visits recorded yet.",
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(uiState.recentVisits) { visit ->
                    RecentCustomerCard(
                        visit = visit,
                        onClick = { onNavigateToCustomerDetail(visit.recordId) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

data class StatCardData(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun StatCardItem(data: StatCardData) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = data.color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = data.title, fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                Icon(imageVector = data.icon, contentDescription = null, tint = data.color)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = data.value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = data.color)
        }
    }
}

@Composable
fun RecentCustomerCard(
    visit: CustomerVisit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = visit.cusName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                StatusChip(text = visit.prePp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Mob: ${visit.mobNo} | CRE: ${visit.creName}", fontSize = 13.sp, color = Color.Gray)
            Text(text = "Purpose: ${visit.purposeOfVisit} | Op: ${visit.operator}", fontSize = 13.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Date: ${visit.date}", fontSize = 12.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Happy Calling: ", fontSize = 12.sp, color = Color.Gray)
                    StatusChip(text = visit.happyCalling)
                }
            }
        }
    }
}
