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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.lastmiledelivery.data.models.organization.DeliveryBoy
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delivery Boy Register", color = Color.White) },
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
    )
    { paddingValues ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
//            Text("Delivery Boy Signup", fontSize = 24.sp, fontWeight = FontWeight.Bold)
//            Text("Organization ID${organizationId}", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(8.dp))
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
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") })
            OutlinedTextField(
                value = phoneNo,
                onValueChange = { phoneNo = it },
                label = { Text("Phone") })
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(value = cnic, onValueChange = { cnic = it }, label = { Text("CNIC") })
            OutlinedTextField(
                value = licenseNo,
                onValueChange = { licenseNo = it },
                label = { Text("License No") })
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
                readOnly = true
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

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = {},
                    label = { Text("City") },
                    readOnly = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = zipCode,
                    onValueChange = {},
                    label = { Text("Zip Code") },
                    readOnly = true,
                    modifier = Modifier.weight(1f)
                )
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
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
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
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.pink))
            ) {
                Text("Register", color = Color.White)
            }

            signupState?.let { result ->
                when {
                    result.isSuccess -> {
                        Toast.makeText(context, "Signup successful", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        deliveryBoyViewModel.clearDeliveryBoySignupState()
                    }

                    result.isFailure -> {
                        Toast.makeText(
                            context,
                            "Signup failed: ${result.exceptionOrNull()?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        deliveryBoyViewModel.clearDeliveryBoySignupState()
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryBoyListScreen(
    orgId: Int,
    navController: NavHostController,
    viewModel: OrganizationViewModel = hiltViewModel() // or pass manually if not using Hilt
) {
    val deliveryBoys = viewModel.deliveryBoyList
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    // Fetch data when screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchDeliveryBoys(orgId)
    }
    var selectedBoy by remember { mutableStateOf<DeliveryBoy?>(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DeliveryBoys", color = Color.White) },
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
    )

    { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 🔼 Button at the top
            Button(
                onClick = {
                    navController.navigate("organization_deliveryBoys/${orgId}")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.pink))
            ) {
                Text("Register DeliveryBoy", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔄 Main content area
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = errorMessage ?: "Unknown error",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                deliveryBoys.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "No delivery boys registered.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(deliveryBoys) { boy ->
//                            DeliveryBoyItem(deliveryBoy = boy)
//                            DeliveryBoyItem(deliveryBoy = boy, onClick = {
//                                selectedBoy = boy
//                            })
                            DeliveryBoyItem(
                                deliveryBoy = boy,
                                onClick = { selectedBoy = boy },
                                onViewEarnings = {
                                    navController.navigate("organization_deliveryboy_earnings/${orgId}/${boy.id}")
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
            selectedBoy?.let {
                DeliveryBoyDetailDialog(
                    deliveryBoy = it,
                    onDismiss = { selectedBoy = null }
                )
            }
        }
    }
}
//
//@Composable
//fun DeliveryBoyItem(deliveryBoy: DeliveryBoy, onClick: () -> Unit) {
//    Card(
//        shape = RoundedCornerShape(12.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick() }
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(16.dp)
//        ) {
//            AsyncImage(
//                model = deliveryBoy.profile_picture,
//                contentDescription = null,
//                modifier = Modifier
//                    .size(64.dp)
//                    .clip(CircleShape)
//            )
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            Column {
//                Text(text = deliveryBoy.name, fontWeight = FontWeight.Bold)
//                Text(text = deliveryBoy.email, style = MaterialTheme.typography.bodySmall)
//                Text(text = "Status: ${deliveryBoy.status}")
//            }
//        }
//    }
//}
@Composable
fun DeliveryBoyItem(deliveryBoy: DeliveryBoy, onClick: () -> Unit, onViewEarnings: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = deliveryBoy.profile_picture,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = deliveryBoy.name, fontWeight = FontWeight.Bold)
                    Text(text = deliveryBoy.email, style = MaterialTheme.typography.bodySmall)
                    Text(text = "Status: ${deliveryBoy.status}")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onViewEarnings,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.pink))
            ) {
                Icon(Icons.Default.Money, contentDescription = "View Earnings", tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("View Earnings", color = Color.White)
            }
        }
    }
}

@Composable
fun DeliveryBoyDetailDialog(
    deliveryBoy: DeliveryBoy,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Delivery Boy Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = { onDismiss() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                AsyncImage(
                    model = deliveryBoy.profile_picture,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Name: ${deliveryBoy.name}")
                Text("Email: ${deliveryBoy.email}")
                Text("Phone: ${deliveryBoy.phone_no}")
                Text("CNIC: ${deliveryBoy.cnic}")
                Text("Status: ${deliveryBoy.status}")
                Text("Approval: ${deliveryBoy.approval_status}")
                Text("Address: ${deliveryBoy.street}, ${deliveryBoy.city}, ${deliveryBoy.zip_code}, ${deliveryBoy.country}")
                Text("License #: ${deliveryBoy.license_no}")
                Text("License Expiry: ${deliveryBoy.license_expiration_date}")

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AsyncImage(
                        model = deliveryBoy.license_front,
                        contentDescription = "License Front",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    AsyncImage(
                        model = deliveryBoy.license_back,
                        contentDescription = "License Back",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

//@Composable
//fun DeliveryBoyItem(deliveryBoy: DeliveryBoy) {
//    Card(
//        shape = RoundedCornerShape(12.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(16.dp)
//        ) {
//            AsyncImage(
//                model = deliveryBoy.profile_picture,
//                contentDescription = null,
//                modifier = Modifier
//                    .size(64.dp)
//                    .clip(CircleShape)
//            )
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            Column {
//                Text(text = deliveryBoy.name, fontWeight = FontWeight.Bold)
//                Text(text = deliveryBoy.email, style = MaterialTheme.typography.bodySmall)
//                Text(text = "Status: ${deliveryBoy.status}")
//            }
//        }
//    }
//}


