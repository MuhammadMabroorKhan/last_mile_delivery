package com.example.lastmiledelivery.ui.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.admin.ApiVendorRegisterWebsite
import com.example.lastmiledelivery.data.models.customer.CustomerMainScreenResponse
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.admin.AdminViewModel
import com.example.lastmiledelivery.viewmodels.common.ShopCategoryViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel

//
//// 5. --- Composable Screen ---
@Composable
fun ApiVendorsConnectionScreen(
    navController: NavHostController, viewModel: AdminViewModel = hiltViewModel()
) {
    val vendors = viewModel.filteredVendors()
    val isLoading = viewModel.isLoading
    var selectedVendor by remember { mutableStateOf<ApiVendorRegisterWebsite?>(null) }

    val context = LocalContext.current
    AdminScaffold(navController, title = "API Vendor Connection") { // ✅ Pass title

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            OutlinedTextField(value = viewModel.searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                label = { Text("Search by Email or CNIC") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.showAddVariableDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Variable")
                }

                Button(
                    onClick = { viewModel.isDialogVisible = true }, modifier = Modifier.weight(1f)
                ) {
                    Text("Add Method")
                }

                viewModel.addVariableMessage?.let { message ->
                    LaunchedEffect(message) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        viewModel.addVariableMessage = null
                    }
                }

                viewModel.saveResult?.let { message ->
                    LaunchedEffect(message) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        viewModel.saveResult = null
                    }
                }


            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(vendors) { vendor ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    navController.navigate("apiVendorWebsiteDetail/${vendor.vendor_ID}/${vendor.lmd_users_ID}")
                                }, elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Profile Image
                                vendor.profile_picture?.let {
                                    AsyncImage(
                                        model = it,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } ?: Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Text Info: Take available space
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = vendor.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Status: ${vendor.approval_status}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "CNIC: ${vendor.cnic}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                // Button: Fixed size
                                Button(
                                    onClick = { selectedVendor = vendor },
                                    contentPadding = PaddingValues(
                                        horizontal = 12.dp, vertical = 8.dp
                                    )
                                ) {
                                    Text("Details", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }

                        }
                    }
                }

                // Dialog for details
                selectedVendor?.let { vendor ->
                    AlertDialog(onDismissRequest = { selectedVendor = null },
                        title = { Text("Vendor Details") },
                        text = {
                            Column {
                                Text("Name: ${vendor.name}")
                                Text("Email: ${vendor.email}")
                                Text("Phone: ${vendor.phone_no}")
                                Text("CNIC: ${vendor.cnic}")
                                Text("Vendor Type: ${vendor.vendor_type}")
                                Text("Approval Status: ${vendor.approval_status}")
                                Text("Account Created: ${vendor.account_creation_date}")
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { selectedVendor = null }) {
                                Text("Close")
                            }
                        })
                }

            }




            if (viewModel.showAddVariableDialog) {
                AddVariableDialog(tagValue = viewModel.newVariableTag,
                    onValueChange = { viewModel.newVariableTag = it },
                    onDismiss = { viewModel.showAddVariableDialog = false },
                    onSave = {
                        viewModel.addVariable {
                            Toast.makeText(context, "Variable added!", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            AddNewApiMethodDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.isDialogVisible = false })

        }
    }
}

