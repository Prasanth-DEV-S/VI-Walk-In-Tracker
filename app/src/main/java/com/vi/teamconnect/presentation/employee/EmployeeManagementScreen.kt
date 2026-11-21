package com.vi.teamconnect.presentation.employee

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.vi.teamconnect.presentation.components.StatusChip
import com.vi.teamconnect.presentation.components.VIHeader
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
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setTab(0) },
                    text = { Text("Pending (${uiState.pendingEmployees.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setTab(1) },
                    text = { Text("All Employees (${uiState.allEmployees.size})", fontWeight = FontWeight.Bold) }
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
                        color = Color.Gray
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
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = employee.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                StatusChip(text = employee.accountStatus.name)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "ID: ${employee.employeeId} | Mob: ${employee.phoneNumber}", fontSize = 13.sp, color = Color.Gray)
            Text(text = "Role: ${employee.role.name}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(10.dp))

            if (employee.accountStatus == AccountStatus.PENDING_APPROVAL) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusActiveGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("APPROVE")
                    }

                    Button(
                        onClick = onReject,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRejectedRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("REJECT")
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onToggleRole,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (employee.role == Role.ADMIN) "Make EMPLOYEE" else "Make ADMIN", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onToggleStatus,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (employee.accountStatus == AccountStatus.ACTIVE) "Suspend" else "Activate", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
