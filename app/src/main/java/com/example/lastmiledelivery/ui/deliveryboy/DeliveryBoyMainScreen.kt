package com.example.lastmiledelivery.ui.deliveryboy

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Typeface
import android.location.Location
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.deliveryboy.ReadySuborder
import com.example.lastmiledelivery.ui.vendor.StatusChip
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.common.ShopCategoryViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import com.example.lastmiledelivery.viewmodels.deliveryboy.DeliveryBoyViewModel
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DeliveryBoyMainScreen(
//    navController: NavHostController,
//    authViewModel: AuthViewModel = hiltViewModel(),
//    deliveryBoyViewModel: DeliveryBoyViewModel = hiltViewModel()
//) {
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//    val user = remember { authViewModel.getUserDetails() }
//
//
//    LaunchedEffect(Unit) {
//        if (!authViewModel.isLoggedIn()) {
//            navController.navigate("login") {
//                popUpTo("deliveryboy") { inclusive = true }
//            }
//        }
//    }
//
//
//    // Trigger data fetch when the composable enters composition
//    LaunchedEffect(key1 = user.id) {
//        deliveryBoyViewModel.getDeliveryBoyData(user.id)
//    }
//
//    LaunchedEffect(deliveryBoyViewModel.deliveryBoyState) {
//        val storedDeliveryBoyId = deliveryBoyViewModel.getDeliveryBoyID()
//        Log.d("DeliveryBoyMainScreen", "Stored DeliveryBoy Id: $storedDeliveryBoyId")
//    }
//    val deliveryBoyData = deliveryBoyViewModel.deliveryBoyState
//    val error = deliveryBoyViewModel.errorMessage
//
//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            deliveryBoyData?.delivery_boy_id?.let { deliveryBoyId ->
//                DrawerContent(deliveryBoyId=deliveryBoyId,user.id,navController, drawerState, scope)
//            }
//        }
//    ) {
//        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text("Dashboard", color = Color.White) },
//                    navigationIcon = {
//                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
//                            Icon(
//                                Icons.Filled.Menu,
//                                contentDescription = "Menu",
//                                tint = Color.White // ✅ Set icon color to white
//                            )
//                        }
//                    },
//                    actions = {
//                        // 🔔 Notification Icon
//                        IconButton(onClick = {
//                            Toast.makeText(
//                                context,
//                                "${deliveryBoyData?.delivery_boy_id}No new notifications ${user.id}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            deliveryBoyData?.delivery_boy_id?.let { deliveryBoyId ->
//                                navController.navigate("ready_suborders_deliveryBoyScreen/${deliveryBoyId}/${user.id}")
//                            }
//                        }) {
//                            Icon(
//                                imageVector = Icons.Filled.Notifications,
//                                contentDescription = "Notifications",
//                                tint = Color.White
//                            )
//                        }
//
//
//                        // Cart Icon
//                        IconButton(onClick = {
//                            navController.navigate("deliveryBoy_Profile")
//
//                        }) {
//                            AsyncImage(
//                                model = deliveryBoyData?.profile_picture,
//                                contentDescription = "Profile Picture",
//                                modifier = Modifier
//                                    .size(40.dp)
//                                    .clip(CircleShape)
//                                    .border(2.dp, Color.Gray, CircleShape),
//                                contentScale = ContentScale.Crop
//                            )
//                        }
//                    },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = colorResource(id = R.color.pink)// ✅ Use a pink shade
//                    )
//                )
//            }
//        ) { paddingValues ->
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues)
//            ) {
//                deliveryBoyData?.delivery_boy_id?.let {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(end = 8.dp),
//                        horizontalArrangement = Arrangement.End
//                    ) {
//                        StatusSwitchToggle(viewModel = deliveryBoyViewModel, deliveryBoyId = it)
//                    }
//                }
//
//                Text(text = "Welcome Delivery Boy", style = MaterialTheme.typography.headlineMedium)
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = "Hi! ${user.name}",
//                    style = MaterialTheme.typography.headlineMedium,
//                    modifier = Modifier
//                        .padding(start = 16.dp)
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                if (deliveryBoyData == null) {
//                    CircularProgressIndicator()
//                } else {
//
//                    deliveryBoyData.let {
//                        Text(
//                            text = "Hi! ${it.delivery_boy_id}",
//                            style = MaterialTheme.typography.headlineMedium,
//                            modifier = Modifier
//                                .padding(start = 16.dp)
//                        )
//                        Text(
//                            text = "Hi! ${it.approval_status}",
//                            style = MaterialTheme.typography.headlineMedium,
//                            modifier = Modifier
//                                .padding(start = 16.dp)
//                        )
//                        AsyncImage(
//                            model = it.profile_picture,
//                            contentDescription = "Profile Picture",
//                            modifier = Modifier
//                                .size(80.dp)
//                                .clip(CircleShape)
//                                .border(2.dp, Color.Gray, CircleShape),
//                            contentScale = ContentScale.Crop
//                        )
//                    }
//                }
//            }
//        }
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryBoyMainScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    deliveryBoyViewModel: DeliveryBoyViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val user = remember { authViewModel.getUserDetails() }


    LaunchedEffect(Unit) {
        if (!authViewModel.isLoggedIn()) {
            navController.navigate("login") {
                popUpTo("deliveryboy") { inclusive = true }
            }
        }
    }


    // Trigger data fetch when the composable enters composition
    LaunchedEffect(key1 = user.id) {
        deliveryBoyViewModel.getDeliveryBoyData(user.id)
    }

    LaunchedEffect(deliveryBoyViewModel.deliveryBoyState) {
        val storedDeliveryBoyId = deliveryBoyViewModel.getDeliveryBoyID()
        Log.d("DeliveryBoyMainScreen", "Stored DeliveryBoy Id: $storedDeliveryBoyId")
    }
    val deliveryBoyData = deliveryBoyViewModel.deliveryBoyState
//    val error = deliveryBoyViewModel.errorMessage


    val suborders = deliveryBoyViewModel.readySuborders
    val isLoading = deliveryBoyViewModel.isLoadingReadySuborders
    val error = deliveryBoyViewModel.errorMessageReadySuborders

    val response = deliveryBoyViewModel.acceptOrderResponse.value
    val loading = deliveryBoyViewModel.loading.value

    LaunchedEffect(Unit) {
        deliveryBoyViewModel.fetchReadySuborders(deliveryBoyId = user.id) // Replace with actual ID
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
                    deliveryBoyViewModel.startAutoRefreshReadyOrders(user.id)
                }

                Lifecycle.Event.ON_STOP -> {
                    deliveryBoyViewModel.stopAutoRefreshReadyOrders()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            deliveryBoyViewModel.stopAutoRefreshReadyOrders()
        }
    }


    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    // Fetch current location
    getCurrentLocation(context) { location ->
        currentLocation = location
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            deliveryBoyData?.delivery_boy_id?.let { deliveryBoyId ->
                DrawerContent(
                    deliveryBoyId = deliveryBoyId,
                    user.id,
                    navController,
                    drawerState,
                    scope
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Menu",
                                tint = Color.White // ✅ Set icon color to white
                            )
                        }
                    },
                    actions = {
                        // 🔔 Notification Icon
                        IconButton(onClick = {
                            Toast.makeText(
                                context,
                                "${deliveryBoyData?.delivery_boy_id}No new notifications ${user.id}",
                                Toast.LENGTH_SHORT
                            ).show()
                            deliveryBoyData?.delivery_boy_id?.let { deliveryBoyId ->
                                navController.navigate("ready_suborders_deliveryBoyScreen/${deliveryBoyId}/${user.id}")
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }


                        // Cart Icon
                        IconButton(onClick = {
                            navController.navigate("deliveryBoy_Profile")

                        }) {
                            AsyncImage(
                                model = deliveryBoyData?.profile_picture,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.Gray, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(id = R.color.pink)// ✅ Use a pink shade
                    )
                )
            }
        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (error != null) {
                    Text(text = "Error: $error", color = Color.Red)
                } else {
                    // Display the map and order list
                    currentLocation?.let { location ->
                        val pickupLocations = suborders.map { suborder ->
                            LatLng(
                                suborder.shop.branch.pickup_location.latitude.toDouble(),
                                suborder.shop.branch.pickup_location.longitude.toDouble()
                            )
                        }

                        // Create a map for order counts by branch ID
                        // Calculate the order count for each branch using the getOrdersCountByBranch function
                        val orderCountByBranch = getOrdersCountByBranch(suborders)

                        deliveryBoyData?.delivery_boy_id?.let { deliveryBoyId ->
//                            navController.navigate("ready_suborders_deliveryBoyScreen/${deliveryBoyId}/${user.id}")

                            // Show map with current location, multiple pickup locations, and order counts
                            DeliveryBoyMap(
                                navController=navController,
                                deliveryBoyId=deliveryBoyId,
                                lmdUserId=user.id,
                                currentLocation = location,
                                pickupLocations = pickupLocations, // Pass the list of pickup locations
                                orderCountByBranch = orderCountByBranch // Pass the order count map
                                , suborders = suborders
                            )
                        }
                    }
                    getOrdersCountByBranchs(suborders)
                }
                deliveryBoyData?.delivery_boy_id?.let {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp) // Add some padding from top and end
                            .zIndex(2f) // Ensure it's above everything else
                    ) {
                        StatusSwitchToggle(viewModel = deliveryBoyViewModel, deliveryBoyId = it)
                    }
                }
            }
        }
    }
}




