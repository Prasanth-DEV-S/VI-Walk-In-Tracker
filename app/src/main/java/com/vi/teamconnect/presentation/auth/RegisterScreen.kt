package com.vi.teamconnect.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.presentation.components.GoldGradientButton
import com.vi.teamconnect.presentation.components.GoldOutlinedTextField
import com.vi.teamconnect.presentation.components.VIDropdownField
import com.vi.teamconnect.presentation.theme.DarkCharcoal
import com.vi.teamconnect.presentation.theme.DeepGold
import com.vi.teamconnect.presentation.theme.LightGold
import com.vi.teamconnect.presentation.theme.LightGoldBorder
import com.vi.teamconnect.presentation.theme.PrimaryBackground
import com.vi.teamconnect.presentation.theme.PrimaryGold
import com.vi.teamconnect.presentation.theme.SecondaryBackground
import com.vi.teamconnect.presentation.theme.SecondaryGrey

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SecondaryBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "VI TEAM CONNECT",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkCharcoal,
                letterSpacing = 1.sp
            )
            Text(
                text = "Employee Registration",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepGold,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryBackground),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = LightGoldBorder, shape = RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.isRegistrationSuccess) {
                        Text(
                            text = "Registration Submitted!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepGold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightGold.copy(alpha = 0.3f), shape = RoundedCornerShape(10.dp))
                                .border(1.dp, LightGoldBorder, shape = RoundedCornerShape(10.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = uiState.successMessage ?: "",
                                fontSize = 14.sp,
                                color = DarkCharcoal,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        GoldGradientButton(
                            text = "BACK TO LOGIN",
                            onClick = onNavigateToLogin,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    } else {
                        GoldOutlinedTextField(
                            value = uiState.employeeId,
                            onValueChange = viewModel::onEmployeeIdChanged,
                            label = "Employee ID (Unique) *",
                            trailingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = PrimaryGold) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        GoldOutlinedTextField(
                            value = uiState.employeeName,
                            onValueChange = viewModel::onEmployeeNameChanged,
                            label = "Full Employee Name *",
                            trailingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryGold) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        GoldOutlinedTextField(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChanged,
                            label = "Email Address (Optional)",
                            trailingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryGold) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        GoldOutlinedTextField(
                            value = uiState.phoneNumber,
                            onValueChange = viewModel::onPhoneNumberChanged,
                            label = "Phone Number (10 digits) *",
                            trailingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryGold) }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        VIDropdownField(
                            label = "Assigned Branch *",
                            selectedValue = uiState.selectedBranch?.branchName ?: "",
                            options = uiState.branches.map { it.branchName },
                            onValueChange = { selectedName ->
                                val branchObj = uiState.branches.firstOrNull { it.branchName == selectedName }
                                if (branchObj != null) {
                                    viewModel.onBranchSelected(branchObj)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = viewModel::onPasswordChanged,
                            label = { Text("Create Password *") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryGold) },
                            trailingIcon = {
                                IconButton(onClick = viewModel::togglePasswordVisibility) {
                                    Icon(
                                        imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle Password",
                                        tint = PrimaryGold
                                    )
                                }
                            },
                            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryGold,
                                unfocusedBorderColor = LightGoldBorder,
                                focusedLabelColor = PrimaryGold,
                                unfocusedLabelColor = SecondaryGrey
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = uiState.confirmPassword,
                            onValueChange = viewModel::onConfirmPasswordChanged,
                            label = { Text("Confirm Password *") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryGold) },
                            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryGold,
                                unfocusedBorderColor = LightGoldBorder,
                                focusedLabelColor = PrimaryGold,
                                unfocusedLabelColor = SecondaryGrey
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (uiState.errorMessage != null) {
                            Text(
                                text = uiState.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        GoldGradientButton(
                            text = "SUBMIT REGISTRATION",
                            onClick = viewModel::register,
                            isLoading = uiState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(onClick = onNavigateToLogin) {
                            Text("Already registered? Login here", color = DeepGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
