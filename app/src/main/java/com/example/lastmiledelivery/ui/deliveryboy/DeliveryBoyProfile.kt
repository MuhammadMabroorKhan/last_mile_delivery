package com.example.lastmiledelivery.ui.deliveryboy

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import com.example.lastmiledelivery.viewmodels.deliveryboy.DeliveryBoyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryBoyProfileScreen(navController: NavHostController, authViewModel: AuthViewModel = hiltViewModel(), deliveryBoyViewModel: DeliveryBoyViewModel = hiltViewModel()) {
    val user = remember { authViewModel.getUserDetails() }

    // Trigger data fetch when the composable enters composition
    LaunchedEffect(key1 = user.id) {
        deliveryBoyViewModel.getDeliveryBoyData(user.id)
    }
    // Observe customer data and error messages
    val deliveryBoy = deliveryBoyViewModel.deliveryBoyState
    LaunchedEffect(Unit) {
        val deliveryBoyId = deliveryBoyViewModel.getDeliveryBoyID()
        if (deliveryBoyId != -1) {
            if (deliveryBoyId != null) {
                deliveryBoyViewModel.getDeliveryBoyData(deliveryBoyId)
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            deliveryBoy?.let {
                // Profile Image
                val profileImage = it.profile_picture ?: "drawable/default_profile"
                Image(
                    painter = rememberAsyncImagePainter(profileImage),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                )

                Spacer(modifier = Modifier.height(8.dp))


                // Name & Email
                Text(
                    text = it.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = it.email,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Options
                DeliveryBoyProfileOption(icon = Icons.Default.Person, title = "Personal Info") {}
                DeliveryBoyProfileOption(icon = Icons.Default.DirectionsCar, title = "Vehicle") {
                    navController.navigate("vehicle_screen/${it.delivery_boy_id}")
                }
                DeliveryBoyProfileOption(icon = Icons.Default.History, title = "Past Order") {}
//                ProfileOption(icon = Icons.Default.Help, title = "Help & Support") {}
//                ProfileOption(icon = Icons.Default.Star, title = "Rating & Review") {}
//                ProfileOption(icon = Icons.Default.Menu, title = "Create Menu") {}
//                ProfileOption(icon = Icons.Default.LocalShipping, title = "Courier") {}
            } ?: Text(text = "Loading...", fontSize = 18.sp, color = Color.Gray)
        }
    }
}

@Composable
fun DeliveryBoyProfileOption(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = Color.Black, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Go", tint = Color.Gray)
        }
    }
}
