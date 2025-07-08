package com.example.lastmiledelivery.ui.deliveryboy

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.deliveryboy.ReadySuborder
import com.example.lastmiledelivery.ui.vendor.StatusChip
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.admin.LmdViewModel
import com.example.lastmiledelivery.viewmodels.deliveryboy.DeliveryBoyViewModel
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadySubordersScreen(
    deliveryBoyID: Int,
    lmdUserID: Int,
    navController: NavHostController,
    viewModel: DeliveryBoyViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    lmdViewModel: LmdViewModel = hiltViewModel()
) {

    val settings = lmdViewModel.settings

    LaunchedEffect(Unit) {
        lmdViewModel.fetchSettings()
    }


    val user = remember { authViewModel.getUserDetails() }
    val suborders = viewModel.readySuborders
    val isLoading = viewModel.isLoadingReadySuborders
    val error = viewModel.errorMessageReadySuborders

    val vehicles = viewModel.vehiclesState

    LaunchedEffect(Unit) {
        viewModel.loadVehicles(deliveryBoyID)
    }

    val response = viewModel.acceptOrderResponse.value
    val loading = viewModel.loading.value

    LaunchedEffect(Unit) {
        viewModel.fetchReadySuborders(deliveryBoyId = lmdUserID) // Replace with actual ID
    }

    val isTestUser = remember {
        val normalizedName = user.name.replace("_", "", ignoreCase = true)
            .replace(" ", "", ignoreCase = true)
            .lowercase()
        normalizedName.startsWith("testdeliveryboy")
    }

//    val groupedOrders = remember(suborders, settings) {
//        if (settings != null) {
//            viewModel.groupSubordersByCustomerWithinRadius(suborders, settings.pickup_radius_km)
//        } else emptyList()
//    }
    val groupedOrders = remember(suborders, settings, isTestUser) {
        if (settings != null) {
            // 1. Filter suborders first by test user logic
            val filteredSuborders = suborders.filter { suborder ->
                val customerNameNormalized = suborder.customer.name
                    .replace("_", "", ignoreCase = true)
                    .replace(" ", "", ignoreCase = true)
                    .lowercase()

                val isTestCustomer = customerNameNormalized.contains("testcustomer")

                // Only allow test delivery boys to see test customers' orders and vice versa
                (isTestUser && isTestCustomer) || (!isTestUser && !isTestCustomer)
            }

            // 2. Then group them
            viewModel.groupSubordersByCustomerWithinRadius(filteredSuborders, settings.pickup_radius_km)
        } else emptyList()
    }


    // State for dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedSuborder: ReadySuborder? by remember { mutableStateOf(null) }

    // Vendor ViewModel states
    val suborderDetails by vendorViewModel.suborderDetails.collectAsState()
    val orderDetails by vendorViewModel.orderDetails.collectAsState()
    val isLoadingVendor by vendorViewModel.isLoadingSuborderDetails
    val errorVendor by vendorViewModel.errors

    // Fetch details when selectedSuborder changes
    LaunchedEffect(selectedSuborder) {
        selectedSuborder?.let {
            vendorViewModel.loadSuborderDetails(
                it.vendor_ID,
                it.shop_ID,
                it.branch_ID,
                it.suborder_id
            )
        }
    }


    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.startAutoRefreshReadyOrders(lmdUserID)
                }

                Lifecycle.Event.ON_STOP -> {
                    viewModel.stopAutoRefreshReadyOrders()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopAutoRefreshReadyOrders()
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.pink)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (isLoading) {
//                CircularProgressIndicator()
            } else if (error != null) {
                Text(text = "Error: $error", color = Color.Red)

            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    settings?.let {
                        Text(
                            "Pickup Radius (km)  ${it.pickup_radius_km}",
                            modifier = Modifier.padding(8.dp)
                        )
                    } ?: Text("Loading...", modifier = Modifier.padding(8.dp))
                }
                Divider()

                LazyColumn {
                    items(groupedOrders) { group ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Customer: ${group.customerName}",
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Orders: ${group.suborders.size}")

                                group.suborders.forEachIndexed { index, suborder ->
                                    val pickup = suborder.shop.branch.pickup_location
                                    val delivery = suborder.customer.delivery_address

                                    val pickupLat = pickup.latitude.toDoubleOrNull() ?: 0.0
                                    val pickupLng = pickup.longitude.toDoubleOrNull() ?: 0.0
                                    val deliveryLat = delivery.latitude
                                    val deliveryLng = delivery.longitude

                                    val deliveryDistance = calculateDistanceKm(pickupLat, pickupLng, deliveryLat, deliveryLng)

                                    val reference = group.suborders.first()
                                    val refLat = reference.shop.branch.pickup_location.latitude.toDoubleOrNull() ?: 0.0
                                    val refLng = reference.shop.branch.pickup_location.longitude.toDoubleOrNull() ?: 0.0

                                    val pickupToPickupDistance = if (index == 0) 0.0 else calculateDistanceKm(refLat, refLng, pickupLat, pickupLng)

                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text(
                                            text = "Suborder: ${suborder.suborder_id}",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedSuborder = suborder
                                                    showDialog = true
                                                }
                                                .padding(vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        if (index == 0) {
                                            Text("First Pickup Location (Reference)")
                                        }

                                        Text("Pickup → Delivery: ${"%.2f".format(deliveryDistance)} km")
                                        Text("Distance from First Pickup: ${"%.2f".format(pickupToPickupDistance)} km")
                                    }
                                }


                                Spacer(Modifier.height(8.dp))
                                Button(onClick = {
                                    if (vehicles != null) {
                                        viewModel.acceptGroupedOrders(
                                            group,
                                            deliveryBoyID,
                                            vehicles
                                        )
                                    }
                                }) {
                                    Text("Accept Grouped Orders")
                                }
                            }
                        }
                    }
                }

            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Order Details", fontWeight = FontWeight.Bold) },
                    text = {
                        // Make the dialog content scrollable
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp, max = 500.dp) // Set dialog height limit
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                when {
                                    isLoadingVendor -> {
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(24.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator()
                                            }
                                        }
                                    }

                                    errorVendor != null -> {
                                        item {
                                            Text(
                                                text = errorVendor ?: "Unknown error",
                                                color = Color.Red,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }

                                    suborderDetails != null -> {
                                        item {
                                            val details = suborderDetails!!
                                            // Suborder Header
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color(
                                                        0xFFF7F7F7
                                                    )
                                                ),
                                                elevation = CardDefaults.cardElevation(2.dp)
                                            ) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Text(
                                                        "Suborder ID: ${details.suborder_id}",
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    StatusChip(status = details.status)
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text("Payment Status: ${details.payment_status}")
                                                    Text("Total Amount: Rs. ${details.total_amount}")
                                                    Text("Vendor Type: ${details.vendor_type}")
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                "Order Items",
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(
                                                    top = 8.dp,
                                                    bottom = 8.dp
                                                )
                                            )
                                        }

                                        // Order items list
                                        orderDetails?.forEach { orderDetail ->
                                            item {
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 6.dp),
                                                    elevation = CardDefaults.cardElevation(1.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(12.dp)
                                                    ) {
                                                        AsyncImage(
                                                            model = ImageRequest.Builder(
                                                                LocalContext.current
                                                            )
                                                                .data(orderDetail.item.item_picture)
                                                                .crossfade(true)
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
                                    }

                                    else -> {
                                        item {
                                            Text("Loading...")
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
//                        Button(onClick = {
//                            selectedSuborder?.let {
//                                viewModel.acceptOrder(
//                                    deliveryBoyID,
//                                    it.suborder_id
//                                )
//                            }
//                            showDialog = false
//                        }) {
//                            Text("Accept")
//                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            val context = LocalContext.current

            LaunchedEffect(response) {
                response?.let {
                    if (it.message != null) {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        viewModel.fetchReadySuborders(deliveryBoyId = lmdUserID)
                    } else if (it.error != null) {
                        Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
                    }
                    viewModel.clearAcceptOrderResponse()
                }
            }


        }
    }
}

fun String?.toSafeDouble(): Double {
    return this?.trim()?.toDoubleOrNull() ?: 0.0
}

//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////

// BELWO CODE IS FOR ONE BY ONE ACCEPT ORDER BUT WE HAVE SHOW Radius on top
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ReadySubordersScreen(
//    deliveryBoyID: Int,
//    lmdUserID: Int,
//    navController: NavHostController,
//    viewModel: DeliveryBoyViewModel = hiltViewModel(),
//    vendorViewModel: VendorViewModel = hiltViewModel(),
//    authViewModel: AuthViewModel = hiltViewModel(),
//    lmdViewModel: LmdViewModel = hiltViewModel()
//) {
//
//    val settings = lmdViewModel.settings
//
//    LaunchedEffect(Unit) {
//        lmdViewModel.fetchSettings()
//    }
//
//    val user = remember { authViewModel.getUserDetails() }
//    val suborders = viewModel.readySuborders
//    val isLoading = viewModel.isLoadingReadySuborders
//    val error = viewModel.errorMessageReadySuborders
//
//    val vehicles = viewModel.vehiclesState
//
//    LaunchedEffect(Unit) {
//        viewModel.loadVehicles(deliveryBoyID)
//    }
//
//    val response = viewModel.acceptOrderResponse.value
//    val loading = viewModel.loading.value
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchReadySuborders(deliveryBoyId = lmdUserID) // Replace with actual ID
//    }
//
//
//    // State for dialog
//    var showDialog by remember { mutableStateOf(false) }
//    var selectedSuborder: ReadySuborder? by remember { mutableStateOf(null) }
//
//    // Vendor ViewModel states
//    val suborderDetails by vendorViewModel.suborderDetails.collectAsState()
//    val orderDetails by vendorViewModel.orderDetails.collectAsState()
//    val isLoadingVendor by vendorViewModel.isLoadingSuborderDetails
//    val errorVendor by vendorViewModel.errors
//
//    // Fetch details when selectedSuborder changes
//    LaunchedEffect(selectedSuborder) {
//        selectedSuborder?.let {
//            vendorViewModel.loadSuborderDetails(
//                it.vendor_ID,
//                it.shop_ID,
//                it.branch_ID,
//                it.suborder_id
//            )
//        }
//    }
//
//
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    DisposableEffect(Unit) {
//        val observer = LifecycleEventObserver { _, event ->
//            when (event) {
//                Lifecycle.Event.ON_START -> {
//                    viewModel.startAutoRefreshReadyOrders(lmdUserID)
//                }
//
//                Lifecycle.Event.ON_STOP -> {
//                    viewModel.stopAutoRefreshReadyOrders()
//                }
//
//                else -> {}
//            }
//        }
//
//        lifecycleOwner.lifecycle.addObserver(observer)
//
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//            viewModel.stopAutoRefreshReadyOrders()
//        }
//    }
//
//    val isTestUser = remember {
//        val normalizedName = user.name.replace("_", "", ignoreCase = true)
//            .replace(" ", "", ignoreCase = true)
//            .lowercase()
//        normalizedName.startsWith("testdeliveryboy")
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Orders", color = Color.White) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back",
//                            tint = Color.White
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = colorResource(id = R.color.pink)
//                )
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()
//        ) {
//            if (isLoading) {
////                CircularProgressIndicator()
//            } else if (error != null) {
//                Text(text = "Error: $error", color = Color.Red)
//
//            } else {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                ) {
//                    settings?.let {
//                        Text(
//                            "Pickup Radius (km)  ${it.pickup_radius_km}",
//                            modifier = Modifier.padding(8.dp)
//                        )
//                    } ?: Text("Loading...", modifier = Modifier.padding(8.dp))
//                }
//                Divider()
//
//                LazyColumn {
////                    items(suborders) { suborder ->
//                    items(suborders, key = { it.suborder_id }) { suborder ->
//
//                        val customerNameNormalized = suborder.customer.name
//                            .replace("_", "", ignoreCase = true)
//                            .replace(" ", "", ignoreCase = true)
//                            .lowercase()
//
//                        val isTestCustomer = customerNameNormalized.contains("testcustomer")
//                        Log.d(
//                            "TEST_CHECK",
//                            "${suborder.orders_ID},${suborder.suborder_id} user.name=${user.name}, isTestUser=$isTestUser"
//                        )
//                        Log.d(
//                            "TEST_CHECK",
//                            "${suborder.orders_ID},${suborder.suborder_id} customer.name=${suborder.customer.name}, isTestCustomer=$isTestCustomer"
//                        )
//
//                        // ✅ Only show matching orders based on user and customer test status
//                        if ((isTestUser && isTestCustomer) || (!isTestUser && !isTestCustomer)) {
//
//
//                            val pickupLat =
//                                suborder.shop.branch.pickup_location.latitude.toDoubleOrNull()
//                                    ?: 0.0
//                            val pickupLng =
//                                suborder.shop.branch.pickup_location.longitude.toDoubleOrNull()
//                                    ?: 0.0
//                            val deliveryLat = suborder.customer.delivery_address.latitude
//                            val deliveryLng = suborder.customer.delivery_address.longitude
//
//                            val distanceKm =
//                                calculateDistanceKm(pickupLat, pickupLng, deliveryLat, deliveryLng)
//
//                            val pickupAddress =
//                                "${suborder.shop.branch.pickup_location.area}, ${suborder.shop.branch.pickup_location.city}"
//                            val deliveryAddress =
//                                "${suborder.customer.delivery_address.street}, ${suborder.customer.delivery_address.city}"
//
//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(8.dp)
//                                    .clickable {
//                                        selectedSuborder = suborder
//                                        showDialog =
//                                            true // Show dialog immediately or after data load depending on need
//                                    },
//                                elevation = CardDefaults.cardElevation(4.dp)
//                            ) {
//                                Column(modifier = Modifier.padding(16.dp)) {
//                                    // Line 1: Order Info
//                                    Text(
//                                        "Order ID: ${suborder.orders_ID}, Suborder ID: ${suborder.suborder_id}, Customer ID: ${suborder.customer.customer_ID}, Name: ${suborder.customer.name}",
//                                        fontWeight = FontWeight.Bold
//                                    )
//
//// Line 2: Pickup
//                                    Row(verticalAlignment = Alignment.CenterVertically) {
//                                        Icon(
//                                            Icons.Filled.LocationOn,
//                                            contentDescription = "Pickup",
//                                            tint = Color(0xFF4CAF50)
//                                        )
//                                        Spacer(modifier = Modifier.width(4.dp))
//                                        Text(
//                                            text = "Pickup from: $pickupAddress",
//                                            fontWeight = FontWeight.Bold
//                                        )
//                                    }
//
//// Line 3: Delivery
//                                    Row(verticalAlignment = Alignment.CenterVertically) {
//                                        Icon(
//                                            Icons.Filled.LocationOn,
//                                            contentDescription = "Delivery",
//                                            tint = Color(0xFFF44336)
//                                        )
//                                        Spacer(modifier = Modifier.width(4.dp))
//                                        Text(
//                                            text = "Deliver to: $deliveryAddress",
//                                            fontWeight = FontWeight.Bold
//                                        )
//                                    }
//
//// Line 4: Distance and Total Amount
//                                    Text(
//                                        text = "Distance: $distanceKm km",
//                                        color = Color.Gray,
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                    Text(
//                                        "Total Amount: Rs. ${suborder.total_amount}",
//                                        color = Color.Green,
//                                        fontWeight = FontWeight.SemiBold
//                                    )
//
//                                    if (vehicles != null && vehicles.isNotEmpty()) {
//                                        Column(
//                                            verticalArrangement = Arrangement.spacedBy(12.dp),
//                                            modifier = Modifier.padding(top = 8.dp)
//                                        ) {
//                                            vehicles.forEach { vehicle ->
//                                                val perKm =
//                                                    vehicle.per_km_charge.toDoubleOrNull() ?: 0.0
//                                                val totalCharge = perKm * distanceKm
//
//                                                Text(
//                                                    text = "Delivery Charges: ${vehicle.per_km_charge} × ${
//                                                        "%.2f".format(
//                                                            distanceKm
//                                                        )
//                                                    } = ${"%.0f".format(totalCharge)} Rs",
//                                                    fontWeight = FontWeight.Medium,
//                                                    color = Color.DarkGray
//                                                )
//                                            }
//                                        }
//                                    }
//
//
//                                    // Accept Order button
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(top = 8.dp),
//                                        horizontalArrangement = Arrangement.End
//                                    ) {
//                                        Button(
//                                            onClick = {
//                                                viewModel.acceptOrder(
//                                                    deliveryBoyID,
//                                                    suborder.suborder_id
//                                                )
//                                            }
//                                        ) {
//                                            Text("Accept Order")
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (showDialog) {
//                AlertDialog(
//                    onDismissRequest = { showDialog = false },
//                    title = { Text("Order Details", fontWeight = FontWeight.Bold) },
//                    text = {
//                        // Make the dialog content scrollable
//                        Box(
//                            Modifier
//                                .fillMaxWidth()
//                                .heightIn(min = 100.dp, max = 500.dp) // Set dialog height limit
//                        ) {
//                            LazyColumn(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .padding(16.dp)
//                            ) {
//                                when {
//                                    isLoadingVendor -> {
//                                        item {
//                                            Box(
//                                                modifier = Modifier
//                                                    .fillMaxWidth()
//                                                    .padding(24.dp),
//                                                contentAlignment = Alignment.Center
//                                            ) {
//                                                CircularProgressIndicator()
//                                            }
//                                        }
//                                    }
//
//                                    errorVendor != null -> {
//                                        item {
//                                            Text(
//                                                text = errorVendor ?: "Unknown error",
//                                                color = Color.Red,
//                                                modifier = Modifier.padding(8.dp)
//                                            )
//                                        }
//                                    }
//
//                                    suborderDetails != null -> {
//                                        item {
//                                            val details = suborderDetails!!
//                                            // Suborder Header
//                                            Card(
//                                                modifier = Modifier.fillMaxWidth(),
//                                                colors = CardDefaults.cardColors(
//                                                    containerColor = Color(
//                                                        0xFFF7F7F7
//                                                    )
//                                                ),
//                                                elevation = CardDefaults.cardElevation(2.dp)
//                                            ) {
//                                                Column(modifier = Modifier.padding(16.dp)) {
//                                                    Text(
//                                                        "Suborder ID: ${details.suborder_id}",
//                                                        fontWeight = FontWeight.Bold
//                                                    )
//                                                    Spacer(modifier = Modifier.height(8.dp))
//                                                    StatusChip(status = details.status)
//                                                    Spacer(modifier = Modifier.height(8.dp))
//                                                    Text("Payment Status: ${details.payment_status}")
//                                                    Text("Total Amount: Rs. ${details.total_amount}")
//                                                    Text("Vendor Type: ${details.vendor_type}")
//                                                }
//                                            }
//
//                                            Spacer(modifier = Modifier.height(16.dp))
//                                            Text(
//                                                "Order Items",
//                                                fontWeight = FontWeight.Bold,
//                                                modifier = Modifier.padding(
//                                                    top = 8.dp,
//                                                    bottom = 8.dp
//                                                )
//                                            )
//                                        }
//
//                                        // Order items list
//                                        orderDetails?.forEach { orderDetail ->
//                                            item {
//                                                Card(
//                                                    modifier = Modifier
//                                                        .fillMaxWidth()
//                                                        .padding(vertical = 6.dp),
//                                                    elevation = CardDefaults.cardElevation(1.dp)
//                                                ) {
//                                                    Row(
//                                                        modifier = Modifier
//                                                            .fillMaxWidth()
//                                                            .padding(12.dp)
//                                                    ) {
//                                                        AsyncImage(
//                                                            model = ImageRequest.Builder(
//                                                                LocalContext.current
//                                                            )
//                                                                .data(orderDetail.item.item_picture)
//                                                                .crossfade(true)
//                                                                .build(),
//                                                            contentDescription = "Item Image",
//                                                            modifier = Modifier
//                                                                .size(70.dp)
//                                                                .clip(MaterialTheme.shapes.medium),
//                                                            contentScale = ContentScale.Crop
//                                                        )
//
//                                                        Spacer(modifier = Modifier.width(12.dp))
//
//                                                        Column(
//                                                            modifier = Modifier
//                                                                .weight(1f)
//                                                                .align(Alignment.CenterVertically)
//                                                        ) {
//                                                            Text(
//                                                                orderDetail.item.item_name,
//                                                                fontWeight = FontWeight.SemiBold
//                                                            )
//                                                            Text("Qty: ${orderDetail.quantity}")
//                                                            Text("Price: Rs. ${orderDetail.order_detail_price}")
//                                                            Text("Total: Rs. ${orderDetail.order_detail_total}")
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    else -> {
//                                        item {
//                                            Text("Loading...")
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    },
//                    confirmButton = {
//                        Button(onClick = {
//                            selectedSuborder?.let {
//                                viewModel.acceptOrder(
//                                    deliveryBoyID,
//                                    it.suborder_id
//                                )
//                            }
//                            showDialog = false
//                        }) {
//                            Text("Accept")
//                        }
//                    },
//                    dismissButton = {
//                        TextButton(onClick = { showDialog = false }) {
//                            Text("Cancel")
//                        }
//                    }
//                )
//            }
//            val context = LocalContext.current
//
//            LaunchedEffect(response) {
//                response?.let {
//                    if (it.message != null) {
//                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
//                        viewModel.fetchReadySuborders(deliveryBoyId = lmdUserID)
//                    } else if (it.error != null) {
//                        Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
//                    }
//                    viewModel.clearAcceptOrderResponse()
//                }
//            }
//
//
//        }
//    }
//}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

//      BELOW CODE IS ACCEPT ORDER ONE BY ONE WITHOUT ANY CONDITION
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ReadySubordersScreen(
//    deliveryBoyID: Int,
//    lmdUserID: Int,
//    navController: NavHostController,
//    viewModel: DeliveryBoyViewModel = hiltViewModel(),
//    vendorViewModel: VendorViewModel = hiltViewModel(),
//    authViewModel: AuthViewModel = hiltViewModel()
//) {
//    val user = remember { authViewModel.getUserDetails() }
//    val suborders = viewModel.readySuborders
//    val isLoading = viewModel.isLoadingReadySuborders
//    val error = viewModel.errorMessageReadySuborders
//
//    val vehicles = viewModel.vehiclesState
//
//    LaunchedEffect(Unit) {
//        viewModel.loadVehicles(deliveryBoyID)
//    }
//
//    val response = viewModel.acceptOrderResponse.value
//    val loading = viewModel.loading.value
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchReadySuborders(deliveryBoyId = lmdUserID) // Replace with actual ID
//    }
//
//
//    // State for dialog
//    var showDialog by remember { mutableStateOf(false) }
//    var selectedSuborder: ReadySuborder? by remember { mutableStateOf(null) }
//
//    // Vendor ViewModel states
//    val suborderDetails by vendorViewModel.suborderDetails.collectAsState()
//    val orderDetails by vendorViewModel.orderDetails.collectAsState()
//    val isLoadingVendor by vendorViewModel.isLoadingSuborderDetails
//    val errorVendor by vendorViewModel.errors
//
//    // Fetch details when selectedSuborder changes
//    LaunchedEffect(selectedSuborder) {
//        selectedSuborder?.let {
//            vendorViewModel.loadSuborderDetails(
//                it.vendor_ID,
//                it.shop_ID,
//                it.branch_ID,
//                it.suborder_id
//            )
//        }
//    }
//
//
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    DisposableEffect(Unit) {
//        val observer = LifecycleEventObserver { _, event ->
//            when (event) {
//                Lifecycle.Event.ON_START -> {
//                    viewModel.startAutoRefreshReadyOrders(lmdUserID)
//                }
//
//                Lifecycle.Event.ON_STOP -> {
//                    viewModel.stopAutoRefreshReadyOrders()
//                }
//
//                else -> {}
//            }
//        }
//
//        lifecycleOwner.lifecycle.addObserver(observer)
//
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//            viewModel.stopAutoRefreshReadyOrders()
//        }
//    }
//
//    val isTestUser = remember {
//        val normalizedName = user.name.replace("_", "", ignoreCase = true)
//            .replace(" ", "", ignoreCase = true)
//            .lowercase()
//        normalizedName.startsWith("testdeliveryboy")
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Orders", color = Color.White) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back",
//                            tint = Color.White
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = colorResource(id = R.color.pink)
//                )
//            )
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()
//        ) {
//            if (isLoading) {
////                CircularProgressIndicator()
//            } else if (error != null) {
//                Text(text = "Error: $error", color = Color.Red)
//
//            } else {
//                LazyColumn {
////                    items(suborders) { suborder ->
//                    items(suborders, key = { it.suborder_id }) { suborder ->
//
//                        val customerNameNormalized = suborder.customer.name
//                            .replace("_", "", ignoreCase = true)
//                            .replace(" ", "", ignoreCase = true)
//                            .lowercase()
//
//                        val isTestCustomer = customerNameNormalized.contains("testcustomer")
//                        Log.d(
//                            "TEST_CHECK",
//                            "${suborder.orders_ID},${suborder.suborder_id} user.name=${user.name}, isTestUser=$isTestUser"
//                        )
//                        Log.d(
//                            "TEST_CHECK",
//                            "${suborder.orders_ID},${suborder.suborder_id} customer.name=${suborder.customer.name}, isTestCustomer=$isTestCustomer"
//                        )
//
//                        // ✅ Only show matching orders based on user and customer test status
//                        if ((isTestUser && isTestCustomer) || (!isTestUser && !isTestCustomer)) {
//
//
//                            val pickupLat =
//                                suborder.shop.branch.pickup_location.latitude.toDoubleOrNull()
//                                    ?: 0.0
//                            val pickupLng =
//                                suborder.shop.branch.pickup_location.longitude.toDoubleOrNull()
//                                    ?: 0.0
//                            val deliveryLat = suborder.customer.delivery_address.latitude
//                            val deliveryLng = suborder.customer.delivery_address.longitude
//
//                            val distanceKm =
//                                calculateDistanceKm(pickupLat, pickupLng, deliveryLat, deliveryLng)
//
//                            val pickupAddress =
//                                "${suborder.shop.branch.pickup_location.area}, ${suborder.shop.branch.pickup_location.city}"
//                            val deliveryAddress =
//                                "${suborder.customer.delivery_address.street}, ${suborder.customer.delivery_address.city}"
//
//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(8.dp)
//                                    .clickable {
//                                        selectedSuborder = suborder
//                                        showDialog =
//                                            true // Show dialog immediately or after data load depending on need
//                                    },
//                                elevation = CardDefaults.cardElevation(4.dp)
//                            ) {
//                                Column(modifier = Modifier.padding(16.dp)) {
//                                    // Line 1: Order Info
//                                    Text(
//                                        "Order ID: ${suborder.orders_ID}, Suborder ID: ${suborder.suborder_id}",
//                                        fontWeight = FontWeight.Bold
//                                    )
//
//// Line 2: Pickup
//                                    Row(verticalAlignment = Alignment.CenterVertically) {
//                                        Icon(
//                                            Icons.Filled.LocationOn,
//                                            contentDescription = "Pickup",
//                                            tint = Color(0xFF4CAF50)
//                                        )
//                                        Spacer(modifier = Modifier.width(4.dp))
//                                        Text(
//                                            text = "Pickup from: $pickupAddress",
//                                            fontWeight = FontWeight.Bold
//                                        )
//                                    }
//
//// Line 3: Delivery
//                                    Row(verticalAlignment = Alignment.CenterVertically) {
//                                        Icon(
//                                            Icons.Filled.LocationOn,
//                                            contentDescription = "Delivery",
//                                            tint = Color(0xFFF44336)
//                                        )
//                                        Spacer(modifier = Modifier.width(4.dp))
//                                        Text(
//                                            text = "Deliver to: $deliveryAddress",
//                                            fontWeight = FontWeight.Bold
//                                        )
//                                    }
//
//// Line 4: Distance and Total Amount
//                                    Text(
//                                        text = "Distance: $distanceKm km",
//                                        color = Color.Gray,
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                    Text(
//                                        "Total Amount: Rs. ${suborder.total_amount}",
//                                        color = Color.Green,
//                                        fontWeight = FontWeight.SemiBold
//                                    )
//
//                                    if (vehicles != null && vehicles.isNotEmpty()) {
//                                        Column(
//                                            verticalArrangement = Arrangement.spacedBy(12.dp),
//                                            modifier = Modifier.padding(top = 8.dp)
//                                        ) {
//                                            vehicles.forEach { vehicle ->
//                                                val perKm =
//                                                    vehicle.per_km_charge.toDoubleOrNull() ?: 0.0
//                                                val totalCharge = perKm * distanceKm
//
//                                                Text(
//                                                    text = "Delivery Charges: ${vehicle.per_km_charge} × ${
//                                                        "%.2f".format(
//                                                            distanceKm
//                                                        )
//                                                    } = ${"%.0f".format(totalCharge)} Rs",
//                                                    fontWeight = FontWeight.Medium,
//                                                    color = Color.DarkGray
//                                                )
//                                            }
//                                        }
//                                    }
//
//
//                                    // Accept Order button
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(top = 8.dp),
//                                        horizontalArrangement = Arrangement.End
//                                    ) {
//                                        Button(
//                                            onClick = {
//                                                viewModel.acceptOrder(
//                                                    deliveryBoyID,
//                                                    suborder.suborder_id
//                                                )
//                                            }
//                                        ) {
//                                            Text("Accept Order")
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (showDialog) {
//                AlertDialog(
//                    onDismissRequest = { showDialog = false },
//                    title = { Text("Order Details", fontWeight = FontWeight.Bold) },
//                    text = {
//                        // Make the dialog content scrollable
//                        Box(
//                            Modifier
//                                .fillMaxWidth()
//                                .heightIn(min = 100.dp, max = 500.dp) // Set dialog height limit
//                        ) {
//                            LazyColumn(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .padding(16.dp)
//                            ) {
//                                when {
//                                    isLoadingVendor -> {
//                                        item {
//                                            Box(
//                                                modifier = Modifier
//                                                    .fillMaxWidth()
//                                                    .padding(24.dp),
//                                                contentAlignment = Alignment.Center
//                                            ) {
//                                                CircularProgressIndicator()
//                                            }
//                                        }
//                                    }
//
//                                    errorVendor != null -> {
//                                        item {
//                                            Text(
//                                                text = errorVendor ?: "Unknown error",
//                                                color = Color.Red,
//                                                modifier = Modifier.padding(8.dp)
//                                            )
//                                        }
//                                    }
//
//                                    suborderDetails != null -> {
//                                        item {
//                                            val details = suborderDetails!!
//                                            // Suborder Header
//                                            Card(
//                                                modifier = Modifier.fillMaxWidth(),
//                                                colors = CardDefaults.cardColors(
//                                                    containerColor = Color(
//                                                        0xFFF7F7F7
//                                                    )
//                                                ),
//                                                elevation = CardDefaults.cardElevation(2.dp)
//                                            ) {
//                                                Column(modifier = Modifier.padding(16.dp)) {
//                                                    Text(
//                                                        "Suborder ID: ${details.suborder_id}",
//                                                        fontWeight = FontWeight.Bold
//                                                    )
//                                                    Spacer(modifier = Modifier.height(8.dp))
//                                                    StatusChip(status = details.status)
//                                                    Spacer(modifier = Modifier.height(8.dp))
//                                                    Text("Payment Status: ${details.payment_status}")
//                                                    Text("Total Amount: Rs. ${details.total_amount}")
//                                                    Text("Vendor Type: ${details.vendor_type}")
//                                                }
//                                            }
//
//                                            Spacer(modifier = Modifier.height(16.dp))
//                                            Text(
//                                                "Order Items",
//                                                fontWeight = FontWeight.Bold,
//                                                modifier = Modifier.padding(
//                                                    top = 8.dp,
//                                                    bottom = 8.dp
//                                                )
//                                            )
//                                        }
//
//                                        // Order items list
//                                        orderDetails?.forEach { orderDetail ->
//                                            item {
//                                                Card(
//                                                    modifier = Modifier
//                                                        .fillMaxWidth()
//                                                        .padding(vertical = 6.dp),
//                                                    elevation = CardDefaults.cardElevation(1.dp)
//                                                ) {
//                                                    Row(
//                                                        modifier = Modifier
//                                                            .fillMaxWidth()
//                                                            .padding(12.dp)
//                                                    ) {
//                                                        AsyncImage(
//                                                            model = ImageRequest.Builder(
//                                                                LocalContext.current
//                                                            )
//                                                                .data(orderDetail.item.item_picture)
//                                                                .crossfade(true)
//                                                                .build(),
//                                                            contentDescription = "Item Image",
//                                                            modifier = Modifier
//                                                                .size(70.dp)
//                                                                .clip(MaterialTheme.shapes.medium),
//                                                            contentScale = ContentScale.Crop
//                                                        )
//
//                                                        Spacer(modifier = Modifier.width(12.dp))
//
//                                                        Column(
//                                                            modifier = Modifier
//                                                                .weight(1f)
//                                                                .align(Alignment.CenterVertically)
//                                                        ) {
//                                                            Text(
//                                                                orderDetail.item.item_name,
//                                                                fontWeight = FontWeight.SemiBold
//                                                            )
//                                                            Text("Qty: ${orderDetail.quantity}")
//                                                            Text("Price: Rs. ${orderDetail.order_detail_price}")
//                                                            Text("Total: Rs. ${orderDetail.order_detail_total}")
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    else -> {
//                                        item {
//                                            Text("Loading...")
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    },
//                    confirmButton = {
//                        Button(onClick = {
//                            selectedSuborder?.let {
//                                viewModel.acceptOrder(
//                                    deliveryBoyID,
//                                    it.suborder_id
//                                )
//                            }
//                            showDialog = false
//                        }) {
//                            Text("Accept")
//                        }
//                    },
//                    dismissButton = {
//                        TextButton(onClick = { showDialog = false }) {
//                            Text("Cancel")
//                        }
//                    }
//                )
//            }
//            val context = LocalContext.current
//
//            LaunchedEffect(response) {
//                response?.let {
//                    if (it.message != null) {
//                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
//                        viewModel.fetchReadySuborders(deliveryBoyId = lmdUserID)
//                    } else if (it.error != null) {
//                        Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
//                    }
//                    viewModel.clearAcceptOrderResponse()
//                }
//            }
//
//
//        }
//    }
//}


private fun calculateDistanceKm(
    startLat: Double, startLng: Double,
    endLat: Double, endLng: Double
): Double {
    val earthRadius = 6371.0 // KM
    val dLat = Math.toRadians(endLat - startLat)
    val dLng = Math.toRadians(endLng - startLng)
    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(startLat)) *
            cos(Math.toRadians(endLat)) *
            sin(dLng / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return (earthRadius * c * 100.0).roundToInt() / 100.0
}