@Composable
fun DeliveryBoyMap(
    navController: NavHostController,
    deliveryBoyId:Int,
    lmdUserId:Int,
    currentLocation: LatLng,
    pickupLocations: List<LatLng>,
    orderCountByBranch: Map<Int, Int>,
    suborders: List<ReadySuborder> // Accept suborders to find branch ID for each pickup location
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 10f)
    }
    val context = LocalContext.current
    val activity = context as? Activity
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

    }


    GoogleMap(
        cameraPositionState = cameraPositionState,
        modifier = Modifier.fillMaxSize(),
        onMapLoaded = {
            mapLoaded = true // triggers the LaunchedEffect above
        }
    ) {


        if (deliveryBoyIcon != null) {
            Marker(
                state = MarkerState(position = currentLocation),
                title = "Current Location",
                icon = deliveryBoyIcon

            )
        }


        // Loop through all pickup locations and place a marker for each
        pickupLocations.forEach { pickupLocation ->
            // Find the corresponding suborder for this pickup location
            val suborder = suborders.find { suborder ->
                LatLng(
                    suborder.shop.branch.pickup_location.latitude.toDouble(),
                    suborder.shop.branch.pickup_location.longitude.toDouble()
                ) == pickupLocation
            }

            // If suborder is found, get the branch ID and order count
            val branchId =
                suborder?.branch_ID ?: "Unknown" // Get the branch ID directly from the suborder
            val orderCount = orderCountByBranch[branchId] ?: 0 // Get the order count from the map

            val distanceKm =
                calculateDistanceKm(
                    pickupLocation.latitude,
                    pickupLocation.longitude,
                    currentLocation.latitude,
                    currentLocation.longitude
                )
            val customIcon =
                createMarkerWithText(context, R.drawable.storefront, distanceKm, orderCount)

//            if (pickupIcon != null) {
//                Marker(
//                    state = MarkerState(position = pickupLocation),
//                    title = "Pickup Location",
//                    icon = pickupIcon,
//                    snippet = "Branch $branchId , Distance :${distanceKm} km , Orders: $orderCount"
//                )
//            }
            Marker(
                state = MarkerState(position = pickupLocation),
                title = "Pickup Location",
                icon = customIcon,
                snippet = "Branch $branchId , Distance :${distanceKm} km , Orders: $orderCount",
                onClick ={
                    Toast.makeText(context,"BRANCH ID IS ${branchId} ",Toast.LENGTH_SHORT).show()
                            navController.navigate("ready_suborders_deliveryBoyScreenFromMapMarker/${deliveryBoyId}/${lmdUserId}/${branchId}")

//                    ready_suborders_deliveryBoyScreenFromMapMarker/{deliveryBoyID}/{lmdUserID}/{branchID}
                    true }
            )

        }
    }
}


