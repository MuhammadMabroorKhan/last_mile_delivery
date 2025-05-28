package com.example.lastmiledelivery.ui.vendor.API_Vendor

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.Cities
import com.example.lastmiledelivery.ui.common.CityDropdown
import com.example.lastmiledelivery.viewmodels.common.CitiesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun API_VendorItemsScreen(
    shopcategory_name: String,
    shopcategory_ID: String,
    vendorId: String,
    branchId: String,
    shopId: String,
    approvalStatus: String,
    onBackPressed: () -> Unit,
    navController: NavHostController
) {

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
            Button(
                onClick = { navController.navigate("vendororderscreen/${vendorId}/${branchId}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.pink)) // Pink color
            ) {
                Icon(
                    imageVector = Icons.Filled.List, // Shop-related icon
                    contentDescription = "View Order",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                Text(
                    text = "View Orders",
                    color = Color.White, // White text for contrast
                    fontWeight = FontWeight.Bold
                )
            }

            Text(text = "API Vendor Items Screen", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Vendor ID: $vendorId", fontSize = 18.sp)
            Text(text = "Branch ID: $branchId", fontSize = 18.sp)
            Text(text = "Shop ID: $shopId", fontSize = 18.sp)
            Text(text = "Approval Status: $approvalStatus", fontSize = 18.sp)
        }
    }
}