package com.example.lastmiledelivery.ui.vendor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModel


@Composable
fun VendorSummaryScreen(vendorId: Int, viewModel: VendorViewModel = hiltViewModel()) {
    val summaryResult by viewModel.summary.observeAsState()

    LaunchedEffect(vendorId) {
        viewModel.loadSummary(vendorId)
    }

    summaryResult?.let { result ->
        result.fold(
            onSuccess = { summary ->

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
//                        .padding(16.dp),
                    , verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SummaryBox(
                            "Total Shops",
                            summary.totalShops.toString(),
                            Icons.Default.Store
                        )
                    }
                    item {
                        SummaryBox(
                            "Branches",
                            summary.totalBranches.toString(),
                            Icons.Default.LocationCity
                        )
                    }
                    item {
                        SummaryBox(
                            "Approved Branches",
                            summary.totalApprovedBranches.toString(),
                            Icons.Default.CheckCircle
                        )
                    }
                    item {
                        SummaryBox(
                            "Suborders",
                            summary.totalSuborders.toString(),
                            Icons.Default.List
                        )
                    }
                    item {
                        SummaryBox(
                            "Delivered",
                            summary.deliveredSuborders.toString(),
                            Icons.Default.LocalShipping
                        )
                    }
                    item {
                        SummaryBox(
                            "Pending",
                            summary.pendingSuborders.toString(),
                            Icons.Default.Pending
                        )
                    }
//                    item { SummaryBox("Orders", summary.totalOrders.toString(), Icons.Default.Receipt) }
                    item {
                        SummaryBox(
                            "Revenue",
                            "Rs. ${summary.totalRevenue}",
                            Icons.Default.AttachMoney
                        )
                    }
                    item {
                        SummaryBox(
                            "Connected Organizations",
                            summary.totalLinkedOrganizations.toString(),
                            Icons.Default.Group
                        )
                    }
//                    item { SummaryBox("Avg Revenue", "Rs. ${summary.avgRevenuePerOrder}", Icons.Default.Money) }


                }

            },
            onFailure = {
                Text("Error loading summary: ${it.message}", color = Color.Red)
            }
        )
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun SummaryBox(title: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(155.dp), // Optional: Set a consistent height
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the card size
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFFEC407A),
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
