package com.vi.teamconnect.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.model.SortBy
import com.vi.teamconnect.presentation.components.GoldCard
import com.vi.teamconnect.presentation.components.GoldEditCustomerDialog
import com.vi.teamconnect.presentation.components.GoldGradientButton
import com.vi.teamconnect.presentation.components.StatusChip
import com.vi.teamconnect.presentation.components.VIDropdownField
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.DarkCharcoal
import com.vi.teamconnect.presentation.theme.DeepGold
import com.vi.teamconnect.presentation.theme.GoldButtonGradient
import com.vi.teamconnect.presentation.theme.LightGoldBorder
import com.vi.teamconnect.presentation.theme.PrimaryGold
import com.vi.teamconnect.presentation.theme.SecondaryGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    viewModel: CustomerListViewModel,
    onNavigateToAddCustomer: () -> Unit,
    onNavigateToCustomerDetail: (String) -> Unit
) {
    val filter by viewModel.filter.collectAsState()
    val visits by viewModel.visits.collectAsState()
    val isFilterSheetVisible by viewModel.isFilterSheetVisible.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var editingVisit by remember { mutableStateOf<CustomerVisit?>(null) }

    if (editingVisit != null) {
        GoldEditCustomerDialog(
            visit = editingVisit!!,
            onDismiss = { editingVisit = null },
            onSave = { updated ->
                viewModel.updateCustomerVisit(updated)
                editingVisit = null
            }
        )
    }

    Scaffold(
        topBar = {
            VIHeader(
                title = "Customer Records",
                actions = {
                    IconButton(onClick = { viewModel.toggleFilterSheet(true) }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = PrimaryGold)
                    }
                }
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(GoldButtonGradient)
                    .clickable { onNavigateToAddCustomer() }
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Customer",
                        tint = DarkCharcoal
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Customer",
                        color = DarkCharcoal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = filter.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("Search Name, Mobile, Branch, CRE...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryGold) },
                trailingIcon = {
                    if (filter.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = PrimaryGold)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGold,
                    unfocusedBorderColor = LightGoldBorder,
                    focusedLabelColor = PrimaryGold,
                    unfocusedLabelColor = SecondaryGrey
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Records Summary & Sort Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${visits.size} Records Found",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoal
                )
                TextButton(onClick = { viewModel.toggleFilterSheet(true) }) {
                    Text("Filter / Sort", color = DeepGold, fontWeight = FontWeight.Bold)
                }
            }

            if (visits.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No customer records match your filter criteria.",
                        color = SecondaryGrey
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visits, key = { it.recordId }) { visit ->
                        CustomerVisitCardItem(
                            visit = visit,
                            onClick = { onNavigateToCustomerDetail(visit.recordId) },
                            onEditClick = { editingVisit = visit }
                        )
                    }
                }
            }
        }

        if (isFilterSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleFilterSheet(false) },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Text("Filter & Sort Customer Records", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                    Spacer(modifier = Modifier.height(16.dp))

                    VIDropdownField(
                        label = "Sort By",
                        selectedValue = when(filter.sortBy) {
                            SortBy.LATEST_DATE -> "Latest Date"
                            SortBy.OLDEST_DATE -> "Oldest Date"
                            SortBy.CUSTOMER_NAME -> "Customer Name"
                            SortBy.CRE_NAME -> "CRE Name"
                        },
                        options = listOf("Latest Date", "Oldest Date", "Customer Name", "CRE Name"),
                        onValueChange = { selected ->
                            val s = when(selected) {
                                "Oldest Date" -> SortBy.OLDEST_DATE
                                "Customer Name" -> SortBy.CUSTOMER_NAME
                                "CRE Name" -> SortBy.CRE_NAME
                                else -> SortBy.LATEST_DATE
                            }
                            viewModel.setSortBy(s)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    VIDropdownField(
                        label = "PRE / POST Filter",
                        selectedValue = filter.prePost.ifEmpty { "All" },
                        options = listOf("All", "PREPAID", "POSTPAID"),
                        onValueChange = { selected ->
                            viewModel.updateFilter(filter.copy(prePost = if (selected == "All") "" else selected))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    VIDropdownField(
                        label = "GMB Status",
                        selectedValue = filter.gmb.ifEmpty { "All" },
                        options = listOf("All", "YES", "NO"),
                        onValueChange = { selected ->
                            viewModel.updateFilter(filter.copy(gmb = if (selected == "All") "" else selected))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    VIDropdownField(
                        label = "Tagging Status",
                        selectedValue = filter.tagging.ifEmpty { "All" },
                        options = listOf("All", "YES", "NO"),
                        onValueChange = { selected ->
                            viewModel.updateFilter(filter.copy(tagging = if (selected == "All") "" else selected))
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                viewModel.resetFilter()
                                viewModel.toggleFilterSheet(false)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryGrey)
                        ) {
                            Text("Reset", color = Color.White)
                        }

                        GoldGradientButton(
                            text = "Apply Filters",
                            onClick = { viewModel.toggleFilterSheet(false) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerVisitCardItem(
    visit: CustomerVisit,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    GoldCard(modifier = Modifier.clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = visit.customerName.ifEmpty { "Customer" }, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkCharcoal)
                    Text(text = "Mob: ${visit.mobileNumber}", fontSize = 13.sp, color = SecondaryGrey)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(text = visit.prePost)
                    IconButton(onClick = onEditClick) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = DeepGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Branch: ${visit.branchName.ifEmpty { "Main Branch" }} | Store: ${visit.storeName.ifEmpty { "Vi Store" }}", fontSize = 13.sp, color = SecondaryGrey)
            Text(text = "CRE: ${visit.creName} | Reason: ${visit.reasonForVisit}", fontSize = 13.sp, color = DarkCharcoal.copy(alpha = 0.8f))

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Date: ${visit.date}", fontSize = 12.sp, color = SecondaryGrey)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "GMB: ${visit.gmb}", fontSize = 12.sp, color = DeepGold, fontWeight = FontWeight.SemiBold)
                    Text(text = "Tagging: ${visit.tagging}", fontSize = 12.sp, color = DeepGold, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
