package com.example.lastmiledelivery.ui.deliveryboy

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.deliveryboy.AssignedSuborder
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.common.ShopCategoryViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import com.example.lastmiledelivery.viewmodels.deliveryboy.DeliveryBoyViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
// Android Location and Permissions
import android.Manifest

import android.content.pm.PackageManager


// Google Maps Compose
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition


// Location Services


// Jetpack Compose
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*

import android.app.Activity

import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap

import com.google.maps.android.compose.*
import kotlin.coroutines.suspendCoroutine

import android.location.Location
import kotlinx.coroutines.delay

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignedOrdersScreen(
    deliveryBoyID: Int,
    lmdUserID: Int,
    navController: NavHostController,
    viewModel: DeliveryBoyViewModel = hiltViewModel()
) {
    val assignedOrders = viewModel.assignedOrders
    val isLoading = viewModel.isLoadingassignedOrders
    val error = viewModel.errorMessageassignedOrders

    LaunchedEffect(Unit) {
        viewModel.fetchAssignedSuborders(deliveryBoyId = lmdUserID)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suborders", color = Color.White) },
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
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Text("Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
                }

                assignedOrders.isEmpty() -> {
                    Text("No assigned orders.", modifier = Modifier.padding(16.dp))
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(assignedOrders) { order ->
                            AssignedOrderCard(lmdUserID, order, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssignedOrderCard(lmdUserID: Int, order: AssignedSuborder, navController: NavHostController) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Order ID: ${order.orders_ID ?: "-"} | Suborder ID: ${order.suborder_id ?: "-"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Order Date: ${order.order_date ?: "-"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            LabelValue(label = "Status", value = order.status ?: "-", color = Color(0xFF1976D2))
            LabelValue(
                label = "Payment",
                value = order.payment_status ?: "-",
                color = Color(0xFF388E3C)
            )
            LabelValue(
                label = "Total",
                value = "Rs. ${order.total_amount ?: "-"}",
                color = Color(0xFF795548)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LabelValue(label = "Customer", value = order.customer?.name ?: "-")
            LabelValue(label = "Phone", value = order.customer?.phone ?: "-")

            LabelValue(
                label = "Pickup",
                value = "${order.shop?.branch?.pickup_location?.area ?: "-"}, ${order.shop?.branch?.pickup_location?.city ?: "-"}"
            )

            LabelValue(
                label = "Drop",
                value = "${order.customer?.delivery_address?.street ?: "-"}, ${order.customer?.delivery_address?.city ?: "-"}"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        "Tracking Suborder ID: ${order.suborder_id}, Order ID: ${order.orders_ID}",
                        Toast.LENGTH_SHORT
                    ).show()
//                    navController.currentBackStackEntry?.savedStateHandle?.set(
//                        "assigned_order",
//                        order
//                    )
//                    navController.navigate("tracking_screenDeliveryBoy")
                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("assigned_order", order)
                        set("delivery_boy_id", lmdUserID) // <-- Pass the lmd_user_id here
                    }

                    navController.navigate("tracking_screenDeliveryBoy")


                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Track Order")
            }
        }
    }
}

@Composable
fun LabelValue(label: String, value: String, color: Color = Color.Black) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(text = value)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuborderTrackingDeliveryBoyScreen(
    deliveryBoyID: Int,
    order: AssignedSuborder,
    navController: NavHostController,
    viewModel: DeliveryBoyViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }

     var orderStatus by remember { mutableStateOf(order.status ?: "-") }
     var orderPaymentStatus by remember { mutableStateOf(order.payment_status ?: "-") }
    LaunchedEffect(order.suborder_id) {
        order.suborder_id?.let { suborderId ->
            viewModel.getLatestLocation(suborderId)
        }
    }
    viewModel.latestLocation?.let {
        if (order.status.equals("handover_confirmed", true) || order.status.equals(
                "in_transit",
                true
            )
        ) {
            orderStatus = it.status ?: orderStatus
            orderPaymentStatus = order.payment_status ?: orderPaymentStatus
        }
    }
    var deliveryBoyIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var pickupIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var dropIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }

// Safely load icons after GoogleMap is initialized
    var mapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(mapLoaded) {
        if (mapLoaded) {
            deliveryBoyIcon = bitmapDescriptorFromVector(context, R.drawable.logo)
            pickupIcon = bitmapDescriptorFromVector(context, R.drawable.storefront)
            dropIcon = bitmapDescriptorFromVector(context, R.drawable.account_circle)
        }
        orderPaymentStatus = order.payment_status ?: orderPaymentStatus
    }

    // Check location settings and fetch location
    LaunchedEffect(Unit) {
        if (hasLocationPermission(context)) {
            val locationRequest = LocationRequest.create().apply {
                priority = Priority.PRIORITY_HIGH_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val client = LocationServices.getSettingsClient(context)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Now it's safe to call lastLocation
//                    fusedLocationClient.lastLocation
//                        .addOnSuccessListener { location ->
//                            if (location != null) {
//                                currentLatLng = LatLng(location.latitude, location.longitude)
//                            } else {
//                                locationError = "Unable to fetch current location."
//                            }
//                        }
                    val locationRequest = LocationRequest.create().apply {
                        interval = 5000 // 5 seconds
                        fastestInterval = 2000
                        priority = Priority.PRIORITY_HIGH_ACCURACY
                    }

                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            val location = locationResult.lastLocation
                            if (location != null) {
                                currentLatLng = LatLng(location.latitude, location.longitude)
                                locationError = null
                                fusedLocationClient.removeLocationUpdates(this) // stop after one good result
                            } else {
                                locationError = "Still trying to fetch current location..."
                            }
                        }
                    }

// Check permissions again here
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    } else {
                        locationError = "Location permission not granted."
                    }


                } else {
                    locationError = "Location permission not granted."
                }
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException && activity != null) {
                    try {
                        exception.startResolutionForResult(activity, 1001)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        sendEx.printStackTrace()
                        locationError = "Failed to enable location settings."
                    }
                } else {
                    locationError = "Location settings are not satisfied."
                }
            }
        } else {
            locationError = "Location permission not granted."
        }
    }

    val pickupLatLng = LatLng(
        order.shop?.branch?.pickup_location?.latitude?.toDoubleOrNull() ?: 0.0,
        order.shop?.branch?.pickup_location?.longitude?.toDoubleOrNull() ?: 0.0
    )

    val dropLatLng = LatLng(
        order.customer?.delivery_address?.latitude ?: 0.0,
        order.customer?.delivery_address?.longitude ?: 0.0
    )


    //////FOR UPDATE LOCATION
    LaunchedEffect(orderStatus) {
        delay(3000)
        if (orderStatus.equals("reached_destination", ignoreCase = true)) {
            viewModel.stopLocationUpdates()
        } else {
            order.suborder_id?.let {
                viewModel.startLocationUpdates(
                    it,
                    orderStatus
                ) {
                    // Replace this with actual location fetch logic
                    val location = getCurrentLocation(context)
                    Pair(location.latitude, location.longitude)
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopLocationUpdates()
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
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {


            Column(modifier = Modifier.fillMaxSize()) {
                Text(
//                    text = "Status: ${order.status ?: "-"}",
                    text = "Status: ${orderStatus}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD))
                        .padding(12.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0),
                    textAlign = TextAlign.Center
                )
                Text(
//                    text = "Status: ${order.status ?: "-"}",
                    text = "Payment Status: ${orderPaymentStatus}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD))
                        .padding(12.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0),
                    textAlign = TextAlign.Center
                )

                when {
                    currentLatLng != null -> {
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp),
                            onMapLoaded = {
                                mapLoaded = true // triggers the LaunchedEffect above
                            },
                            cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(currentLatLng!!, 9f)

                            }
                        ) {
                            if (deliveryBoyIcon != null) {
                                Marker(
                                    state = MarkerState(position = currentLatLng!!),
                                    title = "You (Delivery Boy)",
                                    icon = deliveryBoyIcon
                                )
                            }

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
                                    title = "Delivery Location",
                                    icon = dropIcon
                                )
                            }

                            // Add polyline regardless of icon state (optional)
                            Polyline(
                                points = listOf(currentLatLng!!, pickupLatLng),
                                color = Color.Blue,
                                width = 8f
                            )
                            Polyline(
                                points = listOf(pickupLatLng, dropLatLng),
                                color = Color.Gray,
                                width = 8f
                            )
                        }

                    }

                    locationError != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = locationError ?: "Location error",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

//                if (order.status.equals("assigned", ignoreCase = true)) {
                if (orderStatus.equals("assigned", ignoreCase = true)) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            order.suborder_id?.let {
                                viewModel.confirmPickup(
                                    it,
                                    currentLatLng!!.latitude,
                                    currentLatLng!!.longitude
                                )
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.pink))
                    ) {
                        if (viewModel.isLoadingpickupResponse) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Confirm Pickup", color = Color.White)
                        }
                    }


                    // In the success observer
                    viewModel.pickupResponse?.let {
                        orderStatus = "picked_up" // or whatever new status your API returns
//                            can show toast here
                    }

                    viewModel.pickupError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )

                        //                            can show toast here
                    }
                }


                //FOr reached Destination
                if (orderStatus.equals(
                        "handover_confirmed",
                        true
                    ) || orderStatus.equals("in_transit", true)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.stopLocationUpdates()

                            if (currentLatLng != null && order.suborder_id != null) {
                                order.deliveryboys_ID?.let {
                                    viewModel.reachDestination(
                                        deliveryBoyId = it,
                                        suborderId = order.suborder_id,
                                        lat = currentLatLng!!.latitude,
                                        lng = currentLatLng!!.longitude
                                    )
                                }
                            }

                            viewModel.latestLocation?.let {
                                if (order.status.equals(
                                        "handover_confirmed",
                                        true
                                    ) || order.status.equals(
                                        "in_transit",
                                        true
                                    ) || orderStatus.equals("in_transit", true)
                                ) {
                                    orderStatus = it.status ?: orderStatus
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.teal_700))
                    ) {
                        if (viewModel.isLoadingDestination) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Reached Destination", color = Color.White)
                        }
                    }

                    viewModel.reachDestinationResponse?.message?.let {
                        Text(
                            text = it,
                            color = Color.Green,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )

//                        orderStatus = "reached_destination"
                    }

                    viewModel.destinationError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }



