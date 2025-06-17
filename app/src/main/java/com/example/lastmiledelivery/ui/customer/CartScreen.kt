package com.example.lastmiledelivery.ui.customer

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.customer.CartResponse
import com.example.lastmiledelivery.data.models.customer.CustomerData
import com.example.lastmiledelivery.data.models.customer.OrderDetail
import com.example.lastmiledelivery.data.models.customer.OrderRequest
import com.example.lastmiledelivery.data.models.customer.StockItemRequest
import com.example.lastmiledelivery.data.models.deliveryboy.VehicleCategory
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.customer.CartState
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import com.example.lastmiledelivery.viewmodels.deliveryboy.DeliveryBoyViewModel
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder

import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavHostController,
    viewModel: CustomerViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val cartState by viewModel.cartState.collectAsState()
    val user = remember { authViewModel.getUserDetails() }
//    val isTestUser = remember { user.name.contains("Test User", ignoreCase = true) }
    val isTestUser = remember {
        user.name.replace("_", "", ignoreCase = true).replace(" ", "", ignoreCase = true)
            .contains("testcustomer", ignoreCase = true)
    }

    val customerData by viewModel.customerData.collectAsState()
    val errorMessage by viewModel.errorMessages.collectAsState()

    val context = LocalContext.current

    // Trigger data fetch when the composable enters composition
    LaunchedEffect(key1 = user.id) {
        viewModel.fetchCustomerData(user.id)
    }

    LaunchedEffect(viewModel.customerState) {
        val storedCustomerId = viewModel.getCustomerId()
        Log.d("CustomerMainScreen", "Stored Customer ID: $storedCustomerId")

        if (storedCustomerId != null) {
            viewModel.fetchCartDetails(customerId = storedCustomerId)
        }
    }
    // Observe customer data and error messages
    val customer = viewModel.customerState
    val error = viewModel.errorMessage
    LaunchedEffect(customer?.customerId) {
        customer?.let {
            viewModel.fetchCustomerMainScreen(it.customerId)
        }
    }

    Log.d("CartScreen", "User: ${user?.id}, CustomerData: $customerData")


    val stockData by viewModel.stockState

    LaunchedEffect(cartState) {
        if (cartState is CartState.Success) {
            val cartData = (cartState as CartState.Success).cart

            // Ensure cart is not null
            if (cartData.cart != null && cartData.suborders.isNotEmpty()) {
                val stockCheckItems = cartData.suborders.flatMap { suborder ->
                    suborder.items.map { item ->
                        StockItemRequest(
                            vendor_ID = suborder.vendor_ID,
                            shop_ID = suborder.shop_ID,
                            branch_ID = suborder.branch_ID,
                            item_detail_ID = item.item_detail_id
                        )
                    }
                }

                if (stockCheckItems.isNotEmpty()) {
                    viewModel.fetchStockForItems(stockCheckItems)
                }
            }
        }
    }

    var showDialogOutStock by remember { mutableStateOf(false) }
    var alertMessageOutStock by remember { mutableStateOf("") } // empty string by default

    if (showDialogOutStock) {
        AlertDialog(onDismissRequest = {
            showDialogOutStock = false
            // Re-fetch stock
            val cartData = (cartState as? CartState.Success)?.cart
            if (cartData != null && cartData.cart != null && cartData.suborders.isNotEmpty()) {
                val stockCheckItems = cartData.suborders.flatMap { suborder ->
                    suborder.items.map { item ->
                        StockItemRequest(
                            vendor_ID = suborder.vendor_ID,
                            shop_ID = suborder.shop_ID,
                            branch_ID = suborder.branch_ID,
                            item_detail_ID = item.item_detail_id
                        )
                    }
                }
                if (stockCheckItems.isNotEmpty()) {
                    viewModel.fetchStockForItems(stockCheckItems)
                }
            }
        },
            title = { Text("Stock Issue") },
            text = { Text(alertMessageOutStock) },
            confirmButton = {
                Button(onClick = {
                    showDialogOutStock = false
                    // Re-fetch stock
                    val cartData = (cartState as? CartState.Success)?.cart
                    if (cartData != null && cartData.cart != null && cartData.suborders.isNotEmpty()) {
                        val stockCheckItems = cartData.suborders.flatMap { suborder ->
                            suborder.items.map { item ->
                                StockItemRequest(
                                    vendor_ID = suborder.vendor_ID,
                                    shop_ID = suborder.shop_ID,
                                    branch_ID = suborder.branch_ID,
                                    item_detail_ID = item.item_detail_id
                                )
                            }
                        }
                        if (stockCheckItems.isNotEmpty()) {
                            viewModel.fetchStockForItems(stockCheckItems)
                        }
                    }
                }) {
                    Text("OK")
                }
            })
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Cart", color = Color.White) }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id = R.color.pink)
        )
        )
    }) { paddingValues ->
        when (cartState) {
            is CartState.Loading -> {
                Log.d("CartDebug", "Cart is in loading state...")

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is CartState.Success -> {

                val cartData = (cartState as CartState.Success).cart

                if (cartData.cart == null) {
                    // If cart is null, show "No items in cart"
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No items in cart",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.SpaceBetween // Ensures bottom placement
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 12.dp), // Add some padding from the right side
                            horizontalArrangement = Arrangement.End // Align to the right
                        ) {
                            Text(text = "Clear Cart",
                                color = Color.Blue,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        if (customer != null) {
                                            viewModel.clearCart(customer.customerId)
                                        }
                                    })
                        }

                        // Scrollable Suborders List
//                        LazyColumn(
//                            modifier = Modifier.weight(1f) // Takes remaining space
//                        ) {
//                            if (cartData.suborders.isNotEmpty()) {
//                                items(cartData.suborders) { suborder ->
//                                    Card(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(vertical = 8.dp)
//                                    ) {
//                                        Column(modifier = Modifier.padding(16.dp)) {
//                                            val shopName =
//                                                customerData?.find { it.shopId == suborder.shop_ID }?.shopName
//                                                    ?: "Unknown Shop"
//
//                                            Text(
//                                                "Shop Name: $shopName",
//                                                fontWeight = FontWeight.Bold
//                                            )
//                                            Text(
//                                                "Vendor Type: ${suborder.vendor_type}",
//                                                fontWeight = FontWeight.Bold
//                                            )
//                                            Text("Total Amount: ${suborder.total_amount}")
//                                            Divider()
//
//                                            if (suborder.items.isNotEmpty()) {
//                                                suborder.items.forEach { item ->
//                                                    Row(
//                                                        modifier = Modifier
//                                                            .fillMaxWidth()
//                                                            .padding(vertical = 8.dp),
//                                                        verticalAlignment = Alignment.CenterVertically
//                                                    ) {
//                                                        // Item Image
//                                                        AsyncImage(
//                                                            model = item.itemPicture,
//                                                            contentDescription = "Item Image",
//                                                            modifier = Modifier
//                                                                .size(60.dp)
//                                                                .clip(RoundedCornerShape(8.dp))
//                                                                .background(Color.Gray),
//                                                            contentScale = ContentScale.Crop
//                                                        )
//
//                                                        // Item Name and Description
//                                                        Column(
//                                                            modifier = Modifier
//                                                                .weight(1f)
//                                                                .padding(start = 8.dp)
//                                                        ) {
//
//                                                            Text(
//                                                                text = item.item_name,
//                                                                fontWeight = FontWeight.Bold,
//                                                                fontSize = 16.sp
//                                                            )
//                                                            Text(
//                                                                text = item.id.toString(),
//                                                                fontWeight = FontWeight.Bold,
//                                                                fontSize = 16.sp
//                                                            )
//                                                            Text(
//                                                                text = item.item_description,
//                                                                fontSize = 14.sp,
//                                                                color = Color.Gray,
//                                                                maxLines = 2,
//                                                                overflow = TextOverflow.Ellipsis
//                                                            )
//                                                            Text(
//                                                                text = "RS.${item.price}",
//                                                                fontSize = 14.sp,
//                                                                color = Color.Green
//                                                            )
//                                                        }
//
//                                                        // Quantity
//                                                        Text(
//                                                            text = "x${item.quantity}",
//                                                            fontWeight = FontWeight.Bold,
//                                                            fontSize = 16.sp,
//                                                            modifier = Modifier.padding(start = 8.dp)
//                                                        )
//                                                    }
//                                                    Divider()
//                                                }
//                                            } else {
//                                                Text("No items in this suborder")
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                item { Text("No suborders found") }
//                            }
//                        }
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            if (cartData.suborders.isNotEmpty()) {
                                items(cartData.suborders) { suborder ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            val shopName =
                                                customerData?.find { it.shopId == suborder.shop_ID }?.shopName
                                                    ?: "Unknown Shop"

                                            Text(
                                                "Shop Name: $shopName", fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                "Vendor Type: ${suborder.vendor_type}",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text("Total Amount: ${suborder.total_amount}")
                                            Divider()

                                            if (suborder.items.isNotEmpty()) {
                                                suborder.items.forEach { item ->
                                                    val matchingStock = stockData.find {
                                                        it.item_detail_ID == item.item_detail_id && it.vendor_ID == suborder.vendor_ID && it.shop_ID == suborder.shop_ID && it.branch_ID == suborder.branch_ID
                                                    }

                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        AsyncImage(
                                                            model = item.itemPicture,
                                                            contentDescription = "Item Image",
                                                            modifier = Modifier
                                                                .size(60.dp)
                                                                .clip(RoundedCornerShape(8.dp))
                                                                .background(Color.Gray),
                                                            contentScale = ContentScale.Crop
                                                        )

                                                        Column(
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .padding(start = 8.dp)
                                                        ) {
                                                            Text(
                                                                text = item.item_name,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 16.sp
                                                            )
                                                            Text(
                                                                text = item.item_description,
                                                                fontSize = 14.sp,
                                                                color = Color.Gray
                                                            )
                                                            Text(
                                                                text = "RS.${item.price}",
                                                                fontSize = 14.sp,
                                                                color = Color.Green
                                                            )
//                                                            Text(text = "Qty: ${item.quantity}", fontSize = 14.sp, fontWeight = FontWeight.Bold)

                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                IconButton(onClick = {
                                                                    val storedCustomerId =
                                                                        viewModel.getCustomerId()
                                                                    if (storedCustomerId != null) {
                                                                        viewModel.decreaseItemQuantity(
                                                                            item.id,
                                                                            item.quantity,
                                                                            storedCustomerId
                                                                        )
                                                                    }
                                                                }) {
                                                                    Icon(
                                                                        Icons.Default.Remove,
                                                                        contentDescription = "Decrease"
                                                                    )
                                                                }

                                                                Text(
                                                                    "${item.quantity}",
                                                                    fontWeight = FontWeight.Bold,
                                                                    modifier = Modifier.padding(
                                                                        horizontal = 8.dp
                                                                    )
                                                                )

                                                                IconButton(

                                                                    onClick = {
                                                                        val storedCustomerId =
                                                                            viewModel.getCustomerId()
                                                                        if (storedCustomerId != null) {
                                                                            viewModel.increaseItemQuantity(
                                                                                item.id,
                                                                                storedCustomerId
                                                                            )
                                                                        }
                                                                    }) {
                                                                    Icon(
                                                                        Icons.Default.Add,
                                                                        contentDescription = "Increase"
                                                                    )
                                                                }
                                                            }


                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                modifier = Modifier.padding(top = 4.dp)
                                                            ) {
                                                                if (isTestUser) {
                                                                    // Show only test stock for test users
                                                                    Text(
                                                                        text = "Test Stock: ${matchingStock?.test_stock_qty ?: "Unavailable"}",
                                                                        fontSize = 12.sp,
                                                                        color = Color.Blue
                                                                    )
                                                                } else {
                                                                    // Show only real stock for real users
                                                                    Text(
                                                                        text = "Stock: ${matchingStock?.stock_qty ?: "Unavailable"}",
                                                                        fontSize = 12.sp,
                                                                        color = Color.Green
                                                                    )
                                                                }
                                                            }
                                                        }

                                                        IconButton(onClick = {
                                                            viewModel.removeItem(
                                                                item.id
                                                            )
                                                        }) {
                                                            Icon(
                                                                imageVector = Icons.Default.Close,
                                                                contentDescription = "Remove Item",
                                                                tint = Color.Red
                                                            )
                                                        }
                                                    }
                                                    Divider()
                                                }
                                            } else {
                                                Text("No items in this suborder")
                                            }
                                        }
                                    }
                                }
                            } else {
                                item { Text("No suborders found") }
                            }
                        }
                        //Increase anD decrease
                        LaunchedEffect(viewModel.increaseMessage) {
                            viewModel.increaseMessage?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                viewModel.increaseMessage = null
                            }
                        }

                        LaunchedEffect(viewModel.decreaseMessage) {
                            viewModel.decreaseMessage?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                viewModel.decreaseMessage = null
                            }
                        }
