package com.example.lastmiledelivery.ui.common

import android.app.AlertDialog
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import com.example.lastmiledelivery.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


@Composable
fun MapPickerScreen(navController: NavController) {
    var selectedLatitude by remember { mutableStateOf<Double?>(null) }
    var selectedLongitude by remember { mutableStateOf<Double?>(null) }
    var selectedStreet by remember { mutableStateOf("N/A") }
    var selectedCity by remember { mutableStateOf("N/A") }
    var selectedZipCode by remember { mutableStateOf("N/A") }
    var selectedAddress by remember { mutableStateOf("Select a location") }
    var permissionGranted by remember { mutableStateOf(false) }

    val context = LocalContext.current
    //val cameraPositionState = rememberCameraPositionState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(30.3753, 69.3451), 5f) // Pakistan's center with zoom level 5
    }
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Request location permission
    RequestLocationPermission {
        permissionGranted = true
    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            val hasLocationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasLocationPermission) {
                try {
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                LatLng(it.latitude, it.longitude), 15f
                            )
                        }
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }
    }
//    // Request location permission
//    RequestLocationPermission {
//        permissionGranted = true
//    }

    val updateAddress: (Double, Double) -> Unit = { lat, lon ->
        getAddressFromLocation(lat, lon, context) { street, city, zipCode, fullAddress ->
            selectedStreet = street
            selectedCity = city
            selectedZipCode = zipCode
            selectedAddress = fullAddress

            // Log values for debugging
            Log.d("MapPicker", "Latitude: $lat, Longitude: $lon")
            Log.d("MapPicker", "Street: $street, City: $city, Zip Code: $zipCode")
            Log.d("MapPicker", "Full: $fullAddress ")

        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        if (permissionGranted) {
            GoogleMap(
                modifier = Modifier.fillMaxSize().padding(top = 125.dp, bottom = 105.dp),
                properties = MapProperties(isMyLocationEnabled = true),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    // Update marker position to clicked location
                    selectedLatitude = latLng.latitude
                    selectedLongitude = latLng.longitude
                    updateAddress(latLng.latitude, latLng.longitude)
                }
            ) {
                selectedLatitude?.let { lat ->
                    selectedLongitude?.let { lon ->
                        Marker(
                            state = MarkerState(position = LatLng(lat, lon)),
                            title = "Selected Location"
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = "Lat: ${selectedLatitude ?: "N/A"}, Lng: ${selectedLongitude ?: "N/A"}\n" +
                        "Street: $selectedStreet\nCity: $selectedCity\nZip: $selectedZipCode",
                onValueChange = {},
                readOnly = true,
                label = { Text("Selected Address") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(10.dp, bottom = 35.dp),
            onClick = {
                if (selectedLatitude != null && selectedLongitude != null) {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(
                            "selectedLocation",
                            mapOf(
                                "latitude" to selectedLatitude!!,
                                "longitude" to selectedLongitude!!,
                                "street" to selectedStreet,
                                "city" to selectedCity,
                                "zipCode" to selectedZipCode,
                                "fullAddress" to selectedAddress
                            )
                        )
                    navController.popBackStack()

                } else {
                    Toast.makeText(context, "Please select a location", Toast.LENGTH_SHORT).show()
                }
            },
            colors= ButtonDefaults.buttonColors(colorResource(id=R.color.pink))
        ) {
            Text("Confirm Location",color=Color.White)
        }
    }
}

fun getAddressFromLocation(
    latitude: Double,
    longitude: Double,
    context: Context,
    onResult: (String, String, String, String) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        val geocoder = Geocoder(context, Locale.getDefault())

        suspend fun fetchAddress(lat: Double, lon: Double): Address? {
            return try {
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                addresses?.firstOrNull()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        var address = fetchAddress(latitude, longitude)

        // 🔄 Retry if address is null or missing key fields (offsetting lat/lng slightly)
        if (address == null || (address.thoroughfare == null && address.locality == null && address.postalCode == null)) {
            address = fetchAddress(latitude + 0.0002, longitude + 0.0002)
        }

        withContext(Dispatchers.Main) {
            if (address != null) {
                //val street = address.thoroughfare ?: "N/A"
                val street = address.getAddressLine(0) ?: "N/A"
                val city = address.locality ?: "N/A"
                val zipCode = address.postalCode ?: "N/A"
                val fullAddress = address.getAddressLine(0) ?: "N/A"

                Log.d("MapPicker", "Street: $street, City: $city, Zip Code: $zipCode, Full: $fullAddress")

                val missingFields = mutableListOf<String>()
                if (street == "N/A") missingFields.add("Street")
                if (city == "N/A") missingFields.add("City")
                if (zipCode == "N/A") missingFields.add("Zip Code")

                if (missingFields.isNotEmpty()) {
                    showAddressDialog(context, street, city, zipCode) { updatedStreet, updatedCity, updatedZipCode ->
                        onResult(updatedStreet, updatedCity, updatedZipCode, fullAddress)
                    }
                } else {
                    onResult(street, city, zipCode, fullAddress)
                }
            } else {
                showAddressDialog(context, "N/A", "N/A", "N/A") { updatedStreet, updatedCity, updatedZipCode ->
                    onResult(updatedStreet, updatedCity, updatedZipCode, "N/A")
                }
            }
        }
    }
}

fun showAddressDialog(
    context: Context,
    currentStreet: String,
    currentCity: String,
    currentZipCode: String,
    onSave: (String, String, String) -> Unit
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Complete Address Details")

    val layout = LinearLayout(context)
    layout.orientation = LinearLayout.VERTICAL

    var streetInput: EditText? = null
    var cityInput: EditText? = null
    var zipCodeInput: EditText? = null

    if (currentStreet == "N/A") {
        streetInput = EditText(context).apply { hint = "Enter Street" }
        layout.addView(streetInput)
    }
    if (currentCity == "N/A") {
        cityInput = EditText(context).apply { hint = "Enter City" }
        layout.addView(cityInput)
    }
    if (currentZipCode == "N/A") {
        zipCodeInput = EditText(context).apply {
            hint = "Enter ZIP Code"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        layout.addView(zipCodeInput)
    }

    builder.setView(layout)
    builder.setPositiveButton("Save") { _, _ ->
        val finalStreet = streetInput?.text?.toString()?.ifEmpty { "N/A" } ?: currentStreet
        val finalCity = cityInput?.text?.toString()?.ifEmpty { "N/A" } ?: currentCity
        val finalZipCode = zipCodeInput?.text?.toString()?.ifEmpty { "N/A" } ?: currentZipCode
        onSave(finalStreet, finalCity, finalZipCode)
    }
    builder.setNegativeButton("Cancel", null)
    builder.show()
}


@Composable
fun RequestLocationPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            Toast.makeText(context, "Location permission is required", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}





@Composable
fun AddressTypeDropdown(addressType: String, onAddressTypeChange: (String) -> Unit) {
    val options = listOf("Home", "Office") // Add more if needed
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = addressType,
            onValueChange = {},
            label = { Text("Address Type") },
            modifier = Modifier
                .height(57.dp)
                .clickable { expanded = true }, // Open dropdown on click
            readOnly = true, // Prevent manual typing
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onAddressTypeChange(option) // Update selected type
                        expanded = false
                    }
                )
            }
        }
    }
}




@Composable
fun ProfilePicturePicker(
    profilePictureUri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        onImageSelected(uri) // Update the selected image URI
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
            .clickable { launcher.launch("image/*") }, // Click to open gallery
        contentAlignment = Alignment.Center
    ) {
        if (profilePictureUri != null) {
            // Show selected image
            Image(
                painter = rememberAsyncImagePainter(profilePictureUri),
                contentDescription = "Selected Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Show default image (replace with your actual drawable)
            Image(
                painter = painterResource(id = R.drawable.account_circle), // Add a default image to res/drawable
                contentDescription = "Default Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(Color.LightGray) // Apply tint color
            )
        }
    }
}



@Composable
fun PicturePicker(
    profilePictureUri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        onImageSelected(uri) // Update the selected image URI
    }

    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.LightGray)
            .clickable { launcher.launch("image/*") }, // Click to open gallery
        contentAlignment = Alignment.Center
    ) {
        if (profilePictureUri != null) {
            // Show selected image
            Image(
                painter = rememberAsyncImagePainter(profilePictureUri),
                contentDescription = "Selected Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Show default image (replace with your actual drawable)
            Image(
                painter = painterResource(id = R.drawable.account_circle), // Add a default image to res/drawable
                contentDescription = "Default Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(Color.LightGray) // Apply tint color
            )
        }
    }
}
fun uriToFile(uri: Uri, context: Context): File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val file = File(context.cacheDir, "profile_picture.jpg") // Save as JPEG
    file.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return file
}

