package com.vi.teamconnect.presentation.settings

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.presentation.components.DetailRow
import com.vi.teamconnect.presentation.components.GoldCard
import com.vi.teamconnect.presentation.components.GoldGradientButton
import com.vi.teamconnect.presentation.components.GoldOutlinedTextField
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.DarkCharcoal
import com.vi.teamconnect.presentation.theme.DeepGold
import com.vi.teamconnect.presentation.theme.LightGoldBorder
import com.vi.teamconnect.presentation.theme.SecondaryGrey
import com.vi.teamconnect.presentation.theme.SoftGoldHighlight

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.appSettings.collectAsState()
    val stores by viewModel.stores.collectAsState()
    var showAddStoreDialog by remember { mutableStateOf(false) }
    var newStoreName by remember { mutableStateOf("") }
    var newBranchName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            VIHeader(
                title = "App Settings & Store Config",
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
            // Admin Add Store Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Store Name Management",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                    Text(
                        text = "Admin can add and configure store locations",
                        fontSize = 13.sp,
                        color = DeepGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            GoldCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Configured Stores (${stores.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DarkCharcoal
                        )
                        GoldGradientButton(
                            text = "+ Add Store",
                            onClick = { showAddStoreDialog = true },
                            modifier = Modifier.height(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (stores.isEmpty()) {
                        Text(text = "No stores configured yet.", fontSize = 13.sp, color = SecondaryGrey)
                    } else {
                        stores.forEach { store ->
                            DetailRow(label = store.storeName, value = store.branchName)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Configurable Field Options",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal
            )
            Text(
                text = "Dynamic values synced from Cloud Firestore",
                fontSize = 13.sp,
                color = DeepGold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GoldCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow("Purpose Options", settings.purposeOptions.joinToString(", "))
                    DetailRow("Operator Options", settings.operatorOptions.joinToString(", "))
                    DetailRow("PRE/PP Options", settings.prePpOptions.joinToString(", "))
                    DetailRow("Happy Calling Options", settings.happyCallingOptions.joinToString(", "))
                    DetailRow("Referral Options", settings.referralOptions.joinToString(", "))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftGoldHighlight),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightGoldBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Offline Persistence Engine", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cloud Firestore automatic local caching is enabled. Reads and queued writes are automatically persisted offline.",
                        fontSize = 13.sp,
                        color = SecondaryGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showAddStoreDialog) {
            AlertDialog(
                onDismissRequest = { showAddStoreDialog = false },
                title = { Text("Add New Store Name", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkCharcoal) },
                text = {
                    Column {
                        GoldOutlinedTextField(
                            value = newStoreName,
                            onValueChange = { newStoreName = it },
                            label = "Store Name *"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        GoldOutlinedTextField(
                            value = newBranchName,
                            onValueChange = { newBranchName = it },
                            label = "Branch Name (Optional)"
                        )
                    }
                },
                confirmButton = {
                    GoldGradientButton(
                        text = "Save Store",
                        onClick = {
                            if (newStoreName.isNotBlank()) {
                                viewModel.addStore(newStoreName, newBranchName)
                                newStoreName = ""
                                newBranchName = ""
                                showAddStoreDialog = false
                            }
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showAddStoreDialog = false }) {
                        Text("Cancel", color = SecondaryGrey)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}
