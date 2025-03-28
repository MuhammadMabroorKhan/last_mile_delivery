package com.example.lastmiledelivery.ui.common

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, authViewModel: AuthViewModel = hiltViewModel()) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(2000) // Simulate loading
        if (authViewModel.isLoggedIn()) {
            val role = authViewModel.getUserRole()
            if (role != null) {
                navController.navigate(role) {
                    popUpTo("splash") { inclusive = true } // Clear back stack
                }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Welcome to LMDS",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "We are excited to introduce our innovative Last Mail Delivery System, designed to enhance your mailing experience with efficiency and reliability.",
                fontSize = 18.sp,
                color = (Color.LightGray), // Text color
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(300.dp) // Set width to 300dp
                    .padding(top = 20.dp, start = 10.dp, end = 10.dp) // Top margin + horizontal padding
            )
        }
    }
}
