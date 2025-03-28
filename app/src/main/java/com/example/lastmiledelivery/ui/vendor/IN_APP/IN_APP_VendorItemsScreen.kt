package com.example.lastmiledelivery.ui.vendor.IN_APP

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.vendor.ItemAttribute
import com.example.lastmiledelivery.data.models.vendor.ItemCategory
import com.example.lastmiledelivery.data.models.vendor.ItemVariation
import com.example.lastmiledelivery.data.models.vendor.VendorItemResponse
import com.example.lastmiledelivery.ui.common.ProfilePicturePicker
import com.example.lastmiledelivery.ui.common.uriToFile
import com.example.lastmiledelivery.viewmodels.vendor.IN_APPVendor.IN_APPVENDORItemViewModel
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


//@Composable
//fun IN_APP_VendorItemsScreen(
//    shopcategory_name: String,
//    shopcategory_ID: String,
//    vendorId: String,
//    branchId: String,
//    shopId: String,
//    approvalStatus: String,
//    viewModel: IN_APPVENDORItemViewModel = hiltViewModel()
//) {
//    var showDialog by remember { mutableStateOf(false) }
//    val items by viewModel.items
//    val errorMessage by viewModel.errorMessage
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchItems(vendorId.toInt(), shopId.toInt(), branchId.toInt())
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(text = "In-App Vendor Items Screen", style = MaterialTheme.typography.headlineMedium)
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = { showDialog = true }) {
//            Text(text = "Create Item")
//        }
//
////        Text(text = "Vendor ID: $vendorId", fontSize = 18.sp)
////        Text(text = "Branch ID: $branchId", fontSize = 18.sp)
////        Text(text = "Shop ID: $shopId", fontSize = 18.sp)
////        Text(text = "Approval Status: $approvalStatus", fontSize = 18.sp)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (items == null) {
//            CircularProgressIndicator()
//        } else if (items!!.isEmpty()) {
//            Text(text = "No items found", fontSize = 20.sp, color = Color.Red)
//        } else {
//            LazyColumn {
//                items(items!!) { item ->
//                    VendorItemCard(item)
//                }
//            }
//        }
//
//        if (showDialog) {
//            CreateItemDialog(
//                shopId = shopId.toInt(),
//                vendorId = vendorId.toInt(),
//                branchId = branchId.toInt(),
//                shopCategoryId = shopcategory_ID.toInt(),
//                onDismiss = { showDialog = false },
//                viewModel = viewModel
//            )
//        }
//    }
//}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IN_APP_VendorItemsScreen(
    shopcategory_name: String,
    shopcategory_ID: String,
    vendorId: String,
    branchId: String,
    shopId: String,
    approvalStatus: String,
    onBackPressed: () -> Unit,  // Back button function
    viewModel: IN_APPVENDORItemViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    val items by viewModel.items
    val errorMessage by viewModel.errorMessage

    LaunchedEffect(Unit) {
        viewModel.fetchItems(vendorId.toInt(), shopId.toInt(), branchId.toInt())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "In-App Vendor Items Screen",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

//            Button(
//                onClick = { showDialog = true },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(text = "Create Item")
//            }
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.pink)) // Pink color
            ) {
                Icon(
                    imageVector = Icons.Filled.AddBox, // Shop-related icon
                    contentDescription = "Create Item",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                Text(
                    text = "Create Item",
                    color = Color.White, // White text for contrast
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (items == null) {
                CircularProgressIndicator()
            } else if (items!!.isEmpty()) {
                Text(text = "No items found", fontSize = 20.sp, color = Color.Red)
            } else {
                LazyColumn {
                    items(items!!) { item ->
                        VendorItemCard(item)
                    }
                }
            }

            if (showDialog) {
                CreateItemDialog(
                    shopId = shopId.toInt(),
                    vendorId = vendorId.toInt(),
                    branchId = branchId.toInt(),
                    shopCategoryId = shopcategory_ID.toInt(),
                    onDismiss = { showDialog = false },
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun VendorItemCard(item: VendorItemResponse) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // Ensuring uniform image size
            AsyncImage(
                model = item.picture,
                contentDescription = "Item Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Item name and price in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = item.item_name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Price: ${item.price ?: "N/A"}", fontSize = 16.sp, color = Color.Green)
            }
        }
    }

    // Dialog to show full details
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = item.item_name, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    AsyncImage(
                        model = item.picture,
                        contentDescription = "Item Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = item.item_description, fontSize = 16.sp)
                    Text(text = "Time Sensitive: ${item.timesensitive}", fontSize = 14.sp)
                    Text(text = "Preparation Time: ${item.preparation_time} min", fontSize = 14.sp)
                    Text(text = "Price: ${item.price ?: "N/A"}", fontSize = 14.sp, color = Color.Green)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Attributes:", fontWeight = FontWeight.Bold)
                    item.attributes?.forEach { attr ->
                        Text(text = "${attr.key}: ${attr.value}", fontSize = 14.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDialog = false },colors = ButtonDefaults.buttonColors(Color.Gray)) {
                    Text("Close")
                }
            }
        )
    }
}

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateItemDialog(
    shopId: Int,
    vendorId: Int,
    branchId: Int,
    shopCategoryId: Int,
    onDismiss: () -> Unit,
    viewModel: IN_APPVENDORItemViewModel
) {
    val categories by viewModel.categories
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val context = LocalContext.current
    val variations by viewModel.variations
    val isVariationLoading by viewModel.isVariationLoading
    val variationError by viewModel.variationError

    val attributes by viewModel.attributes
    val isLoadingAttributes by viewModel.isLoadingAttributes
    val attributesError by viewModel.attributesError

    var selectedCategory by remember { mutableStateOf<ItemCategory?>(null) }
    var selectedVariation by remember { mutableStateOf<ItemVariation?>(null) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedVariation by remember { mutableStateOf(false) }

    var itemName by remember { mutableStateOf("") }
    var additionalInfo by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var manualVariation by remember { mutableStateOf("") }
    val selectedAttributes = remember { mutableStateMapOf<String, String>() }

    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    var itemPicture: MultipartBody.Part? by remember { mutableStateOf(null) }


    val finalVariation = selectedVariation?.name ?: manualVariation

    if (finalVariation.isBlank()) {
        Log.d("CreateItemDialog", "Variation is required but not provided!")
    } else {
        Log.d("CreateItemDialog", "Final Variation: $finalVariation")
    }

    //var timeSensitive by remember { mutableStateOf("No") }
    var preparationTime by remember { mutableStateOf("") } // Store as String

    var expandedTimeSensitive by remember { mutableStateOf(false) }
    var timeSensitive by remember { mutableStateOf("Select") } // Default value
    LaunchedEffect(shopCategoryId) {
        viewModel.fetchItemCategories(shopCategoryId)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(text = "Create Item", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))


                ProfilePicturePicker(profilePictureUri) { selectedUri ->
                    profilePictureUri = selectedUri
                    Log.d("ProfilePicture", "Selected URI: ${selectedUri?.toString()}")

                    // Convert URI to MultipartBody.Part
                    val file = selectedUri?.let { uriToFile(it, context) }
                    file?.let {
                        val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                        itemPicture = MultipartBody.Part.createFormData("itemPicture", it.name, requestFile)

                        // Log Multipart Data
                        Log.d("ProfilePicture", "File Name: ${it.name}, Size: ${it.length()} bytes")
                    }
                }



                Spacer(modifier = Modifier.height(16.dp))

                // ✅ Name Input
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // ✅ Description Input
                OutlinedTextField(
                    value = itemDescription,
                    onValueChange = { itemDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // ✅ Additional Info
                OutlinedTextField(
                    value = additionalInfo,
                    onValueChange = { additionalInfo = it },
                    label = { Text("Additional Info") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                // ✅ Price Input
                OutlinedTextField(
                    value = itemPrice,
                    onValueChange = { itemPrice = it },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // ✅ Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "Select Category",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Dropdown Icon")
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    expandedCategory = false
                                    selectedVariation = null
                                    manualVariation = ""

                                    selectedAttributes.clear()

                                    viewModel.fetchItemVariations(category.id)
                                    viewModel.fetchPredefinedAttributes(category.id)
                                }
                            )
                        }
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                errorMessage?.let { Text(text = it, color = Color.Red) }
                Spacer(modifier = Modifier.height(16.dp))

                if (variations.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = expandedVariation,
                        onExpandedChange = { expandedVariation = !expandedVariation }
                    ) {
                        OutlinedTextField(
                            value = selectedVariation?.name ?: "Select Variation",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Dropdown Icon")
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = expandedVariation,
                            onDismissRequest = { expandedVariation = false }
                        ) {
                            variations.forEach { variation ->
                                DropdownMenuItem(
                                    text = { Text(variation.name) },
                                    onClick = {
                                        selectedVariation = variation
                                        manualVariation = ""  // Clear manual variation if dropdown is selected
                                        expandedVariation = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = manualVariation,
                        onValueChange = { manualVariation = it },
                        label = { Text("Enter Variation") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (isVariationLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                variationError?.let { Text(text = it, color = Color.Red) }
                Spacer(modifier = Modifier.height(16.dp))

                // Time Sensitive Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedTimeSensitive,
                    onExpandedChange = { expandedTimeSensitive = !expandedTimeSensitive }
                ) {
                    OutlinedTextField(
                        value = timeSensitive,
                        onValueChange = {},
                        label = { Text("Time Sensitive") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(), // Correct anchor for dropdown
                        trailingIcon = {
                            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Dropdown Icon")
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = expandedTimeSensitive,
                        onDismissRequest = { expandedTimeSensitive = false }
                    ) {
                        listOf("Yes", "No").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    timeSensitive = option // Update selected value
                                    expandedTimeSensitive = false // Close dropdown
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
// Preparation Time Input
                OutlinedTextField(
                    value = preparationTime,
                    onValueChange = { newValue ->
                        // Only allow numbers or empty input
                        if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                            preparationTime = newValue
                        }
                    },
                    label = { Text("Preparation Time (minutes)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )


// Convert UI inputs to RequestBody
                val timeSensitiveRequestBody = timeSensitive.toRequestBody("text/plain".toMediaTypeOrNull())
                val preparationTimeRequestBody = if (preparationTime.isNotEmpty()) {
                    preparationTime.toInt().toString().toRequestBody("text/plain".toMediaTypeOrNull())
                } else {
                    null // Handle empty case
                }

                Spacer(modifier = Modifier.height(16.dp))


                if (attributes.isNotEmpty()) {
                    attributes.forEach { (key, values) ->
                        Text(text = key, fontWeight = FontWeight.Bold)

                        val splitValues = values.flatMap { it.split(",").map { v -> v.trim() } }

                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            splitValues.forEach { value ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .selectable(
                                            selected = selectedAttributes[key] == value,
                                            onClick = { selectedAttributes[key] = value }
                                        )
                                ) {
                                    RadioButton(
                                        selected = selectedAttributes[key] == value,
                                        onClick = { selectedAttributes[key] = value }
                                    )
                                    Text(text = value, modifier = Modifier.padding(start = 4.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (isLoadingAttributes) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    attributesError?.let { Text(text = it, color = Color.Red) }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                // ✅ Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(Color.Gray)) { Text("Cancel") }
                    Button(
                        onClick = {
                            Log.d("CreateItemDialog", "Vendor ID: ${vendorId}")
                            Log.d("CreateItemDialog", "Shop ID: ${shopId}")
                            Log.d("CreateItemDialog", "Branch ID: ${branchId}")
                            Log.d("CreateItemDialog", "Entered Name: $itemName")
                            Log.d("CreateItemDialog", "Entered Description: $itemDescription")
                            Log.d("CreateItemDialog", "Selected Category: ${selectedCategory?.id} - ${selectedCategory?.name}")
                            Log.d("CreateItemDialog", "Branch ID: ${branchId}")
                            Log.d("CreateItemDialog", "Final Variation: $finalVariation")
                            Log.d("CreateItemDialog", "Entered Price: $itemPrice")
                            Log.d("CreateItemDialog", "additionalInfo : $additionalInfo")
                            Log.d("CreateItemDialog", "itemPicture : $itemPicture")

                            if (profilePictureUri != null) {
                                Log.d("CreateItemDialog", "Profile Picture URI: $profilePictureUri")
                            } else {
                                Log.d("CreateItemDialog", "No profile picture selected")
                            }

                            selectedAttributes.forEach { (key, value) ->
                                Log.d("CreateItemDialog", "Attribute: $key -> Selected Value: $value")
                            }

                            if (itemPicture.toString().isNullOrEmpty()) {

                                Toast.makeText(context, "Select Branch PIcture required!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }


                            // Convert values to RequestBody
                            val nameBody = itemName.toRequestBody("text/plain".toMediaTypeOrNull())
                            val descriptionBody = itemDescription.toRequestBody("text/plain".toMediaTypeOrNull())
                            val categoryIdBody = selectedCategory?.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                            val branchIdBody = branchId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                            val variationBody = finalVariation.toRequestBody("text/plain".toMediaTypeOrNull())
                            val priceBody = itemPrice.toRequestBody("text/plain".toMediaTypeOrNull())
                            val additionalInfoBody = additionalInfo.toRequestBody("text/plain".toMediaTypeOrNull())


                            val attributesList = selectedAttributes.map {
                                ItemAttribute(it.key, it.value)
                            }

                            viewModel.createItem(
                                vendorId = vendorId,
                                shopId = shopId,
                                branchId = branchId,
                                name = nameBody,
                                timesensitive=timeSensitiveRequestBody,
                                preparationTime=preparationTimeRequestBody,
                                description = descriptionBody,
                                categoryId = categoryIdBody,
                                branchesId = branchIdBody,
                                variationName = variationBody,
                                price = priceBody,
                                additionalInfo = additionalInfoBody,
                                picture = itemPicture,
                                attributesList = attributesList
                            )

                        },
                        enabled = selectedCategory != null && (selectedVariation != null || manualVariation.isNotBlank())
                    ,colors=ButtonDefaults.buttonColors(Color.Green)
                    ) { Text("Confirm", color = Color.White) }
                }
            }
        }
    }
}
