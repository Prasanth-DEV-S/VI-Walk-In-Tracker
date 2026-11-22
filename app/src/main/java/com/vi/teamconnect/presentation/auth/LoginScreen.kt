package com.vi.teamconnect.presentation.auth

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Pin
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
import androidx.compose.runtime.LaunchedEffect
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
import com.vi.teamconnect.presentation.theme.DarkCharcoal
import com.vi.teamconnect.presentation.theme.DeepGold
import com.vi.teamconnect.presentation.theme.LightGold
import com.vi.teamconnect.presentation.theme.LightGoldBorder
import com.vi.teamconnect.presentation.theme.PrimaryBackground
import com.vi.teamconnect.presentation.theme.PrimaryGold
import com.vi.teamconnect.presentation.theme.SecondaryBackground
import com.vi.teamconnect.presentation.theme.SecondaryGrey

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loggedInUser) {
        if (uiState.loggedInUser != null) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SecondaryBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "VI TEAM CONNECT",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkCharcoal,
                letterSpacing = 1.sp
            )

            Text(
                text = "Customer Visit & Management System",
                fontSize = 14.sp,
                color = DeepGold,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
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
                    Text(
                        text = if (uiState.isPasswordLoginMode) "Password Login" else "Phone OTP Login",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (!uiState.isPasswordLoginMode) {
                        if (!uiState.isOtpSent) {
                            GoldOutlinedTextField(
                                value = uiState.phoneNumber,
                                onValueChange = viewModel::onPhoneNumberChanged,
                                label = "Registered Phone Number",
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = PrimaryGold
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            GoldGradientButton(
                                text = "SEND OTP",
                                onClick = viewModel::sendOtp,
                                isLoading = uiState.isLoading,
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            )
                        } else {
                            Text(
                                text = "Enter OTP sent to +91 ${uiState.phoneNumber}",
                                fontSize = 13.sp,
                                color = SecondaryGrey,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            GoldOutlinedTextField(
                                value = uiState.otpCode,
                                onValueChange = viewModel::onOtpCodeChanged,
                                label = "6-Digit OTP Code",
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Pin,
                                        contentDescription = null,
                                        tint = PrimaryGold
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            GoldGradientButton(
                                text = "VERIFY OTP",
                                onClick = viewModel::verifyOtp,
                                isLoading = uiState.isLoading,
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            )

                            TextButton(
                                onClick = viewModel::resetOtpState,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text("Change Phone Number", color = DeepGold, fontSize = 13.sp)
                            }
                        }
                    } else {
                        GoldOutlinedTextField(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChanged,
                            label = "Email / Employee ID",
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = PrimaryGold
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = viewModel::onPasswordChanged,
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryGold) },
                            trailingIcon = {
                                IconButton(onClick = viewModel::togglePasswordVisibility) {
                                    Icon(
                                        imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle Password",
                                        tint = DeepGold
                                    )
                                }
                            },
                            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryGold,
                                unfocusedBorderColor = LightGoldBorder
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onNavigateToForgotPassword) {
                                Text("Forgot Password?", color = DeepGold, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        GoldGradientButton(
                            text = "LOGIN",
                            onClick = viewModel::loginWithPassword,
                            isLoading = uiState.isLoading,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        )
                    }

                    if (uiState.infoMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .background(LightGold.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                                .border(1.dp, LightGoldBorder, shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = uiState.infoMessage!!,
                                color = DarkCharcoal,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = viewModel::toggleLoginMode) {
                        Text(
                            text = if (uiState.isPasswordLoginMode) "Use Phone OTP Login" else "Use Password Login",
                            color = DeepGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("New Employee?", fontSize = 14.sp, color = SecondaryGrey)
                        TextButton(onClick = onNavigateToRegister) {
                            Text("Register Account", color = DarkCharcoal, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
