package com.example.lastmiledelivery.ui.organization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavHostController
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.ui.common.AddressTypeDropdown
import com.example.lastmiledelivery.ui.common.PicturePicker
import com.example.lastmiledelivery.ui.common.ProfilePicturePicker
import com.example.lastmiledelivery.ui.common.uriToFile
import com.example.lastmiledelivery.viewmodels.organization.OrganizationViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrganizationDeliveryBoySignupScreen(
    navController: NavHostController,
    organizationId: String,
    deliveryBoyViewModel: OrganizationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val signupState by deliveryBoyViewModel.deliveryBoySignupState.observeAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phoneNo by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var cnic by rememberSaveable { mutableStateOf("") }

    var licenseNo by rememberSaveable { mutableStateOf("") }
    var licenseExpDate by rememberSaveable { mutableStateOf("") }
//    var licenseExpDate by remember { mutableStateOf("") }


    var addressType by rememberSaveable { mutableStateOf("Home") }
    var street by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var zipCode by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var longitude by rememberSaveable { mutableStateOf<Double?>(null) }

    var profilePictureUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var licenseFrontUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var licenseBackUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)

    // Observe location
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

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        Text("Delivery Boy Signup", fontSize = 24.sp, fontWeight = FontWeight.Bold)
   Text("Organization ID${organizationId}" , fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))
//        ProfilePicturePicker(profilePictureUri) { profilePictureUri = it }
//        PicturePicker(licenseFrontUri) { licenseFrontUri = it }
//        PicturePicker(licenseBackUri) { licenseBackUri = it }
        // Scrollable container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(16.dp)
                .horizontalScroll(rememberScrollState()) // Enable horizontal scroll
        ) {
            // Profile Picture Picker with label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp) // Padding between items
            ) {
                Text(text = "Profile Picture", style = MaterialTheme.typography.bodySmall)
                ProfilePicturePicker(profilePictureUri) { profilePictureUri = it }
            }

            // License Front Picker with label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp) // Padding between items
            ) {
                Text(text = "License Front", style = MaterialTheme.typography.bodySmall)
                PicturePicker(licenseFrontUri) { licenseFrontUri = it }
            }

            // License Back Picker with label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp) // Padding between items
            ) {
                Text(text = "License Back", style = MaterialTheme.typography.bodySmall)
                PicturePicker(licenseBackUri) { licenseBackUri = it }
            }
        }

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = phoneNo, onValueChange = { phoneNo = it }, label = { Text("Phone") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
        OutlinedTextField(value = cnic, onValueChange = { cnic = it }, label = { Text("CNIC") })
        OutlinedTextField(value = licenseNo, onValueChange = { licenseNo = it }, label = { Text("License No") })
//        OutlinedTextField(value = licenseExpDate, onValueChange = { licenseExpDate = it }, label = { Text("License Expiry Date") })
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                licenseExpDate = formatter.format(selectedDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        OutlinedTextField(
            value = licenseExpDate,
            onValueChange = { licenseExpDate = it },
            label = { Text("License Expiry Date") },
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange, // Use default icon
                        contentDescription = "Select Date"
                    )
                }
            },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        AddressTypeDropdown(addressType) { addressType = it }

        OutlinedTextField(
            value = street,
            onValueChange = {},
            label = { Text("Street Address") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { navController.navigate("map_picker") }) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Pick Location")
                }
            }
        )

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = city, onValueChange = {}, label = { Text("City") }, readOnly = true, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(value = zipCode, onValueChange = {}, label = { Text("Zip Code") }, readOnly = true, modifier = Modifier.weight(1f))
        }

      val profilePicturePart = profilePictureUri?.let { uri ->
            val file = uriToFile(uri, context) // Convert URI to File
            file?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profile_picture", it.name, requestFile)
            }
        }
        val licenseBackPart = licenseBackUri?.let { uri ->
            val file = uriToFile(uri, context) // Convert URI to File
            file?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("license_back", it.name, requestFile)
            }
        }
        val licenseFrontPart = licenseFrontUri?.let { uri ->
            val file = uriToFile(uri, context) // Convert URI to File
            file?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("license_front", it.name, requestFile)
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = {
            deliveryBoyViewModel.deliveryBoySignup(
                name.toRequestBody(),
                email.toRequestBody(),
                phoneNo.toRequestBody(),
                password.toRequestBody(),
                cnic.toRequestBody(),
                profilePicturePart!!,
                licenseNo.toRequestBody(),
                licenseExpDate.toRequestBody(),
                licenseFrontPart!!,
                licenseBackPart!!,
                addressType.toRequestBody(),
                street.toRequestBody(),
                city.toRequestBody(),
                zipCode.toRequestBody(),
                "Pakistan".toRequestBody(),
                latitude?.toString()?.toRequestBody(),
                longitude?.toString()?.toRequestBody(),
                organizationId.toRequestBody()
            )
        },
            colors=ButtonDefaults.buttonColors(colorResource(id = R.color.pink))
        ) {
            Text("Register", color = Color.White)
        }

        signupState?.let { result ->
            when {
                result.isSuccess -> {
                    Toast.makeText(context, "Signup successful", Toast.LENGTH_SHORT).show()
//                    navController.popBackStack()
                    deliveryBoyViewModel.clearDeliveryBoySignupState()
                }
                result.isFailure -> {
                    Toast.makeText(context, "Signup failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    deliveryBoyViewModel.clearDeliveryBoySignupState()
                }
            }
        }
    }
}





