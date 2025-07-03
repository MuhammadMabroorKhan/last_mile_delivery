package com.example.lastmiledelivery.ui.customer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.customer.CustomerMainScreenResponse
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.common.ShopCategoryViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerMainScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    categoryViewModel: ShopCategoryViewModel = hiltViewModel(), // ✅ Multiple ViewModels can be used
    customerViewModel: CustomerViewModel = hiltViewModel()
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val user = remember { authViewModel.getUserDetails() }
    val isTestUser = remember {
        user.name.replace("_", "", ignoreCase = true).replace(" ", "", ignoreCase = true)
            .contains("testcustomer", ignoreCase = true)
    }
    val categories by categoryViewModel.categories.collectAsState()

    val customerData by customerViewModel.customerData.collectAsState()
    val errorMessage by customerViewModel.errorMessages.collectAsState()

    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    // Redirect to login if not logged in
    LaunchedEffect(Unit) {
        if (!authViewModel.isLoggedIn()) {
            navController.navigate("login") {
                popUpTo("customer") { inclusive = true }
            }
        }
    }

    // Trigger data fetch when the composable enters composition
    LaunchedEffect(key1 = user.id) {
        customerViewModel.fetchCustomerData(user.id)
    }

    LaunchedEffect(customerViewModel.customerState) {
        val storedCustomerId = customerViewModel.getCustomerId()
        Log.d("CustomerMainScreen", "Stored Customer ID: $storedCustomerId")
    }

    // Observe customer data and error messages
    val customer = customerViewModel.customerState
    val error = customerViewModel.errorMessage

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(customer?.customerId) {
        customer?.let {
            customerViewModel.fetchCustomerMainScreen(it.customerId)
        }
    }


    var currentLocation by remember { mutableStateOf<Location?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            getCurrentLocation(context) {
                currentLocation = it
            }
        }
    }
    LaunchedEffect(customerData) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            // Inside your location permission launcher result or LaunchedEffect
            getCurrentLocation(context) {
                if (it != null) {
                    currentLocation = it
                } else {
                    // Fallback to fresh location request
                    requestNewLocation(context) { freshLocation ->
                        currentLocation = freshLocation
                    }
                }
            }

        }
    }
    LaunchedEffect(currentLocation) {
        if (currentLocation == null) {
            Toast.makeText(
                context,
                "Please enable your location services (GPS or WiFi-based location).",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        DrawerContent(navController, drawerState, scope)
    }) {
        Scaffold(topBar = {
            TopAppBar(title = { Text("Dashboard", color = Color.White) }, navigationIcon = {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(
                        Icons.Filled.Menu,
                        contentDescription = "Menu",
                        tint = Color.White // ✅ Set icon color to white
                    )
                }
            }, actions = {
                // Favorite Icon
//                        IconButton(onClick = { }) {
//                            Icon(
//                                Icons.Filled.Favorite,
//                                contentDescription = "Favorite",
//                                tint = Color.White // ✅ Set icon color to white
//                            )
//                        }

                // Cart Icon
                IconButton(onClick = {
                    navController.navigate("cart")

                }) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = "Cart",
                        tint = Color.White, // ✅ Set icon color to white
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(id = R.color.pink)// ✅ Use a pink shade
            )
            )
        }) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Pink Background with Curved Bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp) // Adjust height for the curve effect
                        .background(
                            color = colorResource(id = R.color.pink), // Pink Shade
                            shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                        )
                )

                // Content Section (Scrollable)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp) // Adjust padding to prevent overlap with header
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hi! ${user.name}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White, // Ensure text is visible on pink background
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Search Here", color = Color.Gray
                            )
                        }, // Gray placeholder text
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Color.Gray // Gray icon
                            )
                        },
                        textStyle = TextStyle(color = Color.Black), // Input text color black
                        colors = TextFieldDefaults.textFieldColors(
                            cursorColor = Color.Black, // Cursor color black
                            focusedIndicatorColor = Color.Transparent, // Remove bottom border
                            unfocusedIndicatorColor = Color.Transparent, // Remove bottom border
                            containerColor = Color.White // White background for the field
                        ),
                        shape = RoundedCornerShape(20.dp), // Fully rounded field
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .height(50.dp) // Match height to image
                    )
                    // Category Selection Row
                    LazyRow(
                        modifier = Modifier.padding(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(categories) { category ->
                            CategoryButton(name = category.name,
                                id = category.id,
                                isSelected = selectedCategoryId == category.id,
                                onClick = {
                                    selectedCategoryId =
                                        if (selectedCategoryId == category.id) null else category.id
                                })
                        }
                    }

                    // White Section Starts Here
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                RoundedCornerShape(
                                    topStart = 40.dp, topEnd = 40.dp
                                )
                            ) // Top curve for transition
                            .background(Color.White)
                            .padding(16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val filteredShops = customerData?.filter { shop ->
                                val matchesCategory =
                                    selectedCategoryId?.let { it == shop.shopCategoryId } ?: true
                                val matchesSearchQuery =
                                    shop.shopName.contains(searchQuery, ignoreCase = true)

                                val isApiVendorOnly = if (isTestUser) {
                                    shop.vendorType.equals("API Vendor", ignoreCase = true)
                                } else {
                                    true // Non-test users see all
                                }
                                matchesCategory && matchesSearchQuery && isApiVendorOnly
                            }

// Display filtered shops
                            filteredShops?.forEach { shop ->
                                ShopCard(shop, customerViewModel, navController, currentLocation)
                            }

// If no shops match the filter
                            if (filteredShops != null && filteredShops.isEmpty()) {
                                Text(
                                    text = "No shops match your criteria.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }


                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryButton(name: String, id: Int, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) colorResource(id = R.color.pink) else Color.White
    val textColor = if (isSelected) Color.White else Color.Black

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        border = if (isSelected) BorderStroke(2.dp, Color.White) else null, // Add a thin border
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(text = name, color = textColor)
    }
}

@Composable
fun ShopCard(
    shop: CustomerMainScreenResponse,
    customerViewModel: CustomerViewModel,
    navController: NavController,
    currentLocation: Location?
) {
    val context = LocalContext.current

    val shopLat = shop.latitude.toDoubleOrNull()
    val shopLng = shop.longitude.toDoubleOrNull()

    val distanceKm = remember(currentLocation, shopLat, shopLng) {
        if (currentLocation != null && shopLat != null && shopLng != null) {
            calculateDistanceKm(
                currentLocation.latitude,
                currentLocation.longitude,
                shopLat,
                shopLng
            )
        } else null
    }


    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {

                customerViewModel.setSelectedShop(shop) // Store Selected Shop in ViewModel
                navController.navigate("shop_details") // Navigate to Details Screen
            }, // Added Clickable Action
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Shop Image
            Box {
                AsyncImage(
                    model = shop.branchPicture,
                    contentDescription = shop.shopName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )

                // Favorite Icon (Wishlist)
//                Icon(
//                    imageVector = Icons.Outlined.FavoriteBorder,
//                    contentDescription = "Favorite",
//                    tint = Color.Black,
//                    modifier = Modifier
//                        .align(Alignment.TopEnd)
//                        .padding(8.dp)
//                        .size(24.dp)
//                )
            }


            // Shop Details
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = shop.shopName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                // Category
                Text(
                    text = shop.shopDescription,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )

                // Rating Row with Star Icon
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        imageVector = Icons.Default.Star,
//                        contentDescription = "Rating",
//                        tint = colorResource(id = R.color.golden_star), // Gold color for the star
//                        modifier = Modifier.size(16.dp)
//                    )
//
//                    Text(
//                        text = "${shop.avgRating} (${shop.reviewsCount} Ratings)",
//                        style = MaterialTheme.typography.bodySmall,
//                        modifier = Modifier.padding(start = 4.dp)
//                    )
//                }

                // Rating and Distance Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = colorResource(id = R.color.golden_star),
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = "${shop.avgRating} (${shop.reviewsCount} Ratings)",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

//                    distanceKm?.let {
//                        Text(
//                            text = distanceKm?.let { "$it km" } ?: "-- km",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = Color.Gray
//                        )
//
//                    }
                    Text(
                        text = distanceKm?.let { "$it km" } ?: "-- km",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                }

            }
        }
    }
}