// ✅ Reactively observe and trigger cart refresh when item is removed
                        LaunchedEffect(viewModel.removeStatus) {
                            viewModel.removeStatus?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                val storedCustomerId = viewModel.getCustomerId()
                                // ✅ Re-fetch the cart after successful item removal
                                if (storedCustomerId != null) {
                                    viewModel.fetchCartDetails(customerId = storedCustomerId)
                                }
                                // ✅ Reset the status so it doesn't re-trigger
                                viewModel.removeStatus = null
                            }
                        }

// ✅ Similarly handle remove error
                        LaunchedEffect(viewModel.errorMessageRemoveItemCart) {
                            viewModel.errorMessageRemoveItemCart?.let {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                viewModel.errorMessageRemoveItemCart = null
                            }
                        }


                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Amount: ${cartData.cart.total_amount}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.Blue
                            )
                            Button(
                                onClick = {
                                    val outOfStockItems = mutableListOf<String>()

                                    cartData.suborders.forEach { suborder ->
                                        suborder.items.forEach { item ->
                                            val matchingStock = stockData.find {
                                                it.item_detail_ID == item.item_detail_id && it.vendor_ID == suborder.vendor_ID && it.shop_ID == suborder.shop_ID && it.branch_ID == suborder.branch_ID
                                            }

                                            val availableQty = if (isTestUser) {
                                                matchingStock?.test_stock_qty ?: 0
                                            } else {
                                                matchingStock?.stock_qty ?: 0
                                            }

                                            if (item.quantity > availableQty) {
                                                outOfStockItems.add(item.item_name)
                                            }
                                        }
                                    }

//                                    if (outOfStockItems.isNotEmpty()) {
//                                        val message = "Out of stock:\n" + outOfStockItems.joinToString("\n") { "- $it" }
//                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//                                        return@Button
//                                    }

                                    if (outOfStockItems.isNotEmpty()) {
                                        val message =
                                            "The following items exceed stock:\n" + outOfStockItems.joinToString(
                                                "\n"
                                            ) { "- $it" }
                                        showDialogOutStock = true
                                        alertMessageOutStock = message
                                        return@Button
                                    }


                                    val jsonCartData = URLEncoder.encode(
                                        Gson().toJson(cartData), "UTF-8"
                                    ) // Convert cartData to JSON string
                                    val jsonCustomerData = URLEncoder.encode(
                                        Gson().toJson(customerData), "UTF-8"
                                    ) // Convert customerData to JSON

                                    navController.navigate("orderConfirmationScreen/${user.id}/${customer?.customerId}/$jsonCartData/$jsonCustomerData")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.pink))
                            ) {
                                Text(text = "Proceed to Checkout", color = Color.White)
                            }

                        }

                    }


                }
            }

            is CartState.Empty -> {
                val message = (cartState as CartState.Empty).message
                Text(text = message, color = Color.Red, fontSize = 16.sp)
            }

            is CartState.Error -> {
                val errorMessage = (cartState as CartState.Error).message
                Log.e("CartDebug", "Error loading cart: $errorMessage")

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage, color = Color.Red)
                }
            }
        }
    }

    LaunchedEffect(viewModel.clearCartState.collectAsState().value) {
        viewModel.clearCartState.value?.let { result ->
            result.onSuccess {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                viewModel.fetchCartDetails(user.id) // Refresh cart
            }.onFailure {
                Toast.makeText(context, "Failed to clear cart", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderConfirmationScreen(
    navController: NavHostController,
    userId: String,
    customerId: String,
    cartJson: String,
    customerJson: String,
    customerViewModel: CustomerViewModel = hiltViewModel(),
    viewModel: DeliveryBoyViewModel = hiltViewModel()
) {
    val cartData: CartResponse =
        Gson().fromJson(URLDecoder.decode(cartJson, "UTF-8"), CartResponse::class.java)

    val customerDataFetch: List<CustomerData> =
        Gson().fromJson(URLDecoder.decode(customerJson, "UTF-8"), Array<CustomerData>::class.java)
            .toList()

    val context = LocalContext.current

    val addressList by remember { derivedStateOf { customerViewModel.addressList } }
    val selectedAddress by remember { derivedStateOf { customerViewModel.selectedAddress } }

    LaunchedEffect(customerId) {
        customerViewModel.fetchAddresses(userId.toInt())
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedAddress?.street ?: "Select Address") }


    val customerData by customerViewModel.customerData.collectAsState()
    // Observe customer data and error messages
    LaunchedEffect(customerId) {
            customerViewModel.fetchCustomerMainScreen(customerId.toInt())
    }

    val categories = viewModel.vehicleCategories

    LaunchedEffect(Unit) {
        viewModel.loadVehicleCategories() // ✅ Make sure you load them
    }
    var estDeliveryCharges by remember { mutableStateOf<String?>(null) }


    Scaffold(topBar = {
        TopAppBar(title = { Text("Checkout", color = Color.White) }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id = R.color.pink)
        )
        )
    }) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(value = selectedText,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true },
                            label = { Text("Delivery Address") },
                            trailingIcon = {
                                Icon(imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                    contentDescription = "Dropdown Icon",
                                    modifier = Modifier.clickable { expanded = !expanded })
                            })

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            addressList.forEach { address ->
                                DropdownMenuItem(text = { Text(address.street) }, onClick = {
                                    selectedText = address.street
                                    customerViewModel.selectAddress(address)
                                    expanded = false
                                })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            navController.navigate("add_address/${userId.toInt()}")
                        },
                        colors = ButtonDefaults.buttonColors(Color.Gray),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text(text = "+ Add", color = Color.White)
                    }
                }

            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartData.suborders) { suborder ->
                    val pickupLatLng = customerData?.find { it.branchId == suborder.branch_ID }?.let {
                        Pair(it.latitude, it.longitude)
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Shop: ${suborder.shop_ID} - ${suborder.vendor_type}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            suborder.items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = item.item_name)
                                    Text(text = "Qty: ${item.quantity}")
                                    Text(text = "Rs.${item.price}")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Total: ${suborder.total_amount}")


                            Spacer(modifier = Modifier.height(8.dp))
                            val pickupLat = pickupLatLng?.first?.toDoubleOrNull()
                            val pickupLng = pickupLatLng?.second?.toDoubleOrNull()
                            val dropLat = selectedAddress?.latitude
                            val dropLng = selectedAddress?.longitude

                            val distanceInKm = if (
                                pickupLat != null && pickupLng != null &&
                                dropLat != null && dropLng != null
                            ) {
                                calculateDistanceInKm(pickupLat, pickupLng, dropLat, dropLng)
                            } else null

                            // 🔽 Pickup & Drop Location Section
                            Text(
                                text = "Pickup Location (Branch ID: ${suborder.branch_ID}):",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF37474F)
                            )
                            if (pickupLatLng != null) {
                                Text("Lat: ${pickupLatLng.first}  Lng: ${pickupLatLng.second}")
                            } else {
                                Text("Lat: N/A Lng: N/A")
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Drop Location (Customer Address):",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF37474F)
                            )
                            if (selectedAddress != null) {
                                Text("Lat: ${selectedAddress?.latitude}  Lng: ${selectedAddress?.longitude}")
                            } else {
                                Text("Lat: N/A Lng: N/A")
                            }

                            Text(
                                text = "Distance (Pickup → Drop):",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF37474F)
                            )
                            Text(
                                text = if (distanceInKm != null) "%.2f km".format(distanceInKm) else "N/A",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )

                            EstimatedCharges(
                                categories = categories,
                                distanceInKm = distanceInKm
                            ) { estimatedRange ->
                                estDeliveryCharges = estimatedRange
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (selectedAddress == null) {
                        Toast.makeText(
                            context, "Please select a delivery address", Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    val orderDetails = cartData.suborders.flatMap { suborder ->
                        suborder.items.map { item ->
                            OrderDetail(
                                vendor_id = suborder.vendor_ID,
                                shop_id = suborder.shop_ID,
                                branch_id = suborder.branch_ID,
                                item_detail_id = item.item_detail_id,
                                quantity = item.quantity,
                                price = item.price.toDouble()
                            )
                        }
                    }
                    val orderRequest = OrderRequest(
                        customer_id = customerId.toInt(),
                        delivery_address_id = selectedAddress?.id ?: 0,
                        order_details = orderDetails
                    )
                    customerViewModel.placeOrder(orderRequest, onSuccess = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }, onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.pink))
            ) {
                Text(text = "Proceed to Checkout", color = Color.White)
            }
        }
    }
}




