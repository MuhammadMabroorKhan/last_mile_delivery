package com.example.lastmiledelivery.ui.customer

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.customer.Order
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
fun OrderDetailScreen(navController: NavHostController,orderId: Int, customerId: Int, addressId: Int,viewModel: CustomerViewModel= hiltViewModel()) {
    // Fetch the order details when the screen is loaded
    LaunchedEffect(orderId) {
        viewModel.fetchOrderDetails(orderId)
    }

    // Observe the orderDetails state
    val orderDetailsState = viewModel.orderDetails.value

    // Assuming you fetch the order details by these arguments
    // For this example, we'll show the data directly
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

//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//            ) {
//
//                Text("Order Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
//
//
//                Text("Order ID: $orderId")
//                Text("Customer ID: $customerId")
//                Text("Address ID: $addressId")
//            }
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
                                        Text("SubOrder ID: ${suborder.suborder_id}")
                                        Text("SubOrder Status: ${suborder.suborder_status}")
                                        // Add other details from SubOrder here
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
