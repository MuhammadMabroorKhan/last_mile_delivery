package com.example.lastmiledelivery.ui.vendor

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.ui.vendor.API_Vendor.API_VendorMainScreen
import com.example.lastmiledelivery.ui.vendor.IN_APP.IN_APP_VendorMainScreen
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorScaffold(
    navController: NavHostController,
    title: String, // ✅ Title parameter
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val authViewModel: AuthViewModel = hiltViewModel()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController, drawerState, scope, authViewModel)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title, color = Color.White) }, // ✅ Set dynamic title
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Menu",
                                tint = Color.White // ✅ Set icon color to white
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(id = R.color.pink) // ✅ Use a pink shade
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                content() // 👈 This will show the correct screen content
            }
        }
    }
}

@Composable
fun VendorMainScreenWrapper(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val user = remember { authViewModel.getUserDetails() }  // Get user data

    user?.let {
        val viewModel: VendorViewModel = hiltViewModel()
        val vendorState by viewModel.vendorData.observeAsState()

        LaunchedEffect(it.id) {
            if (viewModel.vendorData.value == null) { // ✅ Only fetch if null
                Log.d("API_VendorMainScreen", "Fetching vendor data for ID: ${it.id}")
                viewModel.getVendorData(it.id)
            }
        }


        vendorState?.let { result ->
            result.fold(
                onSuccess = { vendor ->
                    Log.d("PassingID"," before state ${vendor.vendorId}")
                    when (vendor.vendorType) {
                        "In-App Vendor" -> IN_APP_VendorMainScreen(navController, it.id)
                        "API Vendor" -> API_VendorMainScreen(navController, it.id)
                        else -> Text("Unknown Vendor Type", color = Color.Red)
                    }
                },
                onFailure = { exception ->
                    Log.e("VendorMainScreenWrapper", "Error fetching vendor data", exception)
                    Text("Error: ER${exception.message}", color = Color.Red)
                }
            )
        } ?: run {
            CircularProgressIndicator()
        }
    } ?: run {
        CircularProgressIndicator()
    }
}



@Composable
fun DrawerContent(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    authViewModel: AuthViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel()
) {
    val vendorId by vendorViewModel.vendorId.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 🔹 **Header (Centered)**
        Text(
            text = "Menu",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.pink) // Pink for styling
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        )

        // 🔹 **Navigation Items**
        DrawerItem(
            text = "Dashboard",
            icon = Icons.Filled.Dashboard,
            navController = navController,
            route = "vendor",
            drawerState = drawerState,
            scope = scope
        )

        DrawerItem(
            text = "Shops",
            icon = Icons.Filled.ShoppingCart,
            navController = navController,
            route = "shopscreen/${vendorId}", // Dynamic vendor ID
            drawerState = drawerState,
            scope = scope
        )

        DrawerItem(
            text = "Orders",
            icon = Icons.Filled.ReceiptLong,
            navController = navController,
            route = "vendororderscreen/${vendorId}", // Dynamic vendor ID
            drawerState = drawerState,
            scope = scope
        )

        // 🔹 **Logout (Same Style as Other Items)**
        DrawerItem(
            text = "Logout",
            icon = Icons.Default.ExitToApp,
            navController = navController,
            route = "login",
            drawerState = drawerState,
            scope = scope,
            isLogout = true, // Handles logout action
            authViewModel = authViewModel
        )
    }
}

@Composable
fun DrawerItem(
    text: String,
    icon: ImageVector,
    navController: NavHostController,
    route: String,
    drawerState: DrawerState,
    scope: CoroutineScope,
    isLogout: Boolean = false, // Logout Handling
    authViewModel: AuthViewModel? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isLogout) {
                    authViewModel?.logout() // Logout Action
                    navController.navigate(route) {
                        popUpTo("vendor") { inclusive = true }
                    }
                } else {
                    navController.navigate(route)
                }
                scope.launch { drawerState.close() }
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}
