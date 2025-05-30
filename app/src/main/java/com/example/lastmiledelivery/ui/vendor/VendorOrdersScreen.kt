package com.example.lastmiledelivery.ui.vendor

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lastmiledelivery.data.models.vendor.Shop
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModelShops
import kotlinx.coroutines.flow.collectLatest

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.ShopCategoryResponse
import com.example.lastmiledelivery.data.models.vendor.ShopRequest
import com.example.lastmiledelivery.viewmodels.common.ShopCategoryViewModel
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModel
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import coil.request.ImageRequest
import com.example.lastmiledelivery.viewmodels.common.StatusViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorOrdersScreen(
    vendorId: Int, viewModel: VendorViewModel = hiltViewModel(), navController: NavController
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

                val filteredOrders = orders.mapNotNull { order ->
                    val matchingSuborders = order.suborders.filter { sub ->
                        (selectedStatus == null || sub.status.uppercase() == selectedStatus) && (selectedPaymentStatus == null || sub.payment_status?.uppercase() == selectedPaymentStatus)
                    }

                    if (matchingSuborders.isNotEmpty()) {
                        // Return a copy of the order with only the matching suborders
                        order.copy(suborders = matchingSuborders)
                    } else {
                        null // Exclude this order entirely
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuborderDetailsScreen(
    vendorId: Int,
    shopId: Int,
    branchId: Int,
    suborderId: Int,
    navController: NavHostController,
    viewModel: VendorViewModel = hiltViewModel()
) {
    // Observe the state of the suborder details and order details
    val suborderDetails by viewModel.suborderDetails.collectAsState()
    val orderDetails by viewModel.orderDetails.collectAsState()
    val isLoading by viewModel.isLoadingSuborderDetails
    val error by viewModel.errors

    // Load suborder details when the screen is launched or the suborderId changes
//    LaunchedEffect(suborderId) {
//        viewModel.loadSuborderDetails(vendorId, shopId, branchId, suborderId)
//    }
    LaunchedEffect(suborderId) {
        viewModel.startSuborderPolling(vendorId, shopId, branchId, suborderId)
    }

    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(refreshKey) {
        viewModel.loadSuborderDetails(vendorId, shopId, branchId, suborderId)
        viewModel.loadVendorOrders(vendorId)
    }

    val response = viewModel.statusUpdateResponse.value
    val statusViewModel: StatusViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        statusViewModel.loadStatuses()
    }

    val statuses = statusViewModel.statuses.value
    val loading = statusViewModel.isLoading.value

    val scrollState = rememberScrollState()
    Scaffold(topBar = {
        TopAppBar(title = { Text("Suborder Details") }, navigationIcon = {
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
                .verticalScroll(scrollState)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Text(
                        text = error ?: "Unknown error",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                suborderDetails != null -> {
                    val details = suborderDetails!!

                    // --- Suborder Header ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Suborder ID: ${details.suborder_id}", fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Status as a colorful chip
                            StatusChip(status = details.status)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "Payment Status: ${details.payment_status}",
                                Modifier.background(color = Color.White),
                                color = Color(0xFF388E3C)
                            )
                            Text("Total Amount: Rs. ${details.total_amount}")
                            Text("Vendor Type: ${details.vendor_type}")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Order Items ---
                    orderDetails?.let { list ->
                        Text("Order Items", fontWeight = FontWeight.Bold)

                        list.forEach { orderDetail ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(orderDetail.item.item_picture).crossfade(true)
                                            .build(),
                                        contentDescription = "Item Image",
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(MaterialTheme.shapes.medium),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .align(Alignment.CenterVertically)
                                    ) {
                                        Text(
                                            orderDetail.item.item_name,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text("Qty: ${orderDetail.quantity}")
                                        Text("Price: Rs. ${orderDetail.order_detail_price}")
                                        Text("Total: Rs. ${orderDetail.order_detail_total}")
                                    }
                                }
                            }
                        }
                    }

                    if (statuses != null) {
                        val currentStatus = "${details.status}"
                        val statusList = statuses.suborderStatuses.values.toList()
                        val currentIndex = statusList.indexOf(currentStatus)

                        Text(
                            text = "Suborder Statuses:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 0.dp, max = 200.dp) // restricts the height
                                .verticalScroll(rememberScrollState())
                                .padding(end = 4.dp) // for scrollbar padding if needed
                        ) {
                            statusList.forEachIndexed { index, status ->
                                val color = when {
                                    index < currentIndex -> Color.Gray
                                    index == currentIndex -> Color.Blue
                                    else -> Color(0xFF81C784)
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = status.replace("_", " ")
                                            .replaceFirstChar { it.uppercase() },
                                        color = color,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }



                        Spacer(modifier = Modifier.height(16.dp))


                        val nextStatus = when (currentStatus.lowercase()) {
                            "pending" -> "in_progress"
                            "in_progress" -> "ready"
                            "picked_up" -> "handover_confirmed" // show button only at picked_up
//                            "in_transit" -> "confirmed_by_vendor"
                            else -> null
                        }
                        val coroutineScope = rememberCoroutineScope()
                        nextStatus?.let {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.updateSuborderStatus(
                                            details.suborder_id, currentStatus
                                        )

                                        delay(3000) // <-- Delay of 2 seconds

                                        refreshKey++ // triggers LaunchedEffect to re-run and fetch data again

                                        statusViewModel.loadStatuses()
                                    }

                                }, modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Update Status to ${
                                    it.replace("_", " ").replaceFirstChar { c -> c.uppercase() }
                                }")
                            }
                        }

                        response?.let {
                            Spacer(modifier = Modifier.height(8.dp))


                            Text(
                                text = it.message,
                                color = if (it.success) Color.Green else Color.Red
                            )
                        }


                        val currentPaymentStatus = details.payment_status?.lowercase()
                        val nextPaymentStatus = when (currentPaymentStatus) {
                            "confirmed_by_deliveryboy" -> "confirmed_by_vendor"
                            else -> null
                        }


                        nextPaymentStatus?.let {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        if (currentPaymentStatus != null) {
                                            viewModel.updatePaymentStatus(
                                                details.suborder_id, currentPaymentStatus
                                            )

                                        }
                                        delay(2000)
                                        refreshKey++ // triggers LaunchedEffect to re-run and fetch data again

                                        statusViewModel.loadStatuses()
                                    }
                                }, modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Update Payment Status to ${
                                    it.replace("_", " ").replaceFirstChar { c -> c.uppercase() }
                                }")
                            }
                        }


                    }

                }
            }


        }
    }
}

@Composable
fun StatusChip(status: String) {
    val backgroundColor = when (status.lowercase()) {
        "pending" -> Color(0xFFFFF3E0)
        "assigned" -> Color(0xFFE3F2FD)
        "picked" -> Color(0xFFE8F5E9)
        "delivered" -> Color(0xFFE0F7FA)
        "cancelled" -> Color(0xFFFFEBEE)
        else -> Color.LightGray
    }

    val textColor = when (status.lowercase()) {
        "pending" -> Color(0xFFEF6C00)
        "assigned" -> Color(0xFF1976D2)
        "picked" -> Color(0xFF388E3C)
        "delivered" -> Color(0xFF00838F)
        "cancelled" -> Color(0xFFD32F2F)
        else -> Color.DarkGray
    }

    Box(
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
