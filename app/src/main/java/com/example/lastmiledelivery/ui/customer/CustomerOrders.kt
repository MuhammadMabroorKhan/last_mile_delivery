package com.example.lastmiledelivery.ui.customer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.customer.Item
import com.example.lastmiledelivery.data.models.customer.Order
import com.example.lastmiledelivery.data.models.customer.SubOrders
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import com.example.lastmiledelivery.viewmodels.customer.OrderUiState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Polyline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.lastmiledelivery.viewmodels.common.StatusViewModel
import com.example.lastmiledelivery.viewmodels.deliveryboy.DeliveryBoyViewModel
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrders(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    customerViewModel: CustomerViewModel = hiltViewModel(),
) {
    val user = remember { authViewModel.getUserDetails() }
    val customerData by customerViewModel.customerData.collectAsState()
    val errorMessage by customerViewModel.errorMessages.collectAsState()

    val context = LocalContext.current

    // Trigger data fetch when the composable enters composition
    LaunchedEffect(key1 = user.id) {
        customerViewModel.fetchCustomerData(user.id)
    }

    LaunchedEffect(customerViewModel.customerState) {
        val storedCustomerId = customerViewModel.getCustomerId()
        Log.d("CustomerMainScreen", "Stored Customer ID: $storedCustomerId")

        if (storedCustomerId != null) {
            customerViewModel.fetchCustomerOrders(customerId = storedCustomerId)
        }
    }
    // Observe customer data and error messages
    val customer = customerViewModel.customerState


    val orderState = customerViewModel.orderState

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
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (orderState) {
                is OrderUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is OrderUiState.Success -> {
                    val orders = orderState.orders
                    if (orders.isEmpty()) {
                        Text("No orders found.")
                    } else {
                        LazyColumn {
                            items(orders) { order ->
                                OrderCard(order, navController = navController)
                            }
                        }
                    }
                }

                is OrderUiState.Error -> {
                    //Text("Error: ${orderState.message}")
                    Text("No Orders Found")
                }
            }
        }
    }
}