@Composable
fun StatusSwitchToggle(
    viewModel: DeliveryBoyViewModel,
    deliveryBoyId: Int
) {
    val error = viewModel.errorMessageStatus
    val deliveryBoyState = viewModel.deliveryBoyState

    // Local toggle state
    var isOnline by remember { mutableStateOf(false) }

    // When status changes in state, update the switch
    LaunchedEffect(deliveryBoyState?.status) {
        isOnline = deliveryBoyState?.status == "Available"
    }

    // Initially load delivery boy data
    LaunchedEffect(deliveryBoyId) {
        viewModel.getDeliveryBoyData(deliveryBoyId)
    }

    val switchColor = colorResource(id = R.color.pink)

    Column(
        modifier = Modifier.padding(4.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isOnline) "Online" else "Offline",
                modifier = Modifier.padding(end = 8.dp),
                color = if (isOnline) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge
            )

            Switch(
                checked = isOnline,
                onCheckedChange = {
                    isOnline = it // update UI right away
                    viewModel.deliveryBoyToggleStatus(deliveryBoyId)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = switchColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }

    }
}


private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val radius = 6371 // Earth radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return radius * c // Distance in kilometers
}

@Composable
private fun getCurrentLocation(context: Context, onLocationReceived: (LatLng) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Check for permissions
    val permissionGranted = checkLocationPermission(context)
    if (permissionGranted) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                onLocationReceived(latLng)
            }
        }
    } else {
        requestLocationPermission(context)
    }
}

