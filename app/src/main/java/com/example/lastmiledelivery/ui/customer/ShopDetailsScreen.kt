package com.example.lastmiledelivery.ui.customer

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberImagePainter
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.customer.CartMenuItem
import com.example.lastmiledelivery.data.models.customer.CategoryResponse
import com.example.lastmiledelivery.data.models.customer.MenuItem
import com.example.lastmiledelivery.viewmodels.AuthViewModel


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
                                val filteredItems = if (selectedCategoryId != null) {
                                    state.menu.items?.filter { it.item_category_id == selectedCategoryId }
                                } else {
                                    state.menu.items
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
                                                MenuItemCard(item = item, vendor_id = shop.vendorId, shop_id = shop.shopId, branch_id = shop.branchId, viewModel = customerViewModel)
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
fun MenuItemCard(
    item: MenuItem,
    vendor_id: Int,
    shop_id: Int,
    branch_id: Int,
    viewModel: CustomerViewModel, // ✅ Directly use ViewModel
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val user = remember { authViewModel.getUserDetails() }
    val customerData by viewModel.customerData.collectAsState()
    val errorMessage by viewModel.errorMessages.collectAsState()


// Trigger data fetch when the composable enters composition
    LaunchedEffect(key1 = user.id) {
        viewModel.fetchCustomerData(user.id)
    }

    LaunchedEffect(viewModel.customerState) {
        val storedCustomerId = viewModel.getCustomerId()
        Log.d("CustomerMainScreen", "Stored Customer ID: $storedCustomerId")
    }

    // Observe customer data and error messages
    val customer = viewModel.customerState
    val error = viewModel.errorMessage


    LaunchedEffect(customer?.customerId) {
        customer?.let {
            viewModel.fetchCustomerMainScreen(it.customerId)
        }
    }


    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image
            AsyncImage(
                model = item.itemPicture,
                contentDescription = "Item Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Item Name
            Text(
                text = item.item_name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Price & Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Price
                Text(
                    text = "Rs. ${item.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                // Add to Cart Button
                IconButton(
                    onClick = {
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

                        // ✅ Directly call ViewModel function
                        customer?.customerId?.let {
                            viewModel.addItemToCart(
                                customerId = it, // Replace with actual customer ID
                                item = cartItem,
                                quantity = 1 // Default quantity
                            )
                        }
                    },
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