//@Composable
//fun OrderCard(
//    order: Order,
//    navController: NavHostController,
//    context: Context = LocalContext.current
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .clickable {
//                navController.navigate("orderDetail/${order.id}/${order.customers_ID}/${order.addresses_ID}")
//            },
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = RoundedCornerShape(16.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column {
//                Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
//                Text("Amount: PKR ${order.total_amount}", fontSize = 16.sp)
//                Text("Status: ${order.order_status}", color = Color.Gray)
//                Text("Date: ${order.order_date}", fontSize = 12.sp, color = Color.DarkGray)
//                Text(
//                    "Payment: ${order.payment_status} (${order.payment_method})",
//                    fontSize = 12.sp,
//                    color = Color.Gray
//                )
//            }
//
//            IconButton(onClick = {
//                navController.navigate("orderDetail/${order.id}/${order.customers_ID}/${order.addresses_ID}")
//
//            }) {
//                Icon(
//                    imageVector = Icons.Filled.ArrowForwardIos,
//                    contentDescription = "More Options",
//                    tint = Color.Black
//                )
//            }
//        }
//    }
//}
@Composable
fun OrderCard(
    order: Order,
    navController: NavHostController,
    context: Context = LocalContext.current,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    val cancelResult by viewModel.cancelOrderResult
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("orderDetail/${order.id}/${order.customers_ID}/${order.addresses_ID}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Amount: PKR ${order.total_amount}", fontSize = 16.sp)
                Text("Status: ${order.order_status}", color = Color.Gray)
                Text("Date: ${order.order_date}", fontSize = 12.sp, color = Color.DarkGray)
                Text(
                    "Payment: ${order.payment_status} (${order.payment_method})",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Row {
                IconButton(onClick = {
                    navController.navigate("orderDetail/${order.id}/${order.customers_ID}/${order.addresses_ID}")
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = "More Options",
                        tint = Color.Black
                    )
                }

                // Show delete icon only if order is pending
                if (order.order_status.equals("pending", ignoreCase = true)) {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.cancelOrder(order.id)
                            delay(3000)
                            viewModel.fetchCustomerOrders(order.customers_ID)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Cancel Order",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }

    // Show toast for result
    cancelResult?.let {
        when {
            it.isSuccess -> {
                Toast.makeText(context, it.getOrNull(), Toast.LENGTH_SHORT).show()
                viewModel.clearCancelResult()
            }

            it.isFailure -> {
                Toast.makeText(
                    context,
                    it.exceptionOrNull()?.message ?: "Error occurred",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearCancelResult()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavHostController,
    orderId: Int,
    customerId: Int,
    addressId: Int,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    // Fetch the order details when the screen is loaded
    LaunchedEffect(orderId) {
        viewModel.fetchOrderDetails(orderId)
    }

    // Observe the orderDetails state
    val orderDetailsState = viewModel.orderDetails.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SubOrders", color = Color.White) },
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
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            // Show loading or data
            if (orderDetailsState == null) {
                // Loading state
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text("Order Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    // Display the order details if available
                    orderDetailsState?.let { details ->
                        Text("Order ID: ${details.order_id}")
                        Text("Order Date: ${details.order_date}")
                        Text("Order Status: ${details.order_status}")
                        Text("Total Amount: ${details.order_total_amount}")

                        // Display suborders in a list
                        details.suborders?.let { suborders ->
                            LazyColumn {
                                items(suborders) { suborder ->
                                    SubOrderCard(suborder, customerId, addressId, navController)
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
fun SubOrderCard(
    suborder: SubOrders,
    customerId: Int,
    addressId: Int,
    navController: NavHostController
) {
    // State to manage the expansion of the suborder details
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Show the main SubOrder details: ID, Status, and Total Amount
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            ) {
                Text(
                    text = "SubOrder ID: ${suborder.suborder_id}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f)) // To push the arrow to the end

                // Dropdown arrow that toggles visibility of details
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle details",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Text("Status: ${suborder.suborder_status}")
            Text("Total Amount: ${suborder.suborder_total_amount}")
            // Show additional details when expanded
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))


                // Display items within the suborder
                suborder.items?.let { items ->
                    items.forEach { item ->
                        ItemCard(item)
                    }
                }
            }

            // Track Order Button
            Spacer(modifier = Modifier.height(16.dp)) // Add space before the button
            Button(
                onClick = {
                    // Navigate to the track order screen and pass necessary parameters
//                    navController.navigate("track_order/${suborder.suborder_id}/$customerId/$addressId")
                    navController.navigate("track_order/${suborder.suborder_id}/$customerId/$addressId/${suborder.vendor_ID}/${suborder.shop_ID}/${suborder.branch_ID}")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.pink))
            ) {
                Text(text = "Track Order", color = Color.White)
            }
        }
    }
}

@Composable
fun ItemCard(item: Item) {
    // Create a default image to display when no image is available
    val defaultImage = painterResource(id = R.drawable.ic_launcher_background)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image section on the left side
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray) // A placeholder color
            ) {
                // Load the image if available, otherwise use the default image
//                AsyncImage(
//                    model = item.itemPicture ?: defaultImage,
//                    contentDescription = "Item Image",
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .clip(RoundedCornerShape(8.dp)),
//                    contentScale = ContentScale.Crop
//                )
                if (!item.itemPicture.isNullOrBlank()) {
                    AsyncImage(
                        model = item.itemPicture,
                        contentDescription = "Item Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
//                    AsyncImage(
//                        model = defaultImage, // your local fallback image or placeholder URL
//                        contentDescription = "Default Image",
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .clip(RoundedCornerShape(8.dp)),
//                        contentScale = ContentScale.Crop
//                    )
                }

            }

            // Item details on the right side
            Spacer(modifier = Modifier.width(16.dp)) // Add space between image and text

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Item: ${item.item_name}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text("Quantity: ${item.item_quantity}")
                Text("Price: ${item.item_total}")
                Text("Description: ${item.item_description}")
                Text("Preparation Time: ${item.preparation_time} mins")
            }
        }
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TrackOrderScreen(
//    suborderId: Int,
//    customerId: Int,
//    addressId: Int,
//    vendor_ID: Int,
//    shop_ID: Int,
//    branch_ID: Int,
//    navController: NavHostController,
//    viewModel: CustomerViewModel = hiltViewModel(),
//    deliveryBoyViewModel: DeliveryBoyViewModel = hiltViewModel(),
//    vendorViewModel: VendorViewModel = hiltViewModel()
//) {
//    val suborderDetails by vendorViewModel.suborderDetails.collectAsState()
//    val orderDetails by vendorViewModel.orderDetails.collectAsState()
//    val isLoadingSUbOrderDetail by vendorViewModel.isLoadingSuborderDetails
//    val error by vendorViewModel.errors
//
//    // Load suborder details when the screen is launched or the suborderId changes
//    LaunchedEffect(suborderId) {
//        vendorViewModel.loadSuborderDetails(vendor_ID, shop_ID, branch_ID, suborderId)
//    }
//
//
//
//
//
//
//
//
//
//    LaunchedEffect(Unit) {
//        viewModel.getRouteInfo(suborderId)
//    }
//
//    var orderLocationTrackingStatus by remember { mutableStateOf("-") }
//
//    LaunchedEffect(suborderId) {
//        suborderId?.let { suborderId ->
//            deliveryBoyViewModel.getLatestLocation(suborderId)
//        }
//    }
//
//    var orderPaymentStatus by remember { mutableStateOf("-") }
//
//    val status = viewModel.paymentStatus.value
//
//    DisposableEffect(suborderId) {
//        viewModel.startPolling(suborderId)
//        orderPaymentStatus = status?.paymentStatus.toString()
//        onDispose {
//            viewModel.stopPolling()
//        }
//    }
//
//    val scrollState = rememberScrollState()
//    val context = LocalContext.current
//
//    val routeInfo by remember { derivedStateOf { viewModel.routeInfo } }
//    val isLoading by remember { derivedStateOf { viewModel.isLoading } }
//
//    val statusViewModel: StatusViewModel = hiltViewModel()
//
//    LaunchedEffect(Unit) {
//        statusViewModel.loadStatuses()
//    }
//
//    val statuses = statusViewModel.statuses.value
//    val loading = statusViewModel.isLoading.value
//
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Track Order", color = Color.White) },
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
//    ) { padding ->
//
//        Box(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//        ) {
//            when {
//                isLoading -> {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                }
//
//                routeInfo != null -> {
//                    val pickup = routeInfo!!.data?.pickup_location
//                    val drop = routeInfo!!.data?.drop_location
//
//                    if (pickup != null && drop != null) {
//                        val pickupLatLng = LatLng(pickup.latitude, pickup.longitude)
//                        val dropLatLng = LatLng(drop.latitude, drop.longitude)
//
//                        val cameraPositionState = rememberCameraPositionState {
//                            position = CameraPosition.fromLatLngZoom(pickupLatLng, 18f)
//                        }
//
//                        Column(modifier = Modifier.fillMaxSize()) {
//                            GoogleMap(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(300.dp),
//                                cameraPositionState = cameraPositionState
//                            ) {
//                                Marker(
//                                    state = MarkerState(position = pickupLatLng),
//                                    title = "Pickup Location"
//                                )
//                                Marker(
//                                    state = MarkerState(position = dropLatLng),
//                                    title = "Drop Location"
//                                )
//                                Polyline(
//                                    points = listOf(pickupLatLng, dropLatLng),
//                                    color = Color.Blue,
//                                    width = 5f
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.height(16.dp))
//
////                            Column(modifier = Modifier.padding(16.dp)) {
////                                Text("Pickup: ${pickup.latitude}, ${pickup.longitude}")
////                                Text("Drop: ${drop.latitude}, ${drop.longitude}")
////                                Text("Order Date: ${routeInfo!!.data?.order_date}")
////                                Text("Estimated Delivery Time: ${routeInfo!!.data?.estimated_delivery_time}")
////                                Text("Delivery Time: ${routeInfo!!.data?.delivery_time}")
////                            }
//                        }
//                    } else {
//                        Text(
//                            "Location data not available",
//                            modifier = Modifier.align(Alignment.Center)
//                        )
//                    }
//                }
//
//                else -> {
//                    Text("No tracking info available", modifier = Modifier.align(Alignment.Center))
//                }
//            }
//
//            if (statuses != null) {
//                val currentStatus = "picked_up" // You can dynamically set this later
//                val statusList = statuses.suborderStatuses.values.toList()
//                val currentIndex = statusList.indexOf(currentStatus)
//
//
//                var orderStatus by remember { mutableStateOf(currentStatus ?: "-") }
//
//                Column(
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .padding(16.dp)
//                        .verticalScroll(scrollState)
//                ) {
//                    Text(
//                        text = "Suborder Statuses:",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        text = "${vendor_ID}  , ${shop_ID}  ${branch_ID}  ${suborderId}:",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = "${orderStatus}  , ${orderPaymentStatus}:",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = "${orderLocationTrackingStatus}:",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//
//                    when {
//                        status?.error != null -> {
//                            Text("Error: ${status?.error}", color = Color.Red)
//                        }
//
//                        status?.paymentStatus != null -> {
////                            Text(
////                                text = "Payment Status: ${status?.paymentStatus?.replace("_", " ")?.uppercase()}",
////                                color = if (status?.paymentStatus == "confirmed_by_customer") Color.Green else Color.Yellow,
////                                fontSize = 20.sp
////                            )
//                            orderPaymentStatus = status?.paymentStatus
//                        }
//
//                        else -> {
//                            Text("Fetching payment status...", color = Color.Gray)
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    statusList.forEachIndexed { index, status ->
//                        val color = when {
//                            index < currentIndex -> Color.Gray              // Past
//                            index == currentIndex -> Color.Blue             // Current
//                            else -> Color(0xFF81C784)                       // Upcoming
//                        }
//
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 4.dp)
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .size(12.dp)
//                                    .clip(CircleShape)
//                                    .background(color)
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text(
//                                text = status.replace("_", " ").replaceFirstChar { it.uppercase() },
//                                color = color,
//                                fontSize = 16.sp
//                            )
//                        }
//                    }
//
//
//                    deliveryBoyViewModel.latestLocation?.let {
//                        if (currentStatus.equals(
//                                "handover_confirmed",
//                                true
//                            ) || currentStatus.equals(
//                                "in_transit",
//                                true
//                            )
//                        ) {
////                            orderStatus = currentStatus ?: orderStatus
////                            orderPaymentStatus = it.status ?: orderPaymentStatus
//                            orderLocationTrackingStatus = it.status ?: orderLocationTrackingStatus
//
//                        }
//                    }
//
//                    // 👉 Show "Confirm Delivery" button if status is "reached_destination"
//                    if (orderStatus.equals("reached_destination", true)
//                        || orderLocationTrackingStatus.equals("reached_destination", true)
//                    ) {
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Button(onClick = {
//                            viewModel.confirmDelivery(suborderId = suborderId)
//                        }) {
//                            Text("Confirm Delivery")
//                        }
//
//                        // Optional: Show success/error toast
//                        val result = viewModel.confirmDeliveryResult.value
//                        result?.let {
//                            when {
//                                it.isSuccess -> {
//                                    Toast.makeText(context, it.getOrNull(), Toast.LENGTH_SHORT)
//                                        .show()
//                                    viewModel.clearDeliveryResult()
//                                }
//
//                                it.isFailure -> {
//                                    Toast.makeText(
//                                        context,
//                                        it.exceptionOrNull()?.message,
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    viewModel.clearDeliveryResult()
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackOrderScreen(
    suborderId: Int,
    customerId: Int,
    addressId: Int,
    vendor_ID: Int,
    shop_ID: Int,
    branch_ID: Int,
    navController: NavHostController,
    viewModel: CustomerViewModel = hiltViewModel(),
    deliveryBoyViewModel: DeliveryBoyViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel()
) {
    val suborderDetails by vendorViewModel.suborderDetails.collectAsState()
    val orderDetails by vendorViewModel.orderDetails.collectAsState()
    val isLoadingSUbOrderDetail by vendorViewModel.isLoadingSuborderDetails
    val error by vendorViewModel.errors

    // Load suborder details when the screen is launched or the suborderId changes
    LaunchedEffect(suborderId) {
        vendorViewModel.loadSuborderDetails(vendor_ID, shop_ID, branch_ID, suborderId)
    }

    LaunchedEffect(Unit) {
        viewModel.getRouteInfo(suborderId)
    }

    var orderLocationTrackingStatus by remember { mutableStateOf("-") }

    LaunchedEffect(suborderId) {
        suborderId?.let { suborderId ->
            deliveryBoyViewModel.getLatestLocation(suborderId)
        }
    }

    val liveTrackingData by viewModel.liveTracking

    LaunchedEffect(suborderId) {
        viewModel.getLatestLocation(suborderId)
    }
    var orderPaymentStatus by remember { mutableStateOf("-") }

    val status = viewModel.paymentStatus.value

//    DisposableEffect(suborderId) {
//        viewModel.startPolling(suborderId)
//        orderPaymentStatus = status?.paymentStatus.toString()
//        onDispose {
//            viewModel.stopPolling()
//        }
//    }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val routeInfo by remember { derivedStateOf { viewModel.routeInfo } }
    val isLoading by remember { derivedStateOf { viewModel.isLoading } }

    val statusViewModel: StatusViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        statusViewModel.loadStatuses()
    }


    val statuses = statusViewModel.statuses.value
    val loading = statusViewModel.isLoading.value


    val result by viewModel.confirmPaymentResult


    var deliveryBoyIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var pickupIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var dropIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }

// Safely load icons after GoogleMap is initialized
    var mapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(mapLoaded) {
        if (mapLoaded) {
            deliveryBoyIcon =
                bitmapDescriptorFromVector(
                    context,
                    R.drawable.logo
                )
            pickupIcon = bitmapDescriptorFromVector(
                context,
                R.drawable.storefront
            )
            dropIcon = bitmapDescriptorFromVector(
                context,
                R.drawable.account_circle
            )
        }
    }


    val routePoints by viewModel.liveRoute.collectAsState()

    // Trigger tracking
    LaunchedEffect(Unit) {
        viewModel.startLiveRouteTracking(suborderId)
    }




    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Order", color = Color.White) },
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
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                routeInfo != null -> {
                    val pickup = routeInfo!!.data?.pickup_location
                    val drop = routeInfo!!.data?.drop_location

                    if (pickup != null && drop != null) {
                        val pickupLatLng = LatLng(pickup.latitude, pickup.longitude)
                        val dropLatLng = LatLng(drop.latitude, drop.longitude)

                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(pickupLatLng, 18f)
                        }

                        Column(modifier = Modifier.fillMaxSize()) {
//                            GoogleMap(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(300.dp),
//                                cameraPositionState = cameraPositionState
//                            ) {
//                                Marker(
//                                    state = MarkerState(position = pickupLatLng),
//                                    title = "Pickup Location"
//                                )
//                                Marker(
//                                    state = MarkerState(position = dropLatLng),
//                                    title = "Drop Location"
//                                )
//                                Polyline(
//                                    points = listOf(pickupLatLng, dropLatLng),
//                                    color = Color.Blue,
//                                    width = 5f
//                                )
//                            }

                            GoogleMap(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                onMapLoaded = {
                                    mapLoaded = true // triggers the LaunchedEffect above
                                },
                                cameraPositionState = cameraPositionState
                            ) {


                                if (pickupIcon != null) {
                                    Marker(
                                        state = MarkerState(position = pickupLatLng),
                                        title = "Pickup Location",
                                        icon = pickupIcon
                                    )
                                }

                                if (dropIcon != null) {
                                    Marker(
                                        state = MarkerState(position = dropLatLng),
                                        title = "Drop Location",
                                        icon = dropIcon
                                    )
                                }

//                                // Pickup and Drop Markers
//                                Marker(
//                                    state = MarkerState(position = pickupLatLng),
//                                    title = "Pickup Location"
//                                )
//                                Marker(
//                                    state = MarkerState(position = dropLatLng),
//                                    title = "Drop Location"
//                                )

                                // Blue Route Line
                                Polyline(
                                    points = listOf(pickupLatLng, dropLatLng),
                                    color = Color.Blue,
                                    width = 5f
                                )

                                // Live Tracking Marker and Orange Line
                                if (liveTrackingData != null) {
                                    val liveLatLng = LatLng(
                                        liveTrackingData!!.latitude,
                                        liveTrackingData!!.longitude
                                    )

                                    if (deliveryBoyIcon != null) {
                                        Marker(
                                            state = MarkerState(position = liveLatLng),
                                            title = "Delivery Boy Location",
//                                        icon = BitmapDescriptorFactory.defaultMarker(
//                                            BitmapDescriptorFactory.HUE_ORANGE
//                                        )
                                            icon = deliveryBoyIcon
                                        )
                                    }

                                    // Optional orange line: pickup -> live location
                                    Polyline(
                                        points = listOf(pickupLatLng, liveLatLng),
                                        color = Color(0xFFFFA500), // Orange
                                        width = 6f
                                    )
                                }




                                // Static route
//                                Polyline(points = listOf(pickupLatLng, dropLatLng), color = Color.Blue, width = 5f)

                                // Live path points (dotted)
//                                if (routePoints.isNotEmpty()) {
//                                    Polyline(
//                                        points = routePoints,
//                                        color = Color.Green,
//                                        width = 5f,
//                                        pattern = listOf(Dot(), Gap(20f)) // dotted pattern
//                                    )
//                                }
                                if (routePoints.isNotEmpty()) {
                                    Polyline(
                                        points = routePoints,
                                        color = Color.Green,
                                        width = 8f,
                                        pattern = listOf(Dash(20f), Gap(10f)) // More visible dotted/dashed pattern
                                    )
                                }


                            }

                            Spacer(modifier = Modifier.height(16.dp))

//                            Column(modifier = Modifier.padding(16.dp)) {
//                                Text("Pickup: ${pickup.latitude}, ${pickup.longitude}")
//                                Text("Drop: ${drop.latitude}, ${drop.longitude}")
//                                Text("Order Date: ${routeInfo!!.data?.order_date}")
//                                Text("Estimated Delivery Time: ${routeInfo!!.data?.estimated_delivery_time}")
//                                Text("Delivery Time: ${routeInfo!!.data?.delivery_time}")
//                            }
                        }
                    } else {
                        Text(
                            "Location data not available",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                else -> {
                    Text("No tracking info available", modifier = Modifier.align(Alignment.Center))
                }
            }

            when {
                isLoadingSUbOrderDetail -> {
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

                    if (statuses != null) {
                        val currentStatus =
                            "${details.status}" // You can dynamically set this later
                        val statusList = statuses.suborderStatuses.values.toList()
                        val currentIndex = statusList.indexOf(currentStatus)


                        var orderStatus by remember { mutableStateOf(currentStatus ?: "-") }
                        orderPaymentStatus = details.payment_status
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                                .verticalScroll(scrollState)
                        ) {
                            Text(
                                text = "Suborder Statuses:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${orderStatus} , ${orderPaymentStatus} , $orderLocationTrackingStatus",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Light,
                                modifier = Modifier.fillMaxWidth()
                            )

//                            when {
//                                status?.error != null -> {
//                                    Text("Error: ${status?.error}", color = Color.Red)
//                                }
//
//                                status?.paymentStatus != null -> {
////                            Text(
////                                text = "Payment Status: ${status?.paymentStatus?.replace("_", " ")?.uppercase()}",
////                                color = if (status?.paymentStatus == "confirmed_by_customer") Color.Green else Color.Yellow,
////                                fontSize = 20.sp
////                            )
//                                    orderPaymentStatus = status?.paymentStatus
//                                }
//
//                                else -> {
//                                    Text("Fetching payment status...", color = Color.Gray)
//                                }
//                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            statusList.forEachIndexed { index, status ->
                                val color = when {
                                    index < currentIndex -> Color.Gray              // Past
                                    index == currentIndex -> Color.Blue             // Current
                                    else -> Color(0xFF81C784)                       // Upcoming
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
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = status.replace("_", " ")
                                            .replaceFirstChar { it.uppercase() },
                                        color = color,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            deliveryBoyViewModel.latestLocation?.let {
                                if (currentStatus.equals(
                                        "handover_confirmed",
                                        true
                                    ) || currentStatus.equals(
                                        "in_transit",
                                        true
                                    )
                                ) {
//                            orderStatus = currentStatus ?: orderStatus
//                            orderPaymentStatus = it.status ?: orderPaymentStatus
                                    orderLocationTrackingStatus =
                                        it.status ?: orderLocationTrackingStatus

                                }
                            }

                            if (
                                orderLocationTrackingStatus.equals("reached_destination", true)
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = {
                                    coroutineScope.launch {
                                        viewModel.confirmDelivery(suborderId = suborderId)
                                        delay(2000)
                                        vendorViewModel.loadSuborderDetails(
                                            vendor_ID,
                                            shop_ID,
                                            branch_ID,
                                            suborderId
                                        )
                                        statusViewModel.loadStatuses()
                                        deliveryBoyViewModel.getLatestLocation(suborderId)
                                        delay(2000)
                                        deliveryBoyViewModel.latestLocation?.let {
                                            orderLocationTrackingStatus =
                                                it.status ?: orderLocationTrackingStatus

                                        }
                                    }
                                }) {
                                    Text("Confirm Delivery")
                                }

                                // Optional: Show success/error toast
                                val result = viewModel.confirmDeliveryResult.value
                                result?.let {
                                    when {
                                        it.isSuccess -> {
                                            Toast.makeText(
                                                context,
                                                it.getOrNull(),
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            viewModel.clearDeliveryResult()
                                        }

                                        it.isFailure -> {
                                            Toast.makeText(
                                                context,
                                                it.exceptionOrNull()?.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            viewModel.clearDeliveryResult()
                                        }
                                    }
                                }
                            }
                            if (
                                (orderLocationTrackingStatus.equals(
                                    "delivered",
                                    ignoreCase = true
                                ) || orderStatus.equals("delivered", ignoreCase = true))

                                && orderPaymentStatus.equals("pending", ignoreCase = true)
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = {
                                    coroutineScope.launch {
                                        viewModel.confirmPayment(suborderId)
                                        delay(2000)
                                        vendorViewModel.loadSuborderDetails(
                                            vendor_ID,
                                            shop_ID,
                                            branch_ID,
                                            suborderId
                                        )
                                        statusViewModel.loadStatuses()
                                        deliveryBoyViewModel.getLatestLocation(suborderId)
                                        delay(2000)
                                        deliveryBoyViewModel.latestLocation?.let {
                                            orderLocationTrackingStatus =
                                                it.status ?: orderLocationTrackingStatus

                                        }
                                    }


                                }) {
                                    Text("Confirm Payment")
                                }

                                // Optional: Show Toasts for result
                                result?.let {
                                    when {
                                        it.isSuccess -> {
                                            Toast.makeText(
                                                context,
                                                it.getOrNull(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            viewModel.clearConfirmPaymentResult()
                                        }

                                        it.isFailure -> {
                                            Toast.makeText(
                                                context,
                                                it.exceptionOrNull()?.message,
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            viewModel.clearConfirmPaymentResult()
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


private fun bitmapDescriptorFromVector(
    context: Context,
    @DrawableRes vectorResId: Int
): BitmapDescriptor? {
    return try {
        // Get the vector drawable
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // Convert 50dp to pixels based on the screen density
        val sizeInPixels = (50 * context.resources.displayMetrics.density).toInt()

        // Set bounds for the drawable with the desired size (50dp)
        vectorDrawable?.setBounds(0, 0, sizeInPixels, sizeInPixels)

        // Create a bitmap with the new size
        val bitmap = Bitmap.createBitmap(sizeInPixels, sizeInPixels, Bitmap.Config.ARGB_8888)

        // Draw the vector drawable onto the bitmap
        val canvas = Canvas(bitmap)
        vectorDrawable?.draw(canvas)

        // Return the bitmap as a BitmapDescriptor
        BitmapDescriptorFactory.fromBitmap(bitmap)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}