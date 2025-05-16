package com.example.lastmiledelivery.ui.vendor.API_Vendor

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.ui.vendor.VendorScaffold
import com.example.lastmiledelivery.ui.vendor.VendorSummaryScreen
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModel

@Composable
fun API_VendorMainScreen(navController: NavHostController, vendorId: Int) {
    val viewModel: VendorViewModel = hiltViewModel()
    val vendorState by viewModel.vendorData.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(initial = true) // Observe loading state

    Log.d("RecievedIDHERE", " before state ${vendorId}")
    vendorState?.let { result ->
        result.fold(
            onSuccess = { vendor ->
                if (viewModel.vendorId.value != vendor.vendorId) { // ✅ Only update if different
                    viewModel.setVendorId(vendor.vendorId)
                }
            },
            onFailure = { }
        )
    }

    Log.d("RecievedIDHERE", "after state ${vendorId}")
    LaunchedEffect(vendorId) {
        viewModel.getVendorData(vendorId)
    }

    VendorScaffold(navController, title = "API Vendor Dashboard") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {  // Show loader while fetching data
                CircularProgressIndicator()
            } else {
                vendorState?.let { result ->
                    result.fold(
                        onSuccess = { vendor ->
//                            Text(
//                                "Welcome ${vendor.name}",
//                                style = MaterialTheme.typography.headlineMedium
//                            )
//                            Spacer(modifier = Modifier.height(16.dp))
//                            Text(text = "ID: ${vendor.lmdUserId}", fontSize = 18.sp)
//                            Text(text = "Vendor ID: ${vendor.vendorId}", fontSize = 18.sp)
//                            Text(text = "Email: ${vendor.email}", fontSize = 18.sp)
//                            Text(text = "Phone: ${vendor.phoneNo}", fontSize = 18.sp)
//                            Text(text = "CNIC: ${vendor.cnic}", fontSize = 18.sp)
//                            Text(text = "Vendor Type: ${vendor.vendorType}", fontSize = 18.sp)
//
//                            if (vendor.profilePicture.isNullOrEmpty()) {
//                                Image(
//                                    painter = painterResource(id = R.drawable.account_circle),
//                                    contentDescription = "Default Profile Picture",
//                                    modifier = Modifier
//                                        .size(100.dp)
//                                        .clip(CircleShape)
//                                )
//                            } else {
//                                AsyncImage(
//                                    model = vendor.profilePicture,
//                                    contentDescription = "Vendor Profile Picture",
//                                    modifier = Modifier
//                                        .size(100.dp)
//                                        .clip(CircleShape)
//                                )
//                            }
                            VendorSummaryScreen(vendor.vendorId)
                        },
                        onFailure = { exception ->
                            Text("Error: E ${exception.message}", color = Color.Red)
                        }
                    )
                }
            }
        }
    }
}
