package com.vi.teamconnect.presentation.auth

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.R
import com.vi.teamconnect.presentation.components.GoldGradientButton
import com.vi.teamconnect.presentation.components.GoldOutlinedTextField
import com.vi.teamconnect.presentation.theme.BorderColor
import com.vi.teamconnect.presentation.theme.DarkText
import com.vi.teamconnect.presentation.theme.PrimaryBackground
import com.vi.teamconnect.presentation.theme.SecondaryBackground
import com.vi.teamconnect.presentation.theme.SecondaryText
import com.vi.teamconnect.presentation.theme.ViDarkRed
import com.vi.teamconnect.presentation.theme.ViLightYellow
import com.vi.teamconnect.presentation.theme.ViRedPrimary

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
            // VI Logo Header
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "VI Logo",
                modifier = Modifier
                    .size(72.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "VI TEAM CONNECT",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ViRedPrimary,
                letterSpacing = 1.sp
            )

            Text(
                text = "Customer Visit & Management System",
                fontSize = 13.sp,
                color = SecondaryText,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp, bottom = 24.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryBackground),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = BorderColor, shape = RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "User Login",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    GoldOutlinedTextField(
                        value = uiState.identifier,
                        onValueChange = viewModel::onIdentifierChanged,
                        label = "Email / Employee ID",
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = ViRedPrimary
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ViRedPrimary) },
                        trailingIcon = {
                            IconButton(onClick = viewModel::togglePasswordVisibility) {
                                Icon(
                                    imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Password",
                                    tint = ViRedPrimary
                                )
                            }
                        },
                        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ViRedPrimary,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = ViRedPrimary,
                            unfocusedLabelColor = SecondaryText
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onNavigateToForgotPassword) {
                            Text("Forgot Password?", color = ViDarkRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    GoldGradientButton(
                        text = "LOGIN",
                        onClick = viewModel::login,
                        isLoading = uiState.isLoading,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )

                    if (uiState.infoMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .background(ViLightYellow, shape = RoundedCornerShape(8.dp))
                                .border(1.dp, BorderColor, shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = uiState.infoMessage!!,
                                color = DarkText,
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("New Employee?", fontSize = 14.sp, color = SecondaryText)
                        TextButton(onClick = onNavigateToRegister) {
                            Text("Register Account", color = ViRedPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
