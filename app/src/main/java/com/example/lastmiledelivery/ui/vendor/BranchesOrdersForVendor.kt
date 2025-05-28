package com.example.lastmiledelivery.ui.vendor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorBranchOrdersScreen(
    vendorId: Int,branchId: Int, viewModel: VendorViewModel = hiltViewModel(), navController: NavController
) {
    val orders by viewModel.orders
    val isLoading by viewModel.isLoadingOrder
    val error by viewModel.error

    // State for filters
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var selectedPaymentStatus by remember { mutableStateOf<String?>(null) }

    // Get unique statuses and payment statuses from suborders
    val allSuborders = orders.flatMap { it.suborders }
    val uniqueStatuses = allSuborders.map { it.status.uppercase() }.toSet()
    val uniquePaymentStatuses = allSuborders.mapNotNull { it.payment_status?.uppercase() }.toSet()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadVendorOrders(vendorId)
    }


    val cardBgColor = Color(0xFFFDFDFD)
    val suborderCardColor = Color(0xFFF0F4F8)


    Scaffold(topBar = {
        TopAppBar(title = { Text("Vendor Orders") }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Text(
                    text = error ?: "Unknown error",
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                // Status Filter Buttons
                if (uniqueStatuses.isNotEmpty()) {
                    Text("Filter by Suborder Status:", fontWeight = FontWeight.Bold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        item {
                            FilterButton(label = "All",
                                selected = selectedStatus == null,
                                onClick = { selectedStatus = null })
                        }
                        items(uniqueStatuses.toList()) { status ->
                            FilterButton(label = status,
                                selected = selectedStatus == status,
                                onClick = {
                                    selectedStatus = if (selectedStatus == status) null else status
                                })
                        }
                    }
                }

                // Payment Status Filter Buttons
                if (uniquePaymentStatuses.isNotEmpty()) {
                    Text("Filter by Payment Status:", fontWeight = FontWeight.Bold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        item {
                            FilterButton(label = "All",
                                selected = selectedPaymentStatus == null,
                                onClick = { selectedPaymentStatus = null })
                        }
                        items(uniquePaymentStatuses.toList()) { status ->
                            FilterButton(label = status,
                                selected = selectedPaymentStatus == status,
                                onClick = {
                                    selectedPaymentStatus =
                                        if (selectedPaymentStatus == status) null else status
                                })
                        }
                    }
                }

//                val filteredOrders = orders.mapNotNull { order ->
//                    val matchingSuborders = order.suborders.filter { sub ->
//                        (selectedStatus == null || sub.status.uppercase() == selectedStatus) && (selectedPaymentStatus == null || sub.payment_status?.uppercase() == selectedPaymentStatus)
//                    }
//
//                    if (matchingSuborders.isNotEmpty()) {
//                        // Return a copy of the order with only the matching suborders
//                        order.copy(suborders = matchingSuborders)
//                    } else {
//                        null // Exclude this order entirely
//                    }
//                }
                val filteredOrders = orders.mapNotNull { order ->
                    val matchingSuborders = order.suborders.filter { sub ->
                        sub.branch_id == branchId && // ✅ new condition
                                (selectedStatus == null || sub.status.uppercase() == selectedStatus) &&
                                (selectedPaymentStatus == null || sub.payment_status?.uppercase() == selectedPaymentStatus)
                    }

                    if (matchingSuborders.isNotEmpty()) {
                        order.copy(suborders = matchingSuborders)
                    } else {
                        null
                    }
                }

                if (filteredOrders.isEmpty()) {
                    Text("No orders available", style = MaterialTheme.typography.bodyLarge)
                } else {
//
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        items(filteredOrders) { order ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "📅 Date: ${order.order_date}",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Customer Info
                                    Text(
                                        "👤 Customer", fontWeight = FontWeight.Bold, fontSize = 16.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(
                                            model = order.customer.picture,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                order.customer.name, fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                order.customer.phone_no,
                                                fontSize = 13.sp,
                                                color = Color.Gray
                                            )
                                            Text(
                                                "${order.customer.address.street} (${order.customer.address.type})",
                                                fontSize = 13.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Suborders
                                    Text(
                                        "🧾 Suborders",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    order.suborders.forEach { sub ->
                                        val statusColor = when (sub.status.lowercase()) {
                                            "pending" -> Color(0xFFFFA726)
                                            "in_transit" -> Color(0xFF42A5F5)
                                            "delivered" -> Color(0xFF66BB6A)
                                            else -> Color.DarkGray
                                        }

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp)
                                                .clickable {
                                                    navController.navigate(
                                                        "vendor_suborder_details/${vendorId}/${sub.shop_id}/${sub.branch_id}/${sub.suborder_id}"
                                                    )
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(
                                                    0xFFF0F4F8
                                                )
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    val suborderLine = buildString {
                                                        append("ID: ${sub.suborder_id}")
                                                        sub.vendor_order_id?.let {
                                                            append(" | API: $it")
                                                        }
                                                    }

                                                    Text(
                                                        suborderLine,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        "Status: ${sub.status.uppercase()}",
                                                        color = statusColor
                                                    )
                                                    sub.payment_status?.let {
                                                        Text(
                                                            "Payment: ${it.uppercase()}",
                                                            color = Color(0xFF757575)
                                                        )
                                                    }
                                                    Text(
                                                        "Total: Rs. ${sub.total}",
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        "Vendor Type: ${sub.vendor_type}",
                                                        fontSize = 13.sp,
                                                        color = Color.DarkGray
                                                    )
                                                }

                                                Icon(
                                                    imageVector = Icons.Filled.ArrowForward,
                                                    contentDescription = "Details",
                                                    tint = Color.Gray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                }

            }
        }
    }
}

@Composable
private fun FilterButton(label: String, selected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (selected) Color(0xFFFF69B4) else Color.White
    val contentColor = if (selected) Color.White else Color.Black
    val borderColor = Color.Black

    OutlinedButton(
        onClick = onClick, colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor, contentColor = contentColor
        ), border = BorderStroke(1.dp, borderColor), shape = RoundedCornerShape(24.dp)
    ) {
        Text(label)
    }
}
