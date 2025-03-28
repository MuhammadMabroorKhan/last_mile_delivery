package com.example.lastmiledelivery.ui.vendor

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.customer.ApiException
import com.example.lastmiledelivery.data.models.customer.CustomerSignupRequest
import com.example.lastmiledelivery.ui.common.AddressTypeDropdown
import com.example.lastmiledelivery.ui.common.ProfilePicturePicker
import com.example.lastmiledelivery.ui.common.uriToFile

import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@Composable
fun VendorSignupScreen(
    vendorViewModel: VendorViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val signupState by vendorViewModel.signupState.observeAsState()


    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var cnic by rememberSaveable { mutableStateOf("") }
    var vendorType by rememberSaveable { mutableStateOf("In-App Vendor") } // Default selection
    var expanded by remember { mutableStateOf(false) }
    var addressType by rememberSaveable { mutableStateOf("Home") }
    var street by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var zipCode by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var longitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var profilePictureUri by rememberSaveable { mutableStateOf<Uri?>(null) }


    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)

    LaunchedEffect(navController.currentBackStackEntry?.savedStateHandle) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Map<String, Any>>("selectedLocation")
            ?.observe(lifecycleOwner) { selectedLocation ->
                selectedLocation?.let {
                    street = it["street"] as? String ?: ""
                    latitude = it["latitude"] as? Double
                    longitude = it["longitude"] as? Double
                    city = it["city"] as? String ?: ""
                    zipCode = it["zipCode"] as? String ?: ""
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = "Vendor Regestration", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(2.dp)) // Add some spacing between rows
        ProfilePicturePicker(profilePictureUri) { selectedUri ->
            profilePictureUri = selectedUri
            Log.d("ProfilePicture", "Selected URI: ${selectedUri?.toString()}")
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
          //  modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
          //  modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
           // modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
           // modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))

        OutlinedTextField(
            value = cnic,
            onValueChange = { cnic = it },
            label = { Text("CNIC") },
          //  modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(2.dp))

        // Dropdown for Vendor Type
        Box() {
            OutlinedTextField(
                value = vendorType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Vendor Type") },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        Modifier.clickable { expanded = true })
                },
                modifier = Modifier
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                //modifier = Modifier.fillMaxWidth()
            ) {
                listOf("In-App Vendor", "API Vendor").forEach { type ->
                    DropdownMenuItem(
                        text = { Text(text = type) },
                        onClick = {
                            vendorType = type
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))

        AddressTypeDropdown(addressType = addressType) {
            addressType = it
        }

        Spacer(modifier = Modifier.height(2.dp)) // Add some spacing between rows
        // Street, City, and ZipCode will be auto-filled
        OutlinedTextField(
            value = street,
            onValueChange = { }, // ❌ Disable manual input
            label = { Text("Street Address") },
            trailingIcon = {
                IconButton(onClick = { navController.navigate("map_picker") }) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Pick Location")
                }
            },
            readOnly = true,
            modifier = Modifier.height(57.dp)
        )

        Spacer(modifier = Modifier.height(2.dp)) // Add some spacing between rows
        // First Row: City & Zip Code
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Spacer(modifier = Modifier.weight(0.06f)) // 6% space on the left

            OutlinedTextField(
                value = city,
                onValueChange = { },
                label = { Text("City") },
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .weight(0.44f) // Adjusted width
                    .height(57.dp)
            )

            OutlinedTextField(
                value = zipCode,
                onValueChange = { },
                label = { Text("Zip Code") },
                readOnly = true,
                singleLine = true,
                modifier = Modifier
                    .weight(0.44f) // Adjusted width
                    .height(57.dp)
            )

            Spacer(modifier = Modifier.weight(0.06f)) // 6% space on the right
        }

        Spacer(modifier = Modifier.height(2.dp)) // Add some spacing between rows

        // Second Row: Latitude & Longitude
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Spacer(modifier = Modifier.weight(0.06f)) // 6% space on the left
            latitude?.let {
                OutlinedTextField(
                    value = "$it",
                    onValueChange = { },
                    label = { Text("Lat") },
                    readOnly = true,
                    singleLine = true, // Ensures text stays in one line
                    modifier = Modifier
                            .alpha(0f) // Fully transparent
                        .height(1.dp) // Smallest possible height
                )
            }

            longitude?.let {
                OutlinedTextField(
                    value = "$it",
                    onValueChange = { },
                    label = { Text("Lng") },
                    readOnly = true,
                    singleLine = true, // Ensures text stays in one line
                    modifier = Modifier
                            .alpha(0f) // Fully transparent
                        .height(1.dp) // Smallest possible height
                )
            }
            Spacer(modifier = Modifier.weight(0.06f)) // 6% space on the left
        }


        val profilePicturePart = profilePictureUri?.let { uri ->
            val file = uriToFile(uri, context) // Convert URI to File
            file?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profile_picture", it.name, requestFile)
            }
        }
        Button(
            onClick = {
                val customer = CustomerSignupRequest(
                    name,
                    email,
                    phone,
                    password,
                    cnic,
                    addressType,
                    street,
                    city,
                    zipCode,
                    "Pakistan",
                    latitude,
                    longitude,
                    profilePicturePart
                )
                Log.d("customer", "customer: ${customer?.toString()}")

                val requestBody =
                    { value: String -> value.toRequestBody("text/plain".toMediaTypeOrNull()) }



                vendorViewModel.vendorSignup(
                    name = requestBody(name),
                    email = requestBody(email),
                    phoneNo = requestBody(phone),
                    password = requestBody(password),
                    cnic = requestBody(cnic),
                    profilePicture = profilePicturePart,
                    vendorType = requestBody(vendorType),
                    addressType = requestBody(addressType),
                    street = requestBody(street),
                    city = requestBody(city),
                    zipCode = requestBody(zipCode),
                    country = requestBody(country),
                    latitude?.toString()?.toRequestBody(),
                    longitude?.toString()?.toRequestBody(),
                )

                Toast.makeText(context, "Vendor registered successfully!", Toast.LENGTH_SHORT)
                    .show()
            },
            modifier = Modifier.fillMaxWidth(),

            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.pink))
        ) {
            Text(text = "Register", color = Color.White)
        }

        signupState?.let { result ->
            when {
                result.isSuccess -> {
                    val message = result.getOrNull()?.message ?: "Signup Successful"
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    navController.navigate("customer")

                    vendorViewModel.clearSignupState()
                }

                result.isFailure -> {
                    val error = result.exceptionOrNull()
                    when {
                        error is ApiException -> {
                            val errorMessage = error.message
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }

                        else -> {
                            Toast.makeText(context, "Signup failed", Toast.LENGTH_LONG).show()
                        }
                    }
                    vendorViewModel.clearSignupState()
                }
            }
        }

    }
}



