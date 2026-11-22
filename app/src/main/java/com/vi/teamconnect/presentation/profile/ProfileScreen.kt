package com.vi.teamconnect.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.presentation.components.DetailRow
import com.vi.teamconnect.presentation.components.GoldCard
import com.vi.teamconnect.presentation.components.GoldGradientButton
import com.vi.teamconnect.presentation.components.StatusChip
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.DarkCharcoal
import com.vi.teamconnect.presentation.theme.DeepGold
import com.vi.teamconnect.presentation.theme.PrimaryGold
import com.vi.teamconnect.presentation.theme.SecondaryGrey

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogoutSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val emp = uiState.employee

    Scaffold(
        topBar = {
            VIHeader(
                title = "Employee Profile"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = PrimaryGold,
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
            )

            Text(
                text = emp?.fullName ?: "Employee",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoal
            )
            Text(
                text = "ID: ${emp?.employeeId ?: ""}",
                fontSize = 14.sp,
                color = SecondaryGrey,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (emp != null) {
                StatusChip(text = emp.accountStatus.name)
            }

            Spacer(modifier = Modifier.height(24.dp))

            GoldCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow("Employee ID", emp?.employeeId ?: "-")
                    DetailRow("Full Name", emp?.fullName ?: "-")
                    DetailRow("Phone Number", emp?.phoneNumber ?: "-")
                    DetailRow("Role", emp?.role?.name ?: "-")
                    DetailRow("Account Status", emp?.accountStatus?.name ?: "-")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            GoldGradientButton(
                text = "LOGOUT",
                onClick = {
                    viewModel.logout()
                    onLogoutSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
