package com.example.lastmiledelivery.ui.vendor

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lastmiledelivery.data.models.vendor.Shop
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModelShops
import kotlinx.coroutines.flow.collectLatest

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.ShopCategoryResponse
import com.example.lastmiledelivery.data.models.vendor.ShopRequest
import com.example.lastmiledelivery.viewmodels.common.ShopCategoryViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorShopsScreen(
    vendorId: Int, // Vendor ID passed automatically
    viewModel: VendorViewModelShops = hiltViewModel(),
    categoryViewModel: ShopCategoryViewModel = hiltViewModel(),
    navController: NavController
) {
    val shops by viewModel.shops.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) } // State to control dialog visibility

    LaunchedEffect(vendorId) {
        viewModel.fetchShops(vendorId)
        categoryViewModel.fetchCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendor Shops") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
        ) {
            // "Create Shop" Button

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.pink)) // Pink color
            ) {
                Icon(
                    imageVector = Icons.Filled.AddBusiness, // Shop-related icon
                    contentDescription = "Create Shop",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                Text(
                    text = "Create Shop",
                    color = Color.White, // White text for contrast
                    fontWeight = FontWeight.Bold
                )
            }

            // Show the dialog when showDialog is true
            if (showDialog) {
                CreateShopDialog(
                    vendorId = vendorId,
                    categories = categories,
                    onDismiss = { showDialog = false },
                    onConfirm = { name, description, categoryId ->
                        viewModel.createShop(
                            ShopRequest(
                                name = name,
                                description = description,
                                shopcategory_ID = categoryId,
                                vendors_ID = vendorId
                            )
                        )
                        Log.d("Create SHOP Sending","${name} , ${description}  , ${categoryId}  , ${vendorId}")
                        showDialog = false
                    }
                )
            }

            when {
                errorMessage != null -> {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
                shops.isNullOrEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(shops!!.chunked(2)) { _, rowItems ->  // Two items per row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowItems.forEach { shop ->
                                    ShopItem(shop, navController, Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateShopDialog(
    vendorId: Int,
    categories: List<ShopCategoryResponse>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ShopCategoryResponse?>(null) }
    var expanded by remember { mutableStateOf(false) } // Track dropdown state
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Create Shop") },
//        text = {
//            Column {
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = { name = it },
//                    label = { Text("Shop Name") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                OutlinedTextField(
//                    value = description,
//                    onValueChange = { description = it },
//                    label = { Text("Description") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//
//                ExposedDropdownMenuBox(
//                    expanded = expanded,
//                    onExpandedChange = { expanded = !expanded } // Toggle dropdown
//                ) {
//                    OutlinedTextField(
//                        modifier = Modifier
//                            .menuAnchor()
//                            .focusRequester(focusRequester)
//                            .fillMaxWidth()
//                            .clickable { expanded = !expanded }, // Ensure the entire field is clickable
//                        readOnly = true,
//                        value = selectedCategory?.name ?: "Select Category",
//                        onValueChange = {},
//                        label = { Text("Select a category") },
//                        trailingIcon = {
//                            Icon(
//                                Icons.Filled.ArrowDropDown,
//                                contentDescription = "Dropdown Icon",
//                                modifier = Modifier.clickable { expanded = !expanded } // Make icon clickable
//                            )
//                        }
//                    )
//
//                    DropdownMenu(
//                        expanded = expanded,
//                        onDismissRequest = { expanded = false }
//                    ) {
//                        categories.forEach { category ->
//                            DropdownMenuItem(
//                                text = { Text(category.name) },
//                                onClick = {
//                                    selectedCategory = category
//                                    expanded = false // Close dropdown after selection
//                                }
//                            )
//                        }
//                    }
//                }
//
//            }
//        },
//        confirmButton = {
//            Button(
//                onClick = {
//                    if (name.isBlank() || description.isBlank() || selectedCategory == null) {
//                        Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
//                    } else {
//                        onConfirm(name, description, selectedCategory!!.id)
//                    }
//                }
//            ) {
//                Text("Confirm")
//            }
//        },
//        dismissButton = {
//            Button(onClick = onDismiss) {
//                Text("Cancel")
//            }
//        }
//    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create Shop",
                color = colorResource(id=R.color.pink), // Pink color
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Shop Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .focusRequester(focusRequester)
                            .fillMaxWidth()
                            .clickable { expanded = !expanded },
                        readOnly = true,
                        value = selectedCategory?.name ?: "Select Category",
                        onValueChange = {},
                        label = { Text("Select a category") },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Dropdown Icon",
                                modifier = Modifier.clickable { expanded = !expanded }
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || description.isBlank() || selectedCategory == null) {
                        Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                    } else {
                        onConfirm(name, description, selectedCategory!!.id)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green) // Grey color for Confirm
            ) {
                Text("Confirm", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray) // Red color for Cancel
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )

}

@Composable
fun ShopItem(
    shop: Shop,
    navController: NavController, // Navigation Controller
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable {
                // Navigate to the BranchesScreen with shop details
                navController.navigate("branches/${shop.shopcategory_name}/${shop.shopcategory_ID}/${shop.id}/${shop.name}/${shop.description ?: "No description"}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.storefront),
                contentDescription = "Shop Icon",
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(colorResource(id = R.color.pink))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = shop.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = shop.description ?: "No description",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Category: ${shop.shopcategory_name}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}





