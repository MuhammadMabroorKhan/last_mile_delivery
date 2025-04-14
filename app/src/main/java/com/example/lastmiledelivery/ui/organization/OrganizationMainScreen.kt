package com.example.lastmiledelivery.ui.organization

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.ui.organization.DrawerContent
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.common.ShopCategoryViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import com.example.lastmiledelivery.viewmodels.organization.OrganizationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationMainScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    organizationViewModel: OrganizationViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val user = remember { authViewModel.getUserDetails() }

    val organization = organizationViewModel.organizationState
    val error = organizationViewModel.errorMessage

    LaunchedEffect(Unit) {
        if (!authViewModel.isLoggedIn()) {
            navController.navigate("login") {
                popUpTo("Organization") { inclusive = true }
            }
        }
    }

    // Fetch org data
    LaunchedEffect(key1 = user.id) {
        organizationViewModel.fetchOrganizationData(user.id)
    }

    // Log stored ID
    LaunchedEffect(organizationViewModel.organizationState) {
        val storedOrgId = organizationViewModel.getOrganizationId()
        Log.d("OrganizationMainScreen", "Stored Organization ID: $storedOrgId")
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController, drawerState, scope)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(id = R.color.pink)
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Organization ${user.name}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 16.dp)
                    )
                    Text(
                        text = "ID ${user.id}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 16.dp)
                    )
                    Text(
                        text = organization?.name ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 16.dp)
                    )
                    Text(
                        text = " ${organization?.organizationId.toString()}" ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 16.dp)
                    )
                    error?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
    }
}




@Composable
private fun DrawerContent(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    authViewModel: AuthViewModel = hiltViewModel(),
    organizationViewModel: OrganizationViewModel = hiltViewModel()
) {

    val user = remember { authViewModel.getUserDetails() }

    val organization = organizationViewModel.organizationState
    val error = organizationViewModel.errorMessage

    LaunchedEffect(Unit) {
        if (!authViewModel.isLoggedIn()) {
            navController.navigate("login") {
                popUpTo("Organization") { inclusive = true }
            }
        }
    }

    // Fetch org data
    LaunchedEffect(key1 = user.id) {
        organizationViewModel.fetchOrganizationData(user.id)
    }

    // Log stored ID
    LaunchedEffect(organizationViewModel.organizationState) {
        val storedOrgId = organizationViewModel.getOrganizationId()
        Log.d("OrganizationMainScreen", "Stored Organization ID: $storedOrgId")
    }


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
                color = colorResource(id = R.color.pink) // Pink color for style
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        )

        // 🔹 **Navigation Items**
        DrawerItem(
            text = "Dashboard",
            icon = Icons.Filled.Home,
            navController = navController,
            route = "Organization",
            drawerState = drawerState,
            scope = scope
        )

        if (organization != null) {
            DrawerItem(
                text = "DeliveryBoy",
                icon = Icons.Filled.DeliveryDining,
                navController = navController,
//                route = "organization_deliveryBoys/${organization.organizationId}",
                route = "deliveryboys/${organization.organizationId}",
                drawerState = drawerState,
                scope = scope
            )
        }

        if (organization != null) {
            DrawerItem(
                text = "Vendors Connection",
                icon = Icons.Filled.DeliveryDining,
                navController = navController,
    //                route = "organization_deliveryBoys/${organization.organizationId}",
                route = "vendorConnectionScreenForOrganization/${organization.organizationId}",
                drawerState = drawerState,
                scope = scope
            )
        }

        // 🔹 **Logout (Same Design as Other Items)**
        DrawerItem(
            text = "Logout",
            icon = Icons.Default.ExitToApp,
            navController = navController,
            route = "login",
            drawerState = drawerState,
            scope = scope,
            isLogout = true,
            authViewModel = authViewModel
        )
    }
}

@Composable
private fun DrawerItem(
    text: String,
    icon: ImageVector,
    navController: NavHostController,
    route: String,
    drawerState: DrawerState,
    scope: CoroutineScope,
    isLogout: Boolean = false,
    authViewModel: AuthViewModel? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isLogout) {
                    authViewModel?.logout()
                    navController.navigate(route) {
                        popUpTo("Organization") { inclusive = true }
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


