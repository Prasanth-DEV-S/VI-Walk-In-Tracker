package com.vi.teamconnect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.presentation.theme.DarkCharcoal
import com.vi.teamconnect.presentation.theme.GoldButtonGradient
import com.vi.teamconnect.presentation.theme.LightGoldBorder
import com.vi.teamconnect.presentation.theme.PrimaryGold

@Composable
fun GoldGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.LightGray
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = ButtonDefaults.ContentPadding,
        modifier = modifier
            .background(
                brush = if (enabled && !isLoading) GoldButtonGradient else ButtonDefaults.buttonColors().disabledContainerColor.run {
                    androidx.compose.ui.graphics.SolidColor(Color(0xFFE0E0E0))
                },
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = DarkCharcoal,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color = DarkCharcoal,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GoldOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        readOnly = readOnly,
        isError = isError,
        supportingText = if (isError && errorMessage != null) { { Text(errorMessage) } } else null,
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryGold,
            unfocusedBorderColor = LightGoldBorder,
            focusedLabelColor = PrimaryGold,
            unfocusedLabelColor = DarkCharcoal.copy(alpha = 0.7f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun GoldCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 2.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = LightGoldBorder, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        content()
    }
}

/**
 * Reusable Popup AlertDialog for Editing Customer Visit Records
 */
@Composable
fun GoldEditCustomerDialog(
    visit: CustomerVisit,
    onDismiss: () -> Unit,
    onSave: (CustomerVisit) -> Unit
) {
    var storeName by remember { mutableStateOf(visit.storeName) }
    var customerName by remember { mutableStateOf(visit.customerName) }
    var mobileNumber by remember { mutableStateOf(visit.mobileNumber) }
    var prePost by remember { mutableStateOf(if (visit.prePost.isNotBlank()) visit.prePost else "PREPAID") }
    var altNumber by remember { mutableStateOf(visit.altNumber) }
    var reasonForVisit by remember { mutableStateOf(visit.reasonForVisit) }
    var gmb by remember { mutableStateOf(visit.gmb) }
    var tagging by remember { mutableStateOf(visit.tagging) }
    var date by remember { mutableStateOf(visit.date) }
    var creName by remember { mutableStateOf(visit.creName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Customer Record",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkCharcoal
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                GoldOutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = "Customer Name *"
                )

                Spacer(modifier = Modifier.height(8.dp))

                GoldOutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = "Mobile Number *"
                )

                Spacer(modifier = Modifier.height(8.dp))

                GoldOutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = "Store Name *"
                )

                Spacer(modifier = Modifier.height(8.dp))

                DatePickerTextField(
                    label = "Date *",
                    dateValue = date,
                    onDateSelected = { date = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                GoldOutlinedTextField(
                    value = creName,
                    onValueChange = { creName = it },
                    label = "CRE Name *"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("PRE / POST *", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkCharcoal)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = prePost.equals("PREPAID", ignoreCase = true),
                        onClick = { prePost = "PREPAID" },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                    )
                    Text("PREPAID", fontSize = 14.sp)

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = prePost.equals("POSTPAID", ignoreCase = true),
                        onClick = { prePost = "POSTPAID" },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                    )
                    Text("POSTPAID", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                GoldOutlinedTextField(
                    value = altNumber,
                    onValueChange = { altNumber = it },
                    label = "Alt Number"
                )

                Spacer(modifier = Modifier.height(8.dp))

                GoldOutlinedTextField(
                    value = reasonForVisit,
                    onValueChange = { reasonForVisit = it },
                    label = "Reason for Visit"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("GMB Status *", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkCharcoal)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gmb.equals("YES", ignoreCase = true),
                        onClick = { gmb = "YES" },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                    )
                    Text("YES", fontSize = 14.sp)

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = gmb.equals("NO", ignoreCase = true),
                        onClick = { gmb = "NO" },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                    )
                    Text("NO", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Tagging Status *", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkCharcoal)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = tagging.equals("YES", ignoreCase = true),
                        onClick = { tagging = "YES" },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                    )
                    Text("YES", fontSize = 14.sp)

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = tagging.equals("NO", ignoreCase = true),
                        onClick = { tagging = "NO" },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryGold)
                    )
                    Text("NO", fontSize = 14.sp)
                }
            }
        },
        confirmButton = {
            GoldGradientButton(
                text = "Save Changes",
                onClick = {
                    val updated = visit.copy(
                        storeName = storeName.trim(),
                        customerName = customerName.trim(),
                        mobileNumber = mobileNumber.trim(),
                        prePost = prePost,
                        altNumber = altNumber.trim(),
                        reasonForVisit = reasonForVisit.trim(),
                        gmb = gmb,
                        tagging = tagging,
                        date = date,
                        creName = creName.trim()
                    )
                    onSave(updated)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DarkCharcoal)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
