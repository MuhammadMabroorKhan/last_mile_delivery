package com.example.lastmiledelivery.ui.customer

import android.util.Log
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.customer.CartMenuItem
import com.example.lastmiledelivery.data.models.customer.MenuItem
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDetailsScreen(
    navController: NavController,
    customerViewModel: CustomerViewModel = hiltViewModel()
) {
    val selectedShop by customerViewModel.selectedShop.collectAsState()
    val menuState by customerViewModel.menuState.collectAsState()

    val shop = selectedShop

    // 🔹 Store selected category ID
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    val categories by customerViewModel.categories.collectAsState()
    val errorMessage by customerViewModel.errorsMessages.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(selectedShop ?: shop) {
        val currentShop = selectedShop ?: shop
        currentShop?.let { shopData ->
            Log.d("VendorMenu", "Fetching for shop: ${shopData.shopId}")

            customerViewModel.fetchCategories(shopData.vendorId, shopData.shopId, shopData.branchId)
            customerViewModel.fetchVendorMenu(shopData.vendorId, shopData.shopId, shopData.branchId)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(shop?.shopName ?: "Shop Details", color = Color.White) },
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
        if (shop == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No Shop Selected", textAlign = TextAlign.Center)
            }
        } else {
            // ✅ Wrap everything inside LazyColumn
            LazyColumn(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxSize()
                    .padding(paddingValues),  // 🔹 Apply Scaffold's padding here
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 🔹 SHOP DETAILS CARD
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = shop.branchPicture,
                                contentDescription = shop.shopName,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = shop.shopName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = shop.shopDescription,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Rating",
                                        tint = colorResource(id = R.color.golden_star),
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

                item {
                    // 🔍 SEARCH TEXT FIELD
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search items...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }

                item {
                    // 🔹 CATEGORIES SECTION
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val categories = remember(menuState) {
                            (menuState as? CustomerViewModel.MenuState.Success)
                                ?.menu?.items
                                ?.mapNotNull { item ->
                                    item.item_category_id?.let { id ->
                                        item.item_category_name?.let { name ->
                                            id to name  // Only include pairs where both values are non-null
                                        }
                                    }
                                }
                                ?.distinctBy { it.first } ?: emptyList()
                        }

                        if (categories.isNotEmpty()) {

                            Text(
                                text = "Categories",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                            )

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                items(categories) { (categoryId, categoryName) ->
                                    CategoryItem(
                                        categoryName = categoryName,
                                        isSelected = selectedCategoryId == categoryId,
                                        onClick = {
                                            selectedCategoryId =
                                                if (selectedCategoryId == categoryId) null else categoryId
                                        }
                                    )
                                }
                            }
                        }
                    }

                }

                // 🔹 MENU ITEMS SECTION
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Menu Items",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                        )

                        when (val state = menuState) {
                            is CustomerViewModel.MenuState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            is CustomerViewModel.MenuState.Success -> {
                                val filteredItems = state.menu.items?.filter { item ->
                                    val matchesCategory =
                                        selectedCategoryId == null || item.item_category_id == selectedCategoryId
                                    val matchesSearch =
                                        searchQuery.isBlank() || item.item_name?.contains(
                                            searchQuery,
                                            ignoreCase = true
                                        ) == true
                                    matchesCategory && matchesSearch
                                }

                                if (filteredItems != null) {
                                    if (filteredItems.isEmpty()) {
                                        Text(
                                            text = "No items found",
                                            color = Color.Gray,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    } else {
                                        LazyVerticalGrid(
                                            columns = GridCells.Fixed(2),
                                            contentPadding = PaddingValues(8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.heightIn(
                                                min = 100.dp,
                                                max = 600.dp
                                            ) // Prevents overlap
                                        ) {
//                                            items(filteredItems) { item ->
//                                                MenuItemCard(item)
//                                            }
                                            items(filteredItems) { item ->
                                                MenuItemCard(
                                                    item = item,
                                                    vendor_id = shop.vendorId,
                                                    shop_id = shop.shopId,
                                                    branch_id = shop.branchId,
                                                    viewModel = customerViewModel
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            is CustomerViewModel.MenuState.Error -> {
                                Text(
                                    text = state.message,
                                    color = Color.Red,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryItem(categoryName: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = if (isSelected) BorderStroke(2.dp, Color.Black) else null,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .background(if (isSelected) Color.White else colorResource(id = R.color.pink))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (categoryName.isNotEmpty()) {
                Text(
                    text = categoryName,
                    color = if (isSelected) Color.Black else Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MenuItemCard(
    item: MenuItem,
    vendor_id: Int,
    shop_id: Int,
    branch_id: Int,
    viewModel: CustomerViewModel,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val user = remember { authViewModel.getUserDetails() }
    val customerData by viewModel.customerData.collectAsState()
    val errorMessage by viewModel.errorMessages.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) }

    LaunchedEffect(key1 = user.id) {
        viewModel.fetchCustomerData(user.id)
    }

    val customer = viewModel.customerState

    // ✅ Show dialog when triggered
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = item.item_name, style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = { showDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start
                ) {
                    AsyncImage(
                        model = item.itemPicture,
                        contentDescription = "Item Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    item.item_description?.let {
                        Text("Description: $it")
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text("Variation: ${item.variation_name}")
                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Category: ${item.item_category_name}")
                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Preparation Time: ${item.preparation_time} mins")
                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Time Sensitive: ${item.timesensitive}")
                    Spacer(modifier = Modifier.height(4.dp))

                    item.additional_info?.let {
                        Text("Additional Info: $it")
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text("Price: Rs. ${item.price}")
                    Spacer(modifier = Modifier.height(12.dp))

                    // ✅ Attributes
                    item.attributes?.takeIf { it.isNotEmpty() }?.let { attributes ->
                        Text("Attributes:")
                        Spacer(modifier = Modifier.height(4.dp))
                        attributes.forEach { attr ->
                            Text("- ${attr.key}: ${attr.value}")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // ✅ Quantity row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease Quantity")
                        }
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = { quantity++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase Quantity")
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val cartItem = CartMenuItem(
                        id = item.item_id,
                        item_name = item.item_name,
                        item_description = item.item_description,
                        price = item.price.toDoubleOrNull() ?: 0.0,
                        itemPicture = item.itemPicture ?: "",
                        vendor_id = vendor_id,
                        shop_id = shop_id,
                        branch_id = branch_id,
                        itemdetails_id = item.itemdetail_id
                    )

                    customer?.customerId?.let {
                        viewModel.addItemToCart(
                            customerId = it,
                            item = cartItem,
                            quantity = quantity
                        )
                    }

                    showDialog = false
                    quantity = 1
                }) {
                    Text("Add to Cart")
                }
            }
        )
    }

//Main Card UI
    if (
        item.item_name != null &&
        item.price != null &&
        item.itemPicture != null &&
        item.variation_name != null
    ) {
        Card(
            modifier = Modifier
                .width(160.dp)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = item.itemPicture,
                    contentDescription = "Item Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.item_name ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.variation_name ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                item.item_description?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.DarkGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rs. ${item.price}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    IconButton(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .background(Color.Red, CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

}