private fun calculateDistanceInKm(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Double {
    val R = 6371.0 // Radius of Earth in KM

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return R * c // in kilometers
}


@Composable
private fun EstimatedCharges(
    categories: List<VehicleCategory>,
    distanceInKm: Double?,
    onSelect: (String) -> Unit
) {
    val minCharge = categories.minOfOrNull { it.per_km_charge.toDoubleOrNull() ?: 0.0 } ?: 0.0
    val maxCharge = categories.maxOfOrNull { it.per_km_charge.toDoubleOrNull() ?: 0.0 } ?: 0.0

    val estimatedMin = if (distanceInKm != null) (minCharge * distanceInKm).toInt() else 0
    val estimatedMax = if (distanceInKm != null) (maxCharge * distanceInKm).toInt() else 0

    val estimatedText = if (distanceInKm != null) {
        "Estimated Charges: $estimatedMin - $estimatedMax"
    } else {
        "N/A"
    }

    // Immediately invoke onSelect so the parent receives updated charges
    LaunchedEffect(estimatedText) {
        if (distanceInKm != null) {
            onSelect(estimatedText)
        }
    }
    // Show only the estimated charge in a readonly text field
    OutlinedTextField(
        value = estimatedText,
        onValueChange = {},
        readOnly = true,
        label = { Text("Est Delivery Charges") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}





