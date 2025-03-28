package com.example.lastmiledelivery.ui.vendor.API_Vendor

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lastmiledelivery.data.models.Cities
import com.example.lastmiledelivery.ui.common.CityDropdown
import com.example.lastmiledelivery.viewmodels.common.CitiesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun API_VendorItemsScreen(    shopcategory_name: String,
                              shopcategory_ID: String,vendorId: String, branchId: String, shopId: String, approvalStatus: String) {



    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "API Vendor Items Screen", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Vendor ID: $vendorId", fontSize = 18.sp)
        Text(text = "Branch ID: $branchId", fontSize = 18.sp)
        Text(text = "Shop ID: $shopId", fontSize = 18.sp)
        Text(text = "Approval Status: $approvalStatus", fontSize = 18.sp)
    }
}