@Composable
private fun checkLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
private fun requestLocationPermission(context: Context) {
    ActivityCompat.requestPermissions(
        context as Activity,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        1
    )
}

@Composable
fun getOrdersCountByBranchs(suborders: List<ReadySuborder>) {
    val orderCountByBranch = suborders.groupBy { it.branch_ID }
        .mapValues { entry -> entry.value.size }

    // Using Column to display each branch's order count one below the other
    Column {
        orderCountByBranch.forEach { (branchId, orderCount) ->
            Text("Branch $branchId has $orderCount orders")
        }
    }
}

fun getOrdersCountByBranch(suborders: List<ReadySuborder>): Map<Int, Int> {
    return suborders.groupBy { it.branch_ID }
        .mapValues { entry -> entry.value.size }
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
        val canvas = android.graphics.Canvas(bitmap)
        vectorDrawable?.draw(canvas)

        // Return the bitmap as a BitmapDescriptor
        BitmapDescriptorFactory.fromBitmap(bitmap)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


private fun createMarkerWithText(
    context: Context,
    @DrawableRes iconRes: Int,
    distanceKm: Double,
    orderCount: Int
): BitmapDescriptor {
    val density = context.resources.displayMetrics.density

    val iconSize = (50 * density).toInt()
    val textWidth = (100 * density).toInt() // Fixed 100dp width for text
    val totalHeight = (100 * density).toInt() // Icon + text space

    // Create bitmap with enough width for text
    val bitmapWidth = maxOf(iconSize, textWidth)
    val bitmap = Bitmap.createBitmap(bitmapWidth, totalHeight, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // Draw the icon centered
    val drawable = ContextCompat.getDrawable(context, iconRes)
    val iconLeft = (bitmapWidth - iconSize) / 2
    drawable?.setBounds(iconLeft, 0, iconLeft + iconSize, iconSize)
    drawable?.draw(canvas)

    // Format distance (1 decimal place)
    val formattedDistance = String.format("%.1f", distanceKm)

    // Combine text
    val text = "Dis: $formattedDistance km\nOrders: $orderCount"

    // Setup paint
    val paint = TextPaint().apply {
        color = android.graphics.Color.BLACK
        textSize = 9 * density
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
    }

    // Create StaticLayout for multi-line text
    val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        StaticLayout.Builder
            .obtain(text, 0, text.length, paint, textWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()
    } else {
        @Suppress("DEPRECATION")
        StaticLayout(
            text, paint, textWidth, Layout.Alignment.ALIGN_CENTER,
            1f, 0f, false
        )
    }

    // Draw text below the icon
    val textY = iconSize + 4f
    canvas.save()
    canvas.translate((bitmapWidth - textWidth) / 2f, textY)
    staticLayout.draw(canvas)
    canvas.restore()

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}


@Composable
private fun DrawerContent(
    deliveryBoyId: Int,
    lmdUserId: Int,
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 🔹 **Header (Centered)**
        Text(
            text = "Menu",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.pink) // Pink color for style
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        )


        // 🔹 **Navigation Items**
        DrawerItem(
            text = "Dashboard",
            icon = Icons.Filled.Home,
            navController = navController,
            route = "deliveryboy",
            drawerState = drawerState,
            scope = scope
        )

        DrawerItem(
            text = "Suborders",
            icon = Icons.Filled.ReceiptLong,
            navController = navController,
            route = "deliveryBoySuborders/${deliveryBoyId}/${lmdUserId}",
            drawerState = drawerState,
            scope = scope
        )


        // 🔹 **Logout (Same Design as Other Items)**
        DrawerItem(
            text = "Logout",
            icon = Icons.Default.ExitToApp,
            navController = navController,
            route = "login",
            drawerState = drawerState,
            scope = scope,
            isLogout = true,
            authViewModel = authViewModel
        )
    }
}

@Composable
private fun DrawerItem(
    text: String,
    icon: ImageVector,
    navController: NavHostController,
    route: String,
    drawerState: DrawerState,
    scope: CoroutineScope,
    isLogout: Boolean = false,
    authViewModel: AuthViewModel? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isLogout) {
                    authViewModel?.logout()
                    navController.navigate(route) {
                        popUpTo("deliveryboy") { inclusive = true }
                    }
                } else {
                    navController.navigate(route)
                }
                scope.launch { drawerState.close() }
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

