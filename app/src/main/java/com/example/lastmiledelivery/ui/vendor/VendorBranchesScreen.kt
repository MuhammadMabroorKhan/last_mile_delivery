package com.example.lastmiledelivery.ui.vendor

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModelShops
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.vendor.Branch
import com.example.lastmiledelivery.ui.admin.BranchItem
import com.example.lastmiledelivery.ui.common.ProfilePicturePicker
import com.example.lastmiledelivery.ui.common.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModel
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import com.example.lastmiledelivery.data.models.Cities
import com.example.lastmiledelivery.ui.common.CityDropdown
import com.example.lastmiledelivery.viewmodels.admin.VendorApprovalViewModel
import com.example.lastmiledelivery.viewmodels.common.CitiesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorBranchesScreen(
    shopcategory_name: String,
    shopcategory_ID: Int,
    shopId: Int,
    shopName: String,
    shopDescription: String,
    navController: NavHostController,
    viewModel: VendorViewModelShops = hiltViewModel()
) {
    val branches by viewModel.branches.collectAsState()
    var showCreateBranchScreen by rememberSaveable { mutableStateOf(false) }
    var editingBranch by remember { mutableStateOf<Branch?>(null) }
    // Fetch branches when this screen is opened
    LaunchedEffect(shopId) {
        viewModel.fetchBranches(shopId)
    }


    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    LaunchedEffect(navController.currentBackStackEntry?.savedStateHandle) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Map<String, Any>>("selectedLocation")
            ?.observe(lifecycleOwner.value) { selectedLocation ->
                if (selectedLocation != null) {
                    showCreateBranchScreen = true // ✅ Ensure the dialog is shown again
                }
            }
    }



    if (showCreateBranchScreen) {
        Dialog(onDismissRequest = { showCreateBranchScreen = false }) {
            Surface(shape = MaterialTheme.shapes.medium) {
                CreateBranchScreen(
                    shopId = shopId,
                    onBack = { showCreateBranchScreen = false },
                    navController = navController
                )
            }
        }
    }



    editingBranch?.let { branch ->
        EditBranchDialog(
            branch = branch,
            onDismiss = { editingBranch = null },
            onUpdate = { branchId, latitude, longitude, description, openingHours, closingHours, contactNumber, _, _, _, shopId, branchPicture ->

                fun String?.toPlainTextRequestBody(): RequestBody? =
                    this?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())


                viewModel.updateBranch(
                    branchId,
                    latitude ?: "".toPlainTextRequestBody()!!,
                    longitude ?: "".toPlainTextRequestBody()!!,
                    description,
                    openingHours ?: "".toPlainTextRequestBody()!!,
                    closingHours ?: "".toPlainTextRequestBody()!!,
                    contactNumber ?: "".toPlainTextRequestBody()!!,
                    null, null, null,
                    shopId ?: "".toPlainTextRequestBody()!!,
                    branchPicture
                )


                editingBranch = null
            },
            shopsId = shopId
        )
    }



    Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(shopName) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ){
        Text(
                    text = shopDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )

                // "Create Branch" Button
                Button(
                    onClick = { showCreateBranchScreen = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.pink)) // Pink color
                ) {
                    Icon(
                        imageVector = Icons.Filled.Storefront, // Shop-related icon
                        contentDescription = "Create Branch",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                    Text(
                        text = "Create Branch",
                        color = Color.White, // White text for contrast
                        fontWeight = FontWeight.Bold
                    )
                }
                branches?.let { branchList ->
                    LazyColumn {
                        items(branchList) { branch ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                // BranchItem takes full width
                                BranchItem(shopcategory_name,shopcategory_ID,branch,shopId,navController)

                                // Edit Icon positioned on top-right
                                IconButton(
                                    onClick = { editingBranch = branch },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd) // Place it at the top-right
                                        .padding(8.dp) // Add padding for spacing
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit Branch")
                                }
                            }
                        }
                    }



                } ?: Text(
                    text = "No branches found",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
    }
}

