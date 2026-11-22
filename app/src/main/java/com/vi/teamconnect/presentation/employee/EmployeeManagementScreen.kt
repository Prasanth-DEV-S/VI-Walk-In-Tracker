package com.vi.teamconnect.presentation.employee

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.domain.model.AccountStatus
import com.vi.teamconnect.domain.model.Employee
import com.vi.teamconnect.domain.model.Role
import com.vi.teamconnect.presentation.components.GoldCard
import com.vi.teamconnect.presentation.components.StatusChip
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.DarkCharcoal
import com.vi.teamconnect.presentation.theme.DeepGold
import com.vi.teamconnect.presentation.theme.LightGoldBorder
import com.vi.teamconnect.presentation.theme.PrimaryGold
import com.vi.teamconnect.presentation.theme.SecondaryGrey
import com.vi.teamconnect.presentation.theme.StatusActiveGreen
import com.vi.teamconnect.presentation.theme.StatusRejectedRed

@Composable
fun EmployeeManagementScreen(
    viewModel: EmployeeManagementViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        topBar = {
            VIHeader(
                title = "Employee Management",
                onBackClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = PrimaryGold
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setTab(0) },
                    text = {
                        Text(
                            "Pending (${uiState.pendingEmployees.size})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) DeepGold else SecondaryGrey
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setTab(1) },
                    text = {
                        Text(
                            "All Employees (${uiState.allEmployees.size})",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) DeepGold else SecondaryGrey
                        )
                    }
                )
            }

            val list = if (selectedTab == 0) uiState.pendingEmployees else uiState.allEmployees

            if (list.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == 0) "No pending employee registrations." else "No employee records found.",
                        color = SecondaryGrey
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(list, key = { it.uid }) { employee ->
                        EmployeeCardItem(
                            employee = employee,
                            onApprove = { viewModel.approveEmployee(employee.uid) },
                            onReject = { viewModel.rejectEmployee(employee.uid) },
                            onToggleRole = {
                                val nextRole = if (employee.role == Role.ADMIN) Role.EMPLOYEE else Role.ADMIN
                                viewModel.updateRole(employee.uid, nextRole)
                            },
                            onToggleStatus = {
                                val nextStatus = if (employee.accountStatus == AccountStatus.ACTIVE) AccountStatus.SUSPENDED else AccountStatus.ACTIVE
                                viewModel.updateStatus(employee.uid, nextStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeeCardItem(
    employee: Employee,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onToggleRole: () -> Unit,
    onToggleStatus: () -> Unit
) {
    GoldCard {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = employee.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkCharcoal)
                StatusChip(text = employee.accountStatus.name)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "ID: ${employee.employeeId} | Mob: ${employee.phoneNumber}", fontSize = 13.sp, color = SecondaryGrey)
            Text(text = "Role: ${employee.role.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeepGold)

            Spacer(modifier = Modifier.height(10.dp))

            if (employee.accountStatus == AccountStatus.PENDING_APPROVAL) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusActiveGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("APPROVE", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = onReject,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRejectedRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("REJECT", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onToggleRole,
                        border = androidx.compose.foundation.BorderStroke(1.dp, LightGoldBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            if (employee.role == Role.ADMIN) "Make EMPLOYEE" else "Make ADMIN",
                            fontSize = 12.sp,
                            color = DeepGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onToggleStatus,
                        border = androidx.compose.foundation.BorderStroke(1.dp, LightGoldBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            if (employee.accountStatus == AccountStatus.ACTIVE) "Suspend" else "Activate",
                            fontSize = 12.sp,
                            color = DarkCharcoal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
