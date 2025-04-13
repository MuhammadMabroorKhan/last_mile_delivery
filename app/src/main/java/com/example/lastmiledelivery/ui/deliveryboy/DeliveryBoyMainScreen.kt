package com.example.lastmiledelivery.ui.deliveryboy

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.common.ShopCategoryViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import com.example.lastmiledelivery.viewmodels.deliveryboy.DeliveryBoyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryBoyMainScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    deliveryBoyViewModel: DeliveryBoyViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val user = remember { authViewModel.getUserDetails() }


    LaunchedEffect(Unit) {
        if (!authViewModel.isLoggedIn()) {
            navController.navigate("login") {
                popUpTo("deliveryboy") { inclusive = true }
            }
        }
    }


    // Trigger data fetch when the composable enters composition
    LaunchedEffect(key1 = user.id) {
        deliveryBoyViewModel.getDeliveryBoyData(user.id)
    }

    LaunchedEffect(deliveryBoyViewModel.deliveryBoyState) {
        val storedDeliveryBoyId = deliveryBoyViewModel.getDeliveryBoyID()
        Log.d("DeliveryBoyMainScreen", "Stored DeliveryBoy Id: $storedDeliveryBoyId")
    }
    val deliveryBoyData = deliveryBoyViewModel.deliveryBoyState
    val error = deliveryBoyViewModel.errorMessage

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
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Menu",
                                tint = Color.White // ✅ Set icon color to white
                            )
                        }
                    },
                    actions = {
                        // Cart Icon
                        IconButton(onClick = {
                            navController.navigate("deliveryBoy_Profile")

                        }) {
                            AsyncImage(
                                model = deliveryBoyData?.profile_picture,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.Gray, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(id = R.color.pink)// ✅ Use a pink shade
                    )
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
//                deliveryBoyData?.delivery_boy_id?.let {
//                    StatusSwitchToggle(deliveryBoyViewModel,
//                        it
//                    )
//                }
                deliveryBoyData?.delivery_boy_id?.let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        StatusSwitchToggle(viewModel = deliveryBoyViewModel, deliveryBoyId = it)
                    }
                }

                Text(text = "Welcome Delivery Boy", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hi! ${user.name}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(start = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (deliveryBoyData == null) {
                    CircularProgressIndicator()
                } else {

                    deliveryBoyData.let {
                        Text(
                            text = "Hi! ${it.delivery_boy_id}",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier
                                .padding(start = 16.dp)
                        )
                        Text(
                            text = "Hi! ${it.approval_status}",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier
                                .padding(start = 16.dp)
                        )
                        AsyncImage(
                            model = it.profile_picture,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape),
                            contentScale = ContentScale.Crop
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
    authViewModel: AuthViewModel = hiltViewModel()
) {
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
            route = "deliveryboy",
            drawerState = drawerState,
            scope = scope
        )

        DrawerItem(
            text = "Orders",
            icon = Icons.Filled.ReceiptLong,
            navController = navController,
            route = "customerOrders",
            drawerState = drawerState,
            scope = scope
        )


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
fun DrawerItem(
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
                        popUpTo("deliveryboy") { inclusive = true }
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


@Composable
fun StatusSwitchToggle(
    viewModel: DeliveryBoyViewModel,
    deliveryBoyId: Int
) {
    val error = viewModel.errorMessageStatus
    val deliveryBoyState = viewModel.deliveryBoyState

    // Local toggle state
    var isOnline by remember { mutableStateOf(false) }

    // When status changes in state, update the switch
    LaunchedEffect(deliveryBoyState?.status) {
        isOnline = deliveryBoyState?.status == "Available"
    }

    // Initially load delivery boy data
    LaunchedEffect(deliveryBoyId) {
        viewModel.getDeliveryBoyData(deliveryBoyId)
    }

    val switchColor = colorResource(id = R.color.pink)

    Column(
        modifier = Modifier.padding(4.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isOnline) "Online" else "Offline",
                modifier = Modifier.padding(end = 8.dp),
                color = if (isOnline) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge
            )

            Switch(
                checked = isOnline,
                onCheckedChange = {
                    isOnline = it // update UI right away
                    viewModel.deliveryBoyToggleStatus(deliveryBoyId)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = switchColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }

//        error?.let {
//            Text(
//                text = "Error: $it",
//                color = Color.Red,
//                style = MaterialTheme.typography.bodyMedium
//            )
//        }
    }
}
