package com.vi.teamconnect.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.vi.teamconnect.presentation.components.DetailRow
import com.vi.teamconnect.presentation.components.GoldCard
import com.vi.teamconnect.presentation.components.GoldGradientButton
import com.vi.teamconnect.presentation.components.StatusChip
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.DarkCharcoal
import com.vi.teamconnect.presentation.theme.DeepGold
import com.vi.teamconnect.presentation.theme.LightGoldBorder
import com.vi.teamconnect.presentation.theme.PrimaryGold
import com.vi.teamconnect.presentation.theme.SecondaryGrey
import com.vi.teamconnect.presentation.theme.SoftGoldHighlight
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
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryGold)
                        }
                        if (isAdmin) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DeepGold)
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
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryGold)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Card
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = visit.cusName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            StatusChip(text = visit.prePp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Token: ${visit.token}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeepGold)
                        Text(text = "CRE: ${visit.creName} (ID: ${visit.creEmployeeId})", fontSize = 13.sp, color = SecondaryGrey)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("All 15 Business Fields", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                Spacer(modifier = Modifier.height(8.dp))

                GoldCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("1. DATE", visit.date)
                        DetailRow("2. STORE NAME", visit.storeName.ifEmpty { "-" })
                        DetailRow("3. BRANCH NAME", visit.branchName.ifEmpty { "-" })
                        DetailRow("4. CRE NAME", visit.creName)
                        DetailRow("5. CUS NAME", visit.customerName)
                        DetailRow("6. MOB NO", visit.mobileNumber)
                        DetailRow("7. REASON FOR VISIT", visit.reasonForVisit)
                        DetailRow("8. PRE/POST", visit.prePost)
                        DetailRow("9. ALTER NO", visit.altNumber.ifEmpty { "-" })
                        DetailRow("10. GMB", visit.gmb)
                        DetailRow("11. TAGGING", visit.tagging)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Audit Details
                Text("Metadata & Audit Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                Spacer(modifier = Modifier.height(8.dp))

                val createdDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(visit.createdAt))
                val updatedDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(visit.updatedAt))

                GoldCard {
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
                    GoldGradientButton(
                        text = "EDIT RECORD",
                        onClick = { onNavigateToEdit(visit.recordId) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    )

                    if (isAdmin) {
                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            border = androidx.compose.foundation.BorderStroke(1.dp, DeepGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("DELETE", color = DeepGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirm Soft Delete", color = DarkCharcoal, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to delete this customer record? Soft-deleted records are hidden from normal lists and can be recovered by Admin.", color = DarkCharcoal) },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteVisit()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepGold)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel", color = SecondaryGrey)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}
