package com.vi.teamconnect.presentation.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.domain.model.Role
import com.vi.teamconnect.presentation.components.StatusChip
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.ViRedPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CustomerDetailScreen(
    viewModel: CustomerDetailViewModel,
    onNavigateToEdit: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isDeletedSuccess) {
        if (uiState.isDeletedSuccess) {
            onNavigateBack()
        }
    }

    val visit = uiState.visit
    val isAdmin = uiState.currentUser?.role == Role.ADMIN

    Scaffold(
        topBar = {
            VIHeader(
                title = "Customer Visit Details",
                onBackClick = onNavigateBack,
                actions = {
                    if (visit != null) {
                        IconButton(onClick = { onNavigateToEdit(visit.recordId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                        }
                        if (isAdmin) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading || visit == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = visit.cusName, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            StatusChip(text = visit.prePp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Token: ${visit.token}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ViRedPrimary)
                        Text(text = "CRE: ${visit.creName} (ID: ${visit.creEmployeeId})", fontSize = 13.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("All 15 Business Fields", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("1. DATE", visit.date)
                        DetailRow("2. TOKEN", visit.token)
                        DetailRow("3. CRE NAME", visit.creName)
                        DetailRow("4. CUS NAME", visit.cusName)
                        DetailRow("5. MOB NO", visit.mobNo)
                        DetailRow("6. PURPOSE OF VISIT", visit.purposeOfVisit)
                        DetailRow("7. PRE/PP", visit.prePp)
                        DetailRow("8. ALTER NO", visit.alterNo.ifEmpty { "-" })
                        DetailRow("9. SR NO", visit.srNo.ifEmpty { "-" })
                        DetailRow("10. OPERATOR", visit.operator)
                        DetailRow("11. GBP", visit.gbp.ifEmpty { "-" })
                        DetailRow("12. Remarks", visit.remarks.ifEmpty { "No remarks" })
                        DetailRow("13. Happy calling", visit.happyCalling)
                        DetailRow("14. DID", visit.did.ifEmpty { "-" })
                        DetailRow("15. Refferel", visit.refferel.ifEmpty { "-" })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Audit Details
                Text("Metadata & Audit Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                val createdDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(visit.createdAt))
                val updatedDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(visit.updatedAt))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("Created At", createdDateStr)
                        DetailRow("Last Updated At", updatedDateStr)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onNavigateToEdit(visit.recordId) },
                        colors = ButtonDefaults.buttonColors(containerColor = ViRedPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("EDIT RECORD")
                    }

                    if (isAdmin) {
                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("DELETE", color = Color.Red)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirm Soft Delete") },
                text = { Text("Are you sure you want to delete this customer record? Soft-deleted records are hidden from normal lists and can be recovered by Admin.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteVisit()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.weight(0.45f))
        Text(text = value, fontWeight = FontWeight.Normal, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(0.55f))
    }
}