@Composable
fun BranchItem(
    shopcategory_name: String,
    shopcategory_ID: Int,
    branch: Branch,
    shopId: Int,
    navController: NavHostController,
    viewModel: VendorViewModel = hiltViewModel(),
    viewModels: VendorViewModelShops = hiltViewModel()
) {
    val vendorState by viewModel.vendorData.observeAsState()
    val toggleStatus by viewModels.toggleBranchStatus.observeAsState()
    var showDialog by remember { mutableStateOf(false) }
    var isBranchActive by remember { mutableStateOf(branch.status == "active") }
    val context = LocalContext.current

    LaunchedEffect(viewModel.getVendorId()) {
        viewModel.getVendorId()?.let { viewModel.getVendorData(it) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                if (branch.approval_status.equals("pending", ignoreCase = true)) {
                    Toast.makeText(context, "Branch approval is pending", Toast.LENGTH_SHORT).show()
                    return@clickable
                } else if (branch.approval_status.equals("rejected", ignoreCase = true)) {
                    showDialog = true
                    return@clickable
                }

                vendorState?.let { result ->
                    result.fold(
                        onSuccess = { vendor ->
                            when (vendor.vendorType) {
                                "API Vendor" -> navController.navigate("API_VendorItemsScreen/$shopcategory_name/$shopcategory_ID/${vendor.vendorId}/${branch.branch_id}/$shopId/${branch.approval_status}")
                                "In-App Vendor" -> navController.navigate("IN_APP_VendorItemsScreen/$shopcategory_name/$shopcategory_ID/${vendor.vendorId}/${branch.branch_id}/$shopId/${branch.approval_status}")
                                else -> Log.d("Navigation", "Unknown vendor type")
                            }
                        },
                        onFailure = { exception ->
                            Log.e("Navigation", "Error fetching vendor: ${exception.message}")
                        }
                    )
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            AsyncImage(
                model = branch.branch_picture.takeIf { !it.isNullOrBlank() } ?: R.drawable.storefront,
                contentDescription = branch.description,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = branch.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = branch.contact_number ?: "No Contact available",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Branch Active")
                Switch(
                    checked = isBranchActive,
                    onCheckedChange = {
                        isBranchActive = it
                        viewModels.toggleBranchStatus(branch.branch_id)
                    }
                )
            }
        }

        if (showDialog) {
            RejectionReasonDialog(branchId = branch.branch_id, onDismiss = { showDialog = false })
        }
    }
}

@Composable
fun RejectionReasonDialog(branchId: Int, onDismiss: () -> Unit, viewModel: VendorApprovalViewModel = hiltViewModel()) {
    val rejectionReasons by viewModel.branchRejectionReasons.collectAsState()

    LaunchedEffect(branchId) {
        viewModel.getBranchRejectionReasons(branchId) // ✅ Fetch rejection reasons
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Rejection Reasons", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                if (rejectionReasons.isNotEmpty()) {
                    Text("Update Your Branch Details")
                    rejectionReasons.forEach { reason ->
                        Text("• ${reason.reason}")
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                } else {
                    Text("No rejection reasons found.")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }, colors = ButtonDefaults.buttonColors(Color.Gray)) {
                Text("Close")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBranchScreen(
    shopId: Int,
    onBack: () -> Unit,
    navController: NavHostController,
    viewModel: VendorViewModelShops = hiltViewModel(),citiesViewModel: CitiesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var cityId by remember { mutableStateOf("") }
    var areaName by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    var branchPicture: MultipartBody.Part? by remember { mutableStateOf(null) }

    var openingHours by remember { mutableStateOf("") }
    var closingHours by remember { mutableStateOf("") }




    val cities by citiesViewModel.cities.collectAsState()

    LaunchedEffect(Unit) {
        citiesViewModel.getAllCities()
    }

    var selectedCity by remember { mutableStateOf<Cities?>(null) }



//    // Elsewhere, you can access both the name and id of the selected city:
//    selectedCity?.let { city ->
//        Text("Selected City: ${city.name}")
//        Text("City ID: ${city.id}")
//    }




    val showOpeningTimePicker = rememberTimePickerDialog(context) { selectedTime ->
        openingHours = selectedTime
    }

    val showClosingTimePicker = rememberTimePickerDialog(context) { selectedTime ->
        closingHours = selectedTime
    }

    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)

    LaunchedEffect(navController.currentBackStackEntry?.savedStateHandle) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Map<String, Any>>("selectedLocation")
            ?.observe(lifecycleOwner) { selectedLocation ->
                selectedLocation?.let {
                    street = it["street"] as? String ?: ""
                    latitude = (it["latitude"] as? Double).toString()
                    longitude = (it["longitude"] as? Double).toString()
             //       city = it["city"] as? String ?: ""
                    postalCode = it["zipCode"] as? String ?: ""
                }
            }
    }




    Scaffold(
        topBar = {
            TopAppBar(
                title = {
//                    Text("Create Branch")
                    Text(
                        text = "Create Branch",
                        modifier = Modifier.fillMaxWidth()
                    )
                        },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Make it scrollable,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Input Fields
            Spacer(modifier = Modifier.height(5.dp)) // Add some spacing between rows

            // Profile Picture Picker
            ProfilePicturePicker(profilePictureUri) { selectedUri ->
                profilePictureUri = selectedUri
                Log.d("ProfilePicture", "Selected URI: ${selectedUri?.toString()}")

                // Convert URI to MultipartBody.Part
                val file = selectedUri?.let { uriToFile(it, context) }
                file?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    branchPicture = MultipartBody.Part.createFormData("branch_picture", it.name, requestFile)

                    // Log Multipart Data
                    Log.d("ProfilePicture", "File Name: ${it.name}, Size: ${it.length()} bytes")
                }
            }

            // Street, City, and ZipCode will be auto-filled
            OutlinedTextField(
                value = street,
                onValueChange = { }, // ❌ Disable manual input
                label = { Text("Area") },
                trailingIcon = {
                    IconButton(onClick = { navController.navigate("map_picker") }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Pick Location")
                    }
                },
                readOnly = true,
                modifier = Modifier.height(57.dp)
            )




//////////////////HIDE FROM SCREEN //////////////////////////////////////

                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    modifier = Modifier
                        .alpha(0f) // Fully transparent
                        .height(1.dp) // Smallest possible height
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    modifier = Modifier
                        .alpha(0f) // Fully transparent
                        .height(1.dp) // Smallest possible height
                    )
//////////////////HIDE FROM SCREEN //////////////////////////////////////





            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                minLines = 3  // Minimum 2 lines visible by default
            )


            OutlinedTextField(
                value = openingHours,
                onValueChange = {},
                label = { Text("Opening Time") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showOpeningTimePicker() }) {
                        Icon(Icons.Default.AccessTime, contentDescription = "Select Opening Time")
                    }
                }
            )

            OutlinedTextField(
                value = closingHours,
                onValueChange = {},
                label = { Text("Closing Time") },
                readOnly = true,

                trailingIcon = {
                    IconButton(onClick = { showClosingTimePicker() }) {
                        Icon(Icons.Default.AccessTime, contentDescription = "Select Closing Time")
                    }
                }
            )
            OutlinedTextField(value = contactNumber, onValueChange = { contactNumber = it }, label = { Text("Contact Number") })

           // OutlinedTextField(value = cityId, onValueChange = { cityId = it }, label = { Text("City ID") })
            // If cities are available, show the dropdown; otherwise, show a loader.
            if (cities != null) {
                CityDropdown(
                    cities = cities!!,
                    selectedCity = selectedCity,
                    onCitySelected = { selectedCity = it
                    cityId= it.id.toString()
                        Log.d("city","$cityId")
                    }
                )
            } else {
                CircularProgressIndicator()
            }

            OutlinedTextField(value = postalCode, onValueChange = { postalCode = it }, label = { Text("Postal Code") })

            areaName=street

            // Confirm Button
            Button(
                onClick = {
                    val requestBody = { text: String -> text.toRequestBody("text/plain".toMediaTypeOrNull()) }

                    if (branchPicture.toString().isNullOrEmpty()) {

                        Toast.makeText(context, "Select Branch PIcture required!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Validation Check
                    if (latitude.isBlank() || longitude.isBlank() || description.isBlank() ||
                        openingHours.isBlank() || closingHours.isBlank() || contactNumber.isBlank() ||
                        cityId.isBlank() || areaName.isBlank() || postalCode.isBlank()) {

                        Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }



                    // Time Validation: Opening time must be before closing time
                    val openingTime = openingHours.split(":").map { it.toInt() }
                    val closingTime = closingHours.split(":").map { it.toInt() }
                    if (openingTime[0] > closingTime[0] || (openingTime[0] == closingTime[0] && openingTime[1] >= closingTime[1])) {
                        Toast.makeText(context, "Opening time must be before closing time!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Log Data
                    Log.d(
                        "CreateBranchData",
                        "Latitude: $latitude, Longitude: $longitude, Description: $description, " +
                                "Opening Hours: $openingHours, Closing Hours: $closingHours, Contact Number: $contactNumber, " +
                                "City ID: $cityId, Area Name: $areaName, Postal Code: $postalCode, Shop ID: $shopId, " +
                                "Branch Picture: ${branchPicture}"
                    )

                    // Call API (Uncomment when needed)
                     viewModel.createBranch(
                        latitude = requestBody(latitude),
                        longitude = requestBody(longitude),
                        description = requestBody(description),
                        openingHours = requestBody(openingHours),
                        closingHours = requestBody(closingHours),
                        contactNumber = requestBody(contactNumber),
                        cityId = requestBody(cityId),
                        areaName = requestBody(areaName),
                        postalCode = requestBody(postalCode),
                        shopsId = requestBody(shopId.toString()),
                        branchPicture = branchPicture
                    )

                    // Clear all fields after submission
                    latitude = ""
                    longitude = ""
                    description = ""
                    openingHours = ""
                    closingHours = ""
                    contactNumber = ""
                    cityId = ""
                    areaName = ""
                    postalCode = ""
                    profilePictureUri = null
                    branchPicture = null


                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text("Confirm",color=Color.White)
            }
        }
    }
}


@Composable
fun rememberTimePickerDialog(
    context: Context,
    onTimeSelected: (String) -> Unit
): () -> Unit { // Returns a function to show the dialog
    return {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                onTimeSelected(formattedTime)
            },
            hour,
            minute,
            true
        ).show()
    }
}


