package com.example.lastmiledelivery.ui.customer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.customer.AddAddressRequest
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    customerId: Int,
    viewModel: CustomerViewModel = hiltViewModel(),
    navController: NavHostController
) {
    var addressType by remember { mutableStateOf("Other") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("Pakistan") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val coroutineScope = rememberCoroutineScope()    // Delay navigation after a small wait

// Track permission state
    var permissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permissionGranted = isGranted
        }
    )

// On first composition, request permission if needed
    LaunchedEffect(Unit) {
        if (hasLocationPermission(context)) {
            permissionGranted = true
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

// When permission is granted, fetch current location
    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                latitude = location.latitude
                                longitude = location.longitude
                                Log.d("Location", "Lat: $latitude, Lng: $longitude")
                            } else {
                                Log.e("Location", "Location is null")
                            }
                        }
                } else {
                    Log.e("Location", "Permission not granted at runtime")
                }
            } catch (e: SecurityException) {
                Log.e("Location", "SecurityException: ${e.localizedMessage}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Address", color = Color.White) },
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
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                GoogleMapComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    onLocationSelected = { lat, lng, cityResult, stateResult, countryResult, streetResult, zipResult ->
                        latitude = lat
                        longitude = lng
                        city = cityResult ?: ""
                        country = countryResult ?: ""
                        street = streetResult ?: ""
                        zipCode = zipResult ?: ""
                        Log.d(
                            "FinalSelected",
                            "Lat: $lat, Lng: $lng, Street: $streetResult, Zip: $zipResult"
                        )
                    }

                )

                DropdownMenuBox(
                    selectedOption = addressType,
                    options = listOf("Other", "Home", "Work"),
                    onOptionSelected = { addressType = it }
                )

                // Row: Street & City
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = street,
                        onValueChange = { street = it },
                        label = { Text("Street") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row: Zip Code & Country
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = zipCode,
                        onValueChange = { zipCode = it },
                        label = { Text("Zip Code") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = { Text("Country") },
                        modifier = Modifier.weight(1f)
                    )
                }



                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    viewModel.addAddress(
                        customerId,
                        AddAddressRequest(
                            addressType,
                            street,
                            city,
                            zipCode,
                            country,
                            latitude,
                            longitude
                        )
                    )

                    // Delay navigation after a small wait
                    coroutineScope.launch {
                        delay(1500) // 1.5 seconds delay
                        navController.popBackStack()
                    }

                }, modifier = Modifier.fillMaxWidth()) {
                    if (viewModel.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    else Text("Submit")
                }

                viewModel.addAddressResponse?.let {
                    Text(it, color = Color.Green, modifier = Modifier.padding(8.dp))
                }
                viewModel.errorMessage?.let {
                    Text(it, color = Color.Red, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

// Helper Composable for Dropdown
@Composable
fun DropdownMenuBox(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text("Address Type") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown, contentDescription = null,
                    Modifier.clickable { expanded = true })
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(onClick = {
                    onOptionSelected(it)
                    expanded = false
                }, text = { Text(it) })
            }
        }
    }
}


private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}


@SuppressLint("ServiceCast")
@Composable
fun GoogleMapComposable(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
//    onLocationSelected: (lat: Double, lng: Double, city: String?, state: String?, country: String?) -> Unit
    onLocationSelected: (
        lat: Double, lng: Double, city: String?, state: String?,
        country: String?, street: String?, postalCode: String?
    ) -> Unit
) {
    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var hasPermission by remember { mutableStateOf(false) }
    var showEnableLocationDialog by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState()

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    // Ask for permission
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Check and fetch current location
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showEnableLocationDialog = true
                return@LaunchedEffect
            }

            try {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        currentLocation = latLng
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)

                        fetchAddressFromLatLng(
                            context,
                            latLng
                        ) { city, state, country, streetResult, postalCode ->
                            onLocationSelected(
                                latLng.latitude,
                                latLng.longitude,
                                city,
                                state,
                                country,
                                streetResult,
                                postalCode
                            )
                        }

                    }
                }
            } catch (e: SecurityException) {
                Log.e("Map", "Permission error: ${e.message}")
            }
        }
    }

    // Show dialog to enable GPS
    if (showEnableLocationDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Location Disabled") },
            text = { Text("Please enable location to use this feature.") },
            confirmButton = {
                TextButton(onClick = {
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    showEnableLocationDialog = false
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEnableLocationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Google Map
    GoogleMap(
        modifier = modifier,
        properties = MapProperties(isMyLocationEnabled = hasPermission),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            selectedLocation = latLng

            fetchAddressFromLatLng(
                context,
                latLng
            ) { city, state, country, streetResult, postalCode ->
                onLocationSelected(
                    latLng.latitude,
                    latLng.longitude,
                    city,
                    state,
                    country,
                    streetResult,
                    postalCode
                )
            }

        }
    ) {
        selectedLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Selected Location"
            )
        }
    }
}


fun fetchAddressFromLatLng(
    context: Context,
    latLng: LatLng,
    onAddressFetched: (
        city: String?,
        state: String?,
        country: String?,
        street: String?,
        postalCode: String?
    ) -> Unit
) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses =
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5) // Request more results
        if (!addresses.isNullOrEmpty()) {
            val mainAddress = addresses[0]
            val city = mainAddress.locality
            val state = mainAddress.adminArea
            val country = mainAddress.countryName
            val street = mainAddress.thoroughfare ?: mainAddress.featureName
            var postalCode = mainAddress.postalCode

            // Try to find nearest non-null postal code
            if (postalCode == null) {
                for (i in 1 until addresses.size) {
                    val altPostalCode = addresses[i].postalCode
                    if (!altPostalCode.isNullOrEmpty()) {
                        postalCode = altPostalCode
                        Log.d("Geocoder", "Fallback postal code from nearby address: $postalCode")
                        break
                    }
                }
            }

            Log.d(
                "Geocoder",
                "Selected Address -> City: $city, State: $state, Country: $country, Street: $street, PostalCode: $postalCode"
            )

            onAddressFetched(city, state, country, street, postalCode)
        } else {
            onAddressFetched(null, null, null, null, null)
        }
    } catch (e: Exception) {
        Log.e("Geocoder", "Error: ${e.localizedMessage}")
        onAddressFetched(null, null, null, null, null)
    }
}