@Composable
fun AddNewApiMethodDialog(
    viewModel: AdminViewModel, onDismiss: () -> Unit
) {
    if (viewModel.isDialogVisible) {
        AlertDialog(onDismissRequest = onDismiss, title = { Text("Add New API Method") }, text = {
            Column {
                OutlinedTextField(
                    value = viewModel.apiVendorIdInput,
                    onValueChange = { viewModel.apiVendorIdInput = it },
                    label = { Text("API Vendor ID (optional)") },
                    placeholder = { Text("Defaults to 0 if empty") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = viewModel.methodName,
                    onValueChange = { viewModel.methodName = it },
                    label = { Text("Method Name") })

                Spacer(modifier = Modifier.height(8.dp))
                var expanded by remember { mutableStateOf(false) }
                val httpMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH")
                Box {
                    OutlinedTextField(value = viewModel.httpMethod,
                        onValueChange = {},
                        label = { Text("HTTP Method") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        enabled = false,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) })
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        httpMethods.forEach {
                            DropdownMenuItem(text = { Text(it) }, onClick = {
                                viewModel.httpMethod = it
                                expanded = false
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = viewModel.endpoint,
                    onValueChange = { viewModel.endpoint = it },
                    label = { Text("Endpoint (optional)") })

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = viewModel.description,
                    onValueChange = { viewModel.description = it },
                    label = { Text("Description (optional)") })
            }
        }, confirmButton = {
            Button(onClick = { viewModel.saveNewApiMethod() }) {
                Text("Save")
            }
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        })
    }
}


@Composable
fun AddVariableDialog(
    tagValue: String, onValueChange: (String) -> Unit, onDismiss: () -> Unit, onSave: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Add New Variable") }, text = {
        OutlinedTextField(
            value = tagValue,
            onValueChange = onValueChange,
            label = { Text("Variable Tag") },
            modifier = Modifier.fillMaxWidth()
        )
    }, confirmButton = {
        Button(onClick = onSave) {
            Text("Save")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDetailScreen(
    navController: NavHostController,
    vendorId: Int,
    lmdUserId: Int,
    authViewModel: AuthViewModel = hiltViewModel(),
    categoryViewModel: ShopCategoryViewModel = hiltViewModel(), // ✅ Multiple ViewModels can be used
    customerViewModel: CustomerViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val user = remember { authViewModel.getUserDetails() }
    val categories by categoryViewModel.categories.collectAsState()

    val customerData by customerViewModel.customerData.collectAsState()
    val errorMessage by customerViewModel.errorMessages.collectAsState()

    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }


    var searchQuery by remember { mutableStateOf("") }


    LaunchedEffect(lmdUserId) {
        lmdUserId?.let {
            customerViewModel.fetchCustomerMainScreen(it)
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Vendor SHOPS", color = Color.White) }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id = R.color.pink)
        )
        )
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Content Section (Scrollable)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp) // Adjust padding to prevent overlap with header
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(value = searchQuery, onValueChange = { searchQuery = it }, placeholder = {
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
                    }, textStyle = TextStyle(color = Color.Black), // Input text color black
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black, // Cursor color black
                        focusedIndicatorColor = Color.Transparent, // Remove bottom border
                        unfocusedIndicatorColor = Color.Transparent, // Remove bottom border
                        containerColor = Color.White // White background for the field
                    ), shape = RoundedCornerShape(20.dp), // Fully rounded field
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
                        com.example.lastmiledelivery.ui.admin.CategoryButton(name = category.name,
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
                        // Combined filtering for both category and search query
                        val filteredShops = customerData?.filter { shop ->
                            val matchesCategory =
                                selectedCategoryId?.let { it == shop.shopCategoryId } ?: true
                            val matchesSearchQuery =
                                shop.shopName.contains(searchQuery, ignoreCase = true)

                            matchesCategory && matchesSearchQuery // Both must be true
                        }

// Display filtered shops
                        filteredShops?.forEach { shop ->
                            com.example.lastmiledelivery.ui.admin.ShopCard(
                                shop, customerViewModel, navController, vendorId
                            )
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


@Composable
private fun CategoryButton(name: String, id: Int, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) colorResource(id = R.color.pink) else Color.White
    val textColor = if (isSelected) Color.White else Color.Black

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        border = if (isSelected) BorderStroke(2.dp, Color.White) else BorderStroke(
            2.dp, Color.Black
        ), // Add a thin border
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(text = name, color = textColor)
    }
}

@Composable
private fun ShopCard(
    shop: CustomerMainScreenResponse,
    customerViewModel: CustomerViewModel,
    navController: NavController,
    vendorId: Int
) {
    val context = LocalContext.current
    if (shop.vendorId == vendorId) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
//                    Toast
//                        .makeText(
//                            context,
//                            "${shop.vendorId} and ${shop.branchId}and  ${shop.shopId}",
//                            Toast.LENGTH_SHORT
//                        )
//                        .show()
//                customerViewModel.setSelectedShop(shop) // Store Selected Shop in ViewModel
//                navController.navigate("shop_details") // Navigate to Details Screen
                    navController.navigate("apiVendorWebsiteMappingInfo/${vendorId}/${shop.shopId}/${shop.branchId}") // Navigate to Details Screen
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = colorResource(id = R.color.golden_star), // Gold color for the star
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = "${shop.avgRating} (${shop.reviewsCount} Ratings)",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}