@Composable
fun EditBranchDialog(
    branch: Branch,
    onDismiss: () -> Unit,
    onUpdate: (
        Int, RequestBody?, RequestBody?, RequestBody?, RequestBody?, RequestBody?, RequestBody?,
        RequestBody?, RequestBody?, RequestBody?, RequestBody?, MultipartBody.Part?
    ) -> Unit,
    shopsId: Int
) {
    var latitude by remember { mutableStateOf(branch.latitude ?: "") }
    var longitude by remember { mutableStateOf(branch.longitude ?: "") }
    var description by remember { mutableStateOf(branch.description ?: "") }
    var contactNumber by remember { mutableStateOf(branch.contact_number ?: "") }
    var openingHours by remember { mutableStateOf(branch.opening_hours ?: "") }
    var closingHours by remember { mutableStateOf(branch.closing_hours ?: "") }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    var branchPicture: MultipartBody.Part? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    val showOpeningTimePicker = rememberTimePickerDialog(context) { selectedTime ->
        openingHours = selectedTime
    }

    val showClosingTimePicker = rememberTimePickerDialog(context) { selectedTime ->
        closingHours = selectedTime
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Edit Branch", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(5.dp))

                ProfilePicturePicker(profilePictureUri) { selectedUri ->
                    profilePictureUri = selectedUri
                    Log.d("ProfilePicture", "Selected URI: ${selectedUri?.toString()}")

                    val file = selectedUri?.let { uriToFile(it, context) }
                    file?.let {
                        val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                        branchPicture = MultipartBody.Part.createFormData("branch_picture", it.name, requestFile)

                        Log.d("ProfilePicture", "File Name: ${it.name}, Size: ${it.length()} bytes")
                    }
                }

                OutlinedTextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Latitude") },
                    modifier = Modifier
                        .alpha(0f) // Fully transparent
                        .height(1.dp) // Smallest possible height
                    )
                OutlinedTextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Longitude") },
                    modifier = Modifier
                        .alpha(0f) // Fully transparent
                        .height(1.dp) // Smallest possible height
                    )


                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, minLines = 3)

                OutlinedTextField(
                    value = openingHours,
                    onValueChange = {},
                    label = { Text("Opening Time") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showOpeningTimePicker() }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Select Opening Time")
                        }
                    }
                )

                OutlinedTextField(
                    value = closingHours,
                    onValueChange = {},
                    label = { Text("Closing Time") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showClosingTimePicker() }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Select Closing Time")
                        }
                    }
                )
                OutlinedTextField(value = contactNumber, onValueChange = { contactNumber = it }, label = { Text("Contact Number") })

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = onDismiss,colors=ButtonDefaults.buttonColors(Color.Gray)) { Text("Cancel") }
                    Button(onClick = {
                        fun String?.toPlainTextRequestBody(): RequestBody? =
                            this?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                        val formattedOpeningHours = openingHours?.substring(0, 5) // Extracts HH:mm
                        val formattedClosingHours = closingHours?.substring(0, 5) // Extracts HH:mm

                        onUpdate(
                            branch.branch_id,
                            latitude.toPlainTextRequestBody(),
                            longitude.toPlainTextRequestBody(),
                            description.toPlainTextRequestBody(),
                            formattedOpeningHours.toPlainTextRequestBody(),
                            formattedClosingHours.toPlainTextRequestBody(),
                            contactNumber.toPlainTextRequestBody(),
                            null, null, null,
                            shopsId.toString().toPlainTextRequestBody(),
                            branchPicture
                        )
                    },
                        colors = ButtonDefaults.buttonColors(Color.Green)
                        ) { Text("Save") }
                }
            }
        }
    }
}




