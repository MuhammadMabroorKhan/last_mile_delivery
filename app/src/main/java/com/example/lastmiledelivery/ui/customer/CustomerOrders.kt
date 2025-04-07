package com.example.lastmiledelivery.ui.customer

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
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

    val context= LocalContext.current

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

@Composable
fun OrderCard(order: Order,  navController: NavHostController,context: Context = LocalContext.current) {
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
                Text("Payment: ${order.payment_status} (${order.payment_method})", fontSize = 12.sp, color = Color.Gray)
            }

            IconButton(onClick = {
                navController.navigate("orderDetail/${order.id}/${order.customers_ID}/${order.addresses_ID}")

            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowForwardIos,
                    contentDescription = "More Options",
                    tint = Color.Black
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(navController: NavHostController, orderId: Int, customerId: Int, addressId: Int, viewModel: CustomerViewModel = hiltViewModel()) {
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
                                    SubOrderCard(suborder,customerId,addressId,navController)
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
fun SubOrderCard(suborder: SubOrders, customerId: Int, addressId: Int, navController: NavHostController) {
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
                    navController.navigate("track_order/${suborder.suborder_id}/$customerId/$addressId")
                },
                modifier = Modifier.fillMaxWidth(),
                colors= ButtonDefaults.buttonColors(colorResource(id = R.color.pink))
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
                AsyncImage(
                    model = item.itemPicture ?: defaultImage,
                    contentDescription = "Item Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackOrderScreen(
    suborderId: Int,
    customerId: Int,
    addressId: Int,
    navController: NavHostController,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    // Fetch track order details using suborderId, customerId, and addressId
//    LaunchedEffect(suborderId) {
//        viewModel.fetchTrackOrderDetails(suborderId, customerId, addressId)
//    }
//
//    val trackOrderDetailsState = viewModel.trackOrderDetails.value

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
        Box(modifier = Modifier.padding(padding)) {
//            // Show loading or data
//            if (trackOrderDetailsState == null) {
//                // Loading state
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//            } else {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp)
//                ) {
//                    Text("Track Order Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
//
//                    // Display the track order details if available
//                    trackOrderDetailsState?.let { details ->
//                        Text("SubOrder ID: ${details.suborder_id}")
//                        Text("Status: ${details.status}")
//                        Text("Estimated Delivery Time: ${details.estimated_delivery_time}")
//                        Text("Current Location: ${details.current_location}")
//                        // You can add more details here based on your requirements
//                    }
//                }
//            }
            Text("Order Tracking ")
        }
    }
}