fun requestNewLocation(context: Context, onLocationResult: (Location?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.create().apply {
        priority = Priority.PRIORITY_HIGH_ACCURACY
        interval = 1000
        numUpdates = 1
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            Log.d(
                "Location",
                "Requested new location: ${location?.latitude}, ${location?.longitude}"
            )
            onLocationResult(location)
            fusedLocationClient.removeLocationUpdates(this) // stop updates after one
        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
        }
    }

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        onLocationResult(null)
        return
    }

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}

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

fun getCurrentLocation(context: Context, onLocationResult: (Location?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val finePermission =
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarsePermission =
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

    if (finePermission != PackageManager.PERMISSION_GRANTED &&
        coarsePermission != PackageManager.PERMISSION_GRANTED
    ) {
        Log.e("Location", "Location permissions not granted")
        onLocationResult(null)
        return
    }

    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            Log.d("Location", "Got location: ${location.latitude}, ${location.longitude}")
        } else {
            Log.e("Location", "Last location is null (maybe location is off or not available)")
        }
        onLocationResult(location)
    }.addOnFailureListener { exception ->
        Log.e("Location", "Failed to get location: ${exception.message}")
        onLocationResult(null)
    }
}


@Composable
private fun DrawerContent(
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
            route = "customer",
            drawerState = drawerState,
            scope = scope
        )

        DrawerItem(
            text = "Orders",
            icon = Icons.Filled.ReceiptLong,
            navController = navController,
            route = "customerOrders",
            drawerState = drawerState,
            scope = scope
        )


        DrawerItem(
            text = "Profile",
            icon = Icons.Filled.Person,
            navController = navController,
            route = "customerProfile",
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
fun DrawerItem(
    text: String,
    icon: ImageVector,
    navController: NavHostController,
    route: String,
    drawerState: DrawerState,
    scope: CoroutineScope,
    isLogout: Boolean = false,
    authViewModel: AuthViewModel? = null
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            if (isLogout) {
                authViewModel?.logout()
                navController.navigate(route) {
                    popUpTo("customer") { inclusive = true }
                }
            } else {
                navController.navigate(route)
            }
            scope.launch { drawerState.close() }
        }
        .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon, contentDescription = text, modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text, style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}