//                orderPaymentStatus = order.payment_status ?: orderPaymentStatus
//                if (order.payment_status.equals("confirmed_by_customer", true)) {
                if (orderPaymentStatus.equals("confirmed_by_customer", true)) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            order.suborder_id?.let {
                                viewModel.confirmPaymentByDeliveryBoy(it)
                            }

                            orderPaymentStatus = "confirmed_by_deliveryboy"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.pink))
                    ) {
                        if (viewModel.isLoadingPaymentConfirm) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Confirm Recieved Payment", color = Color.White)
                        }
                    }

                    viewModel.paymentConfirmResponse?.let {
                        Text(
                            text = it,
                            color = Color.Green,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    viewModel.paymentConfirmError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }


            }
        }
    }
}


private fun hasLocationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}


fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorResId: Int): BitmapDescriptor? {
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


suspend fun getCurrentLocation(context: Context): Location {
    return suspendCoroutine { continuation ->
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // Permission Check
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted
            continuation.resume(Location("").apply {
                latitude = 0.0
                longitude = 0.0
            })
            return@suspendCoroutine
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    continuation.resume(Location("").apply {
                        latitude = 0.0
                        longitude = 0.0
                    })
                }
            }
            .addOnFailureListener {
                continuation.resume(Location("").apply {
                    latitude = 0.0
                    longitude = 0.0
                })
            }
    }
}
