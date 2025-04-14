package com.example.lastmiledelivery.ui.organization

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.organization.VendorConnectionRequest
import com.example.lastmiledelivery.viewmodels.AuthViewModel
import com.example.lastmiledelivery.viewmodels.common.ShopCategoryViewModel
import com.example.lastmiledelivery.viewmodels.customer.CustomerViewModel
import com.example.lastmiledelivery.viewmodels.organization.OrganizationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorConnectionRequest(
    viewModel: OrganizationViewModel = hiltViewModel(),
    organizationId: Int,
    navController: NavHostController
) {
    val vendorRequests = viewModel.vendorRequestsOrganizationForCOnnection
    val error = viewModel.errorMessageVendorRequest
    val loading = viewModel.isLoadingVendorRequest

    // Trigger API call once
    LaunchedEffect(Unit, vendorRequests) {
        viewModel.loadVendorRequests(organizationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendor Request", color = Color.White) },
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
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {


            when {
                loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("❌ Error: $error", color = Color.Red)
                    }
                }

                vendorRequests != null -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {

                        if (vendorRequests.pendingRequests.isNotEmpty()) {
                            item {
                                SectionTitle("⏳ Pending Requests")
                            }
                            items(vendorRequests.pendingRequests) { request ->
                                VendorCard(request, navController)
                            }
                        }

                        if (vendorRequests.approvedRequests.isNotEmpty()) {
                            item {
                                SectionTitle("✅ Approved Requests")
                            }
                            items(vendorRequests.approvedRequests) { request ->
                                VendorCard(request, navController)
                            }
                        }

                        if (vendorRequests.rejectedRequests.isNotEmpty()) {
                            item {
                                SectionTitle("❌ Rejected Requests")
                            }
                            items(vendorRequests.rejectedRequests) { request ->
                                VendorCard(request, navController)
                            }
                        }

                        if (
                            vendorRequests.pendingRequests.isEmpty() &&
                            vendorRequests.approvedRequests.isEmpty() &&
                            vendorRequests.rejectedRequests.isEmpty()
                        ) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No requests found.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun VendorCard(request: VendorConnectionRequest, navController: NavHostController) {

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate(
                    "vendor_request_detail/${Uri.encode(request.vendorName)}/" +
                            "${Uri.encode(request.vendorEmail)}/" +
                            "${Uri.encode(request.vendorPhone)}/" +
                            "${Uri.encode(request.orgUserName)}/" +
                            "${Uri.encode(request.approvalStatus)}/" +
                            "${Uri.encode(request.vendorProfilePicture)}/" +
                            "${Uri.encode(request.organizationId.toString())}/" +
                            "${Uri.encode(request.requestId.toString())}/" +
                            "${Uri.encode(request.vendorId.toString())}"
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = request.vendorProfilePicture,
                    contentDescription = "Vendor Image",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Vendor: ${request.vendorName}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "Email: ${request.vendorEmail}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Phone: ${request.vendorPhone}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Organization: ${request.orgUserName}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Status: ${request.approvalStatus.uppercase()}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorRequestDetailScreen(
    vendorName: String,
    vendorEmail: String,
    vendorPhone: String,
    orgUserName: String,
    approvalStatus: String,
    vendorProfilePicture: String,
    organizationId: String,
    requestID: String,
    vendorId: String,
    navController: NavHostController,
    viewModel: OrganizationViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showRejectDialog by remember { mutableStateOf(false) }
    val reasonsList = listOf(
        "Incomplete documents submitted.",
        "Invalid business license.",
        "Mismatch between vendor name and registration.",
        "Other compliance issues"
    )
    val selectedReasons = remember { mutableStateListOf<String>() }


    Scaffold(
//
        topBar = {
            TopAppBar(
                title = { Text("Vendor Request Detail", color = Color.White) },
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
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = vendorProfilePicture,
                contentDescription = "Vendor Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )

            Text(
                "Vendor ID: $vendorId Organization ID:$organizationId Request ID:$requestID",
                style = MaterialTheme.typography.bodyLarge
            )
            Text("Name: $vendorName", style = MaterialTheme.typography.bodyLarge)
            Text("Email: $vendorEmail", style = MaterialTheme.typography.bodyMedium)
            Text("Phone: $vendorPhone", style = MaterialTheme.typography.bodyMedium)
            Text("Organization: $orgUserName", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Status: ${approvalStatus.uppercase()}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            val status = approvalStatus.lowercase()

            if (status == "pending") {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//                    Button(onClick = { /* Handle Approve */ }, modifier = Modifier.weight(1f)) {
//                        Text("Approve")
//                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.acceptVendorRequest(requestID.toInt()) {
                                viewModel.acceptRequestMessage?.let { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    coroutineScope.launch {
                                        delay(2000)
                                        navController.popBackStack()
                                    }
                                }
                            }

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Green
                    ) {
                        Text(text = "Approve", color = Color.White)
                    }
//                    Button(
//                        onClick = { /* Handle Reject */ },
//                        modifier = Modifier.weight(1f),
//                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//                    ) {
//                        Text("Reject", color = Color.White)
//                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { showRejectDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Reject", color = Color.White)
                    }

                }
            } else if (status == "rejected") {
//
                LaunchedEffect(Unit) {
                    viewModel.fetchRejectionReasons(organizationId.toInt()) // use actual organization ID
                }

                Column {
                    Button(
                        onClick = {
                            viewModel.acceptVendorRequest(requestID.toInt()) {
                                viewModel.acceptRequestMessage?.let { message ->
                                    Toast.makeText(
                                        context,
                                        "Cannot Approve With Pending Rejection Reasons\n$message",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    coroutineScope.launch {
                                        delay(2000)
                                        navController.popBackStack()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(text = "Approve", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Rejection Reasons",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    if (viewModel.rejectionError != null) {
                        Text(
                            text = viewModel.rejectionError!!,
                            color = Color.Red,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .padding(horizontal = 8.dp)
                        ) {
                            items(viewModel.rejectionReasons) { reason ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = reason.reason,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "Status: ${reason.status}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (reason.status == "Pending") Color.Red else Color.Gray
                                            )
                                        }
                                        Button(
                                            onClick = {
//
                                                viewModel.correctRejectionReason(reason.id) { success ->
                                                    Toast.makeText(
                                                        context,
                                                        viewModel.correctReasonMessage
                                                            ?: if (success) "Corrected" else "Failed",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }


                                            },
                                            enabled = reason.status == "Pending",
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(
                                                    0xFF2196F3
                                                )
                                            )
                                        ) {
                                            Text("Correct", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            }

            // Reject Dialog
            if (showRejectDialog) {
                AlertDialog(
                    onDismissRequest = { showRejectDialog = false },
                    title = { Text("Reject Request") },
                    text = {
                        Column {
                            Text("Select at least one reason:")
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyColumn(modifier = Modifier.height(150.dp)) {
                                items(reasonsList) { reason ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (selectedReasons.contains(reason)) {
                                                    selectedReasons.remove(reason)
                                                } else {
                                                    selectedReasons.add(reason)
                                                }
                                            }
                                            .padding(8.dp)
                                    ) {
                                        Checkbox(
                                            checked = selectedReasons.contains(reason),
                                            onCheckedChange = {
                                                if (it) selectedReasons.add(reason)
                                                else selectedReasons.remove(reason)
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(reason)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (selectedReasons.isNotEmpty()) {
                                    viewModel.rejectVendorRequest(
                                        requestID.toInt(),
                                        selectedReasons.toList()
                                    ) { success ->
                                        Toast.makeText(
                                            context,
                                            viewModel.rejectMessage ?: "Rejected",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showRejectDialog = false
                                        coroutineScope.launch {
                                            delay(2000)
                                            navController.popBackStack()
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please select at least one reason",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        ) {
                            Text("Reject")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showRejectDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
