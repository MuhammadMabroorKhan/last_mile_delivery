package com.example.lastmiledelivery.ui.customer


import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.customer.Order
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import com.example.lastmiledelivery.viewmodels.customer.OrderUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrderHistory(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    customerViewModel: CustomerViewModel = hiltViewModel()
) {
    val user = remember { authViewModel.getUserDetails() }
    val orderState = customerViewModel.orderState


    LaunchedEffect(key1 = user.id) {
        customerViewModel.fetchCustomerData(user.id)
    }

    LaunchedEffect(customerViewModel.customerState) {
        val storedCustomerId = customerViewModel.getCustomerId()
        if (storedCustomerId != null) {
            customerViewModel.fetchCustomerOrders(storedCustomerId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders History", color = Color.White) },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val startDate = remember { mutableStateOf<LocalDate?>(null) }
            val endDate = remember { mutableStateOf<LocalDate?>(null) }
            val showStartDialog = remember { mutableStateOf(false) }
            val showEndDialog = remember { mutableStateOf(false) }

            val filteredOrders = remember(orderState, startDate.value, endDate.value) {
                if (orderState is OrderUiState.Success) {
                    orderState.orders.filter { order ->
                        val orderDate = try {
                            LocalDateTime.parse(order.order_date, formatter).toLocalDate()
                        } catch (e: Exception) {
                            null
                        }

                        orderDate != null &&
                                (startDate.value == null || !orderDate.isBefore(startDate.value)) &&
                                (endDate.value == null || !orderDate.isAfter(endDate.value))
                    }
                } else emptyList()
            }


            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Filter by Date", style = MaterialTheme.typography.titleMedium)
                    if (startDate.value != null || endDate.value != null) {
                        Text(
                            text = "Clear",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                startDate.value = null
                                endDate.value = null
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DatePickerField(
                        "Start Date",
                        startDate.value,
                        onClick = { showStartDialog.value = true },
                        modifier = Modifier.weight(1f)
                    )
                    DatePickerField(
                        "End Date",
                        endDate.value,
                        onClick = { showEndDialog.value = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (startDate.value != null || endDate.value != null) {
                    Text("Filtered Results:", modifier = Modifier.padding(top = 8.dp))
                }
            }

            if (showStartDialog.value) {
                val state = rememberDatePickerState(
                    initialSelectedDateMillis = startDate.value?.atStartOfDay(ZoneId.systemDefault())
                        ?.toInstant()?.toEpochMilli(),
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return !Instant.ofEpochMilli(utcTimeMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .isAfter(LocalDate.now())
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { showStartDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = {
                            state.selectedDateMillis?.let {
                                startDate.value =
                                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                            }
                            showStartDialog.value = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStartDialog.value = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = state)
                }
            }

            if (showEndDialog.value) {
                val state = rememberDatePickerState(
                    initialSelectedDateMillis = endDate.value?.atStartOfDay(ZoneId.systemDefault())
                        ?.toInstant()?.toEpochMilli(),
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            val date =
                                Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            return date <= LocalDate.now() &&
                                    (startDate.value == null || !date.isBefore(startDate.value))
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { showEndDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = {
                            state.selectedDateMillis?.let {
                                endDate.value =
                                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                            }
                            showEndDialog.value = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEndDialog.value = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = state)
                }
            }

            when (orderState) {
                is OrderUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is OrderUiState.Success -> {
                    val orders = orderState.orders

                    Column {

                        if (filteredOrders.isEmpty()) {
                            Text(
                                "No orders match the selected filters.",
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            LazyColumn {
                                items(filteredOrders) { order ->
                                    CustomerOrderHistoryCard(order, navController = navController)
                                }
                            }
                        }


                    }
                }

                is OrderUiState.Error -> {
                    Text("No Orders Found", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}


@Composable
private fun CustomerOrderHistoryCard(
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


@Composable
private fun DatePickerField(
    label: String,
    value: LocalDate?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateText = value?.format(formatter) ?: ""

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        OutlinedTextField(
            value = dateText,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = "Calendar")
            },
            modifier = Modifier
                .fillMaxWidth()
                // Disable text field interactions
                .pointerInput(Unit) {},
            enabled = false
        )
    }
}
