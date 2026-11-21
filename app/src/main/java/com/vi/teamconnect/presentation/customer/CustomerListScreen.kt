package com.vi.teamconnect.presentation.customer

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.model.SortBy
import com.vi.teamconnect.presentation.components.StatusChip
import com.vi.teamconnect.presentation.components.VIDropdownField
import com.vi.teamconnect.presentation.components.VIHeader
import com.vi.teamconnect.presentation.theme.ViPurpleHeader
import com.vi.teamconnect.presentation.theme.ViRedPrimary

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

    Scaffold(
        topBar = {
            VIHeader(
                title = "Customer Records",
                actions = {
                    IconButton(onClick = { viewModel.toggleFilterSheet(true) }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddCustomer,
                containerColor = ViRedPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = filter.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("Search by Name, Mob, Token, SR No, CRE...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (filter.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Active Filters Summary Chip bar
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
                    color = ViPurpleHeader
                )
                TextButton(onClick = { viewModel.toggleFilterSheet(true) }) {
                    Text("Sort / Filter", color = ViRedPrimary)
                }
            }

            if (visits.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No customer records match your filter criteria.",
                        color = Color.Gray
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
                            onClick = { onNavigateToCustomerDetail(visit.recordId) }
                        )
                    }
                }
            }
        }

        if (isFilterSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleFilterSheet(false) },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Text("Filter & Sort Options", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ViPurpleHeader)
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
                        label = "PRE / PP",
                        selectedValue = filter.prePp.ifEmpty { "All" },
                        options = listOf("All", "PRE", "PP"),
                        onValueChange = { selected ->
                            viewModel.updateFilter(filter.copy(prePp = if (selected == "All") "" else selected))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    VIDropdownField(
                        label = "Operator",
                        selectedValue = filter.operator.ifEmpty { "All" },
                        options = listOf("All", "Vi", "Airtel", "Jio", "BSNL", "Other"),
                        onValueChange = { selected ->
                            viewModel.updateFilter(filter.copy(operator = if (selected == "All") "" else selected))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    VIDropdownField(
                        label = "Happy Calling Status",
                        selectedValue = filter.happyCalling.ifEmpty { "All" },
                        options = listOf("All", "Yes", "No", "Pending", "Not Applicable"),
                        onValueChange = { selected ->
                            viewModel.updateFilter(filter.copy(happyCalling = if (selected == "All") "" else selected))
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("Reset")
                        }

                        Button(
                            onClick = { viewModel.toggleFilterSheet(false) },
                            colors = ButtonDefaults.buttonColors(containerColor = ViRedPrimary)
                        ) {
                            Text("Apply Filters")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerVisitCardItem(
    visit: CustomerVisit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = visit.cusName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                StatusChip(text = visit.prePp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Token: ${visit.token} | Mob: ${visit.mobNo}", fontSize = 13.sp, color = Color.Gray)
            Text(text = "CRE: ${visit.creName} | Purpose: ${visit.purposeOfVisit}", fontSize = 13.sp, color = Color.DarkGray)
            if (visit.srNo.isNotBlank()) {
                Text(text = "SR No: ${visit.srNo}", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Date: ${visit.date}", fontSize = 12.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Happy Calling: ", fontSize = 12.sp, color = Color.Gray)
                    StatusChip(text = visit.happyCalling)
                }
            }
        }
    }
}