//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////


//Below Code is Working fine
//But it don't show distance

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CustomerMainScreen(
//    navController: NavHostController,
//    authViewModel: AuthViewModel = hiltViewModel(),
//    categoryViewModel: ShopCategoryViewModel = hiltViewModel(), // ✅ Multiple ViewModels can be used
//    customerViewModel: CustomerViewModel = hiltViewModel()
//) {
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//    val user = remember { authViewModel.getUserDetails() }
//    val isTestUser = remember {
//        user.name.replace("_", "", ignoreCase = true).replace(" ", "", ignoreCase = true)
//            .contains("testcustomer", ignoreCase = true)
//    }
//    val categories by categoryViewModel.categories.collectAsState()
//
//    val customerData by customerViewModel.customerData.collectAsState()
//    val errorMessage by customerViewModel.errorMessages.collectAsState()
//
//    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
//
//    // Redirect to login if not logged in
//    LaunchedEffect(Unit) {
//        if (!authViewModel.isLoggedIn()) {
//            navController.navigate("login") {
//                popUpTo("customer") { inclusive = true }
//            }
//        }
//    }
//
//    // Trigger data fetch when the composable enters composition
//    LaunchedEffect(key1 = user.id) {
//        customerViewModel.fetchCustomerData(user.id)
//    }
//
//    LaunchedEffect(customerViewModel.customerState) {
//        val storedCustomerId = customerViewModel.getCustomerId()
//        Log.d("CustomerMainScreen", "Stored Customer ID: $storedCustomerId")
//    }
//
//    // Observe customer data and error messages
//    val customer = customerViewModel.customerState
//    val error = customerViewModel.errorMessage
//
//    var searchQuery by remember { mutableStateOf("") }
//
//    LaunchedEffect(customer?.customerId) {
//        customer?.let {
//            customerViewModel.fetchCustomerMainScreen(it.customerId)
//        }
//    }
//
//    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
//        DrawerContent(navController, drawerState, scope)
//    }) {
//        Scaffold(topBar = {
//            TopAppBar(title = { Text("Dashboard", color = Color.White) }, navigationIcon = {
//                IconButton(onClick = { scope.launch { drawerState.open() } }) {
//                    Icon(
//                        Icons.Filled.Menu,
//                        contentDescription = "Menu",
//                        tint = Color.White // ✅ Set icon color to white
//                    )
//                }
//            }, actions = {
//                // Favorite Icon
////                        IconButton(onClick = { }) {
////                            Icon(
////                                Icons.Filled.Favorite,
////                                contentDescription = "Favorite",
////                                tint = Color.White // ✅ Set icon color to white
////                            )
////                        }
//
//                // Cart Icon
//                IconButton(onClick = {
//                    navController.navigate("cart")
//
//                }) {
//                    Icon(
//                        Icons.Filled.ShoppingCart,
//                        contentDescription = "Cart",
//                        tint = Color.White, // ✅ Set icon color to white
//                    )
//                }
//            }, colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = colorResource(id = R.color.pink)// ✅ Use a pink shade
//            )
//            )
//        }) { paddingValues ->
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues)
//            ) {
//                // Pink Background with Curved Bottom
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(220.dp) // Adjust height for the curve effect
//                        .background(
//                            color = colorResource(id = R.color.pink), // Pink Shade
//                            shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
//                        )
//                )
//
//                // Content Section (Scrollable)
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(top = 8.dp) // Adjust padding to prevent overlap with header
//                        .verticalScroll(rememberScrollState()),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "Hi! ${user.name}",
//                        style = MaterialTheme.typography.headlineMedium,
//                        color = Color.White, // Ensure text is visible on pink background
//                        modifier = Modifier
//                            .align(Alignment.Start)
//                            .padding(start = 16.dp)
//                    )
//                    Spacer(modifier = Modifier.height(10.dp))
//                    TextField(
//                        value = searchQuery,
//                        onValueChange = { searchQuery = it },
//                        placeholder = {
//                            Text(
//                                "Search Here", color = Color.Gray
//                            )
//                        }, // Gray placeholder text
//                        leadingIcon = {
//                            Icon(
//                                imageVector = Icons.Default.Search,
//                                contentDescription = "Search Icon",
//                                tint = Color.Gray // Gray icon
//                            )
//                        },
//                        textStyle = TextStyle(color = Color.Black), // Input text color black
//                        colors = TextFieldDefaults.textFieldColors(
//                            cursorColor = Color.Black, // Cursor color black
//                            focusedIndicatorColor = Color.Transparent, // Remove bottom border
//                            unfocusedIndicatorColor = Color.Transparent, // Remove bottom border
//                            containerColor = Color.White // White background for the field
//                        ),
//                        shape = RoundedCornerShape(20.dp), // Fully rounded field
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 10.dp)
//                            .height(50.dp) // Match height to image
//                    )
//                    // Category Selection Row
//                    LazyRow(
//                        modifier = Modifier.padding(8.dp),
//                        contentPadding = PaddingValues(horizontal = 8.dp)
//                    ) {
//                        items(categories) { category ->
//                            CategoryButton(name = category.name,
//                                id = category.id,
//                                isSelected = selectedCategoryId == category.id,
//                                onClick = {
//                                    selectedCategoryId =
//                                        if (selectedCategoryId == category.id) null else category.id
//                                })
//                        }
//                    }
//
//                    // White Section Starts Here
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .clip(
//                                RoundedCornerShape(
//                                    topStart = 40.dp, topEnd = 40.dp
//                                )
//                            ) // Top curve for transition
//                            .background(Color.White)
//                            .padding(16.dp),
//                        contentAlignment = Alignment.TopCenter
//                    ) {
//                        Column(
//                            modifier = Modifier.fillMaxSize(),
//                            verticalArrangement = Arrangement.spacedBy(10.dp)
//                        ) {
//                            val filteredShops = customerData?.filter { shop ->
//                                val matchesCategory =
//                                    selectedCategoryId?.let { it == shop.shopCategoryId } ?: true
//                                val matchesSearchQuery =
//                                    shop.shopName.contains(searchQuery, ignoreCase = true)
//
//                                val isApiVendorOnly = if (isTestUser) {
//                                    shop.vendorType.equals("API Vendor", ignoreCase = true)
//                                } else {
//                                    true // Non-test users see all
//                                }
//                                matchesCategory && matchesSearchQuery && isApiVendorOnly
//                            }
//
//// Display filtered shops
//                            filteredShops?.forEach { shop ->
//                                ShopCard(shop, customerViewModel, navController)
//                            }
//
//// If no shops match the filter
//                            if (filteredShops != null && filteredShops.isEmpty()) {
//                                Text(
//                                    text = "No shops match your criteria.",
//                                    style = MaterialTheme.typography.bodyMedium
//                                )
//                            }
//
//
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun CategoryButton(name: String, id: Int, isSelected: Boolean, onClick: () -> Unit) {
//    val backgroundColor = if (isSelected) colorResource(id = R.color.pink) else Color.White
//    val textColor = if (isSelected) Color.White else Color.Black
//
//    Button(
//        onClick = onClick,
//        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
//        border = if (isSelected) BorderStroke(2.dp, Color.White) else null, // Add a thin border
//        modifier = Modifier.padding(horizontal = 4.dp)
//    ) {
//        Text(text = name, color = textColor)
//    }
//}
//
//@Composable
//fun ShopCard(
//    shop: CustomerMainScreenResponse,
//    customerViewModel: CustomerViewModel,
//    navController: NavController
//) {
//    val context = LocalContext.current
//    Card(
//        shape = RoundedCornerShape(12.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//            .clickable {
//
//                customerViewModel.setSelectedShop(shop) // Store Selected Shop in ViewModel
//                navController.navigate("shop_details") // Navigate to Details Screen
//            }, // Added Clickable Action
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            // Shop Image
//            Box {
//                AsyncImage(
//                    model = shop.branchPicture,
//                    contentDescription = shop.shopName,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(150.dp)
//                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
//                    contentScale = ContentScale.Crop
//                )
//
//                // Favorite Icon (Wishlist)
////                Icon(
////                    imageVector = Icons.Outlined.FavoriteBorder,
////                    contentDescription = "Favorite",
////                    tint = Color.Black,
////                    modifier = Modifier
////                        .align(Alignment.TopEnd)
////                        .padding(8.dp)
////                        .size(24.dp)
////                )
//            }
//
//
//            // Shop Details
//            Column(modifier = Modifier.padding(12.dp)) {
//                Text(
//                    text = shop.shopName,
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold
//                )
//                // Category
//                Text(
//                    text = shop.shopDescription,
//                    style = MaterialTheme.typography.bodySmall,
//                    maxLines = 2
//                )
//
//                // Rating Row with Star Icon
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(
//                        imageVector = Icons.Default.Star,
//                        contentDescription = "Rating",
//                        tint = colorResource(id = R.color.golden_star), // Gold color for the star
//                        modifier = Modifier.size(16.dp)
//                    )
//
//                    Text(
//                        text = "${shop.avgRating} (${shop.reviewsCount} Ratings)",
//                        style = MaterialTheme.typography.bodySmall,
//                        modifier = Modifier.padding(start = 4.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun DrawerContent(
//    navController: NavHostController,
//    drawerState: DrawerState,
//    scope: CoroutineScope,
//    authViewModel: AuthViewModel = hiltViewModel()
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxHeight()
//            .width(250.dp)
//            .background(Color.White)
//            .padding(16.dp)
//    ) {
//        // 🔹 **Header (Centered)**
//        Text(
//            text = "Menu",
//            style = MaterialTheme.typography.headlineMedium.copy(
//                fontWeight = FontWeight.Bold,
//                color = colorResource(id = R.color.pink) // Pink color for style
//            ),
//            textAlign = TextAlign.Center,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 16.dp, bottom = 16.dp)
//        )
//
//        // 🔹 **Navigation Items**
//        DrawerItem(
//            text = "Dashboard",
//            icon = Icons.Filled.Home,
//            navController = navController,
//            route = "customer",
//            drawerState = drawerState,
//            scope = scope
//        )
//
//        DrawerItem(
//            text = "Orders",
//            icon = Icons.Filled.ReceiptLong,
//            navController = navController,
//            route = "customerOrders",
//            drawerState = drawerState,
//            scope = scope
//        )
//
//
//        DrawerItem(
//            text = "Profile",
//            icon = Icons.Filled.Person,
//            navController = navController,
//            route = "customerProfile",
//            drawerState = drawerState,
//            scope = scope
//        )
//
//        // 🔹 **Logout (Same Design as Other Items)**
//        DrawerItem(
//            text = "Logout",
//            icon = Icons.Default.ExitToApp,
//            navController = navController,
//            route = "login",
//            drawerState = drawerState,
//            scope = scope,
//            isLogout = true,
//            authViewModel = authViewModel
//        )
//    }
//}
//
//@Composable
//fun DrawerItem(
//    text: String,
//    icon: ImageVector,
//    navController: NavHostController,
//    route: String,
//    drawerState: DrawerState,
//    scope: CoroutineScope,
//    isLogout: Boolean = false,
//    authViewModel: AuthViewModel? = null
//) {
//    Row(modifier = Modifier
//        .fillMaxWidth()
//        .clickable {
//            if (isLogout) {
//                authViewModel?.logout()
//                navController.navigate(route) {
//                    popUpTo("customer") { inclusive = true }
//                }
//            } else {
//                navController.navigate(route)
//            }
//            scope.launch { drawerState.close() }
//        }
//        .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
//        Icon(
//            imageVector = icon, contentDescription = text, modifier = Modifier.size(24.dp)
//        )
//        Spacer(modifier = Modifier.width(12.dp))
//        Text(
//            text = text, style = MaterialTheme.typography.bodyLarge.copy(
//                fontWeight = FontWeight.Bold
//            )
//        )
//    }
//}


