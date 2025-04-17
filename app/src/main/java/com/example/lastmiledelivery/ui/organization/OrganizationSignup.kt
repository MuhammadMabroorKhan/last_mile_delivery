package com.example.lastmiledelivery.ui.organization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lastmiledelivery.data.models.customer.ApiException
import com.example.lastmiledelivery.data.models.customer.CustomerSignupRequest
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.ui.common.AddressTypeDropdown
import com.example.lastmiledelivery.ui.common.ProfilePicturePicker
import com.example.lastmiledelivery.ui.common.uriToFile
import com.example.lastmiledelivery.viewmodels.organization.OrganizationViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody


@Composable
fun OrganizationSignup(
    organizationViewModel: OrganizationViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val signupState by organizationViewModel.signupState.observeAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phoneNo by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var cnic by rememberSaveable { mutableStateOf("") }
    var addressType by rememberSaveable { mutableStateOf("Home") }
    var street by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var zipCode by rememberSaveable { mutableStateOf("") }
    var profilePictureUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var latitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var longitude by rememberSaveable { mutableStateOf<Double?>(null) }


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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Organization Signup", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top=15.dp))
        Spacer(modifier = Modifier.height(4.dp)) // Add some spacing between rows
        ProfilePicturePicker(profilePictureUri) { selectedUri ->
            profilePictureUri = selectedUri
            Log.d("ProfilePicture", "Selected URI: ${selectedUri?.toString()}")
        }

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name Icon") }, modifier = Modifier.height(57.dp))
        Spacer(modifier = Modifier.height(5.dp)) // Add some spacing between rows
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") }, modifier = Modifier.height(57.dp))
        Spacer(modifier = Modifier.height(5.dp)) // Add some spacing between rows
        OutlinedTextField(value = phoneNo, onValueChange = { phoneNo = it }, label = { Text("Phone Number") }, leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone Icon") },modifier = Modifier.height(57.dp),keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Spacer(modifier = Modifier.height(5.dp)) // Add some spacing between rows
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") },   leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },modifier = Modifier.height(57.dp),visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(5.dp)) // Add some spacing between rows
        OutlinedTextField(value = cnic, onValueChange = { cnic = it }, label = { Text("CNIC") }, leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = "CNIC Icon") }, modifier = Modifier.height(57.dp),keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Spacer(modifier = Modifier.height(5.dp)) // Add some spacing between rows
        //OutlinedTextField(value = addressType, onValueChange = { addressType = it }, label = { Text("Address Type") }, modifier = Modifier.height(57.dp))
        AddressTypeDropdown(addressType = addressType) {
            addressType = it
        }

        Spacer(modifier = Modifier.height(5.dp)) // Add some spacing between rows
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

        Spacer(modifier = Modifier.height(5.dp)) // Add some spacing between rows
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

        Spacer(modifier = Modifier.height(4.dp)) // Add some spacing between rows

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
                    name, email, phoneNo, password, cnic, addressType, street, city, zipCode, "Pakistan", latitude, longitude, profilePicturePart
                )
                Log.d("customer", "customer: ${customer?.toString()}")
                organizationViewModel.organizationSignup(
                    name.toRequestBody(),
                    email.toRequestBody(),
                    phoneNo.toRequestBody(),
                    password.toRequestBody(),
                    cnic.toRequestBody(),
                    addressType.toRequestBody(),
                    street.toRequestBody(),
                    city.toRequestBody(),
                    zipCode.toRequestBody(),
                    "Pakistan".toRequestBody(),
                    latitude?.toString()?.toRequestBody(),
                    longitude?.toString()?.toRequestBody(),
                    profilePicturePart
                )
            },
            colors=ButtonDefaults.buttonColors(colorResource(id = R.color.pink))
        ) {
            Text("Sign Up", color = Color.White)
        }

        signupState?.let { result ->
            when {
                result.isSuccess -> {
                    val message = result.getOrNull()?.message ?: "Signup Successful"
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    navController.navigate("Organization")

                    organizationViewModel.clearSignupState()
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
                    organizationViewModel.clearSignupState()
                }
            }
        }
    }
}


