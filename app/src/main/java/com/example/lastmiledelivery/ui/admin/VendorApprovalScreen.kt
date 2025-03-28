package com.example.lastmiledelivery.ui.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.admin.VendorApproval
import com.example.lastmiledelivery.viewmodels.admin.VendorApprovalViewModel
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay



@Composable
fun VendorApprovalScreen(navController: NavHostController, viewModel: VendorApprovalViewModel = hiltViewModel()) {
    val vendors by viewModel.vendors.collectAsState()

    AdminScaffold(navController, title = "Vendor Approval") { // ✅ Pass title

        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            vendors.forEach { vendor ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            viewModel.selectVendor(vendor)
                            navController.navigate("vendorDetail")
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = if (vendor.profilePicture.isNullOrEmpty()) {
                            painterResource(id = R.drawable.account_circle)
                        } else {
                            rememberAsyncImagePainter(vendor.profilePicture)
                        },
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(text = vendor.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text(text = "Status: ${vendor.approvalStatus}")
                    }
                    Spacer(modifier = Modifier.weight(1f)) // ✅ Pushes the button to the right
                    Button(
                        onClick = {
                            viewModel.selectVendor(vendor)
                            navController.navigate("vendorDetail")
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id=R.color.pink)),
                    ) {
                        Text("Preview")
                    }
                }
                Divider()
            }
        }
    }
}

@Composable
fun VendorDetailScreen(navController: NavHostController, viewModel: VendorApprovalViewModel = hiltViewModel()) {
    val selectedVendor by viewModel.selectedVendor.collectAsState()
    val rejectionReasons by viewModel.rejectionReasons.collectAsState()
    val correctionStatus by viewModel.correctionStatus.collectAsState()

    selectedVendor?.let { vendor ->
        LaunchedEffect(vendor.vendorId) {
            viewModel.fetchRejectionReasons(vendor.vendorId)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Vendor Details", style = MaterialTheme.typography.headlineSmall)
            }


            // Vendor Info
                Spacer(modifier = Modifier.height(12.dp))


            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = if (vendor.profilePicture.isNullOrEmpty()) {
                        painterResource(id = R.drawable.account_circle)
                    } else {
                        rememberAsyncImagePainter(vendor.profilePicture)
                    },
                    contentDescription = "Vendor Profile Picture",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = vendor.name, onValueChange = {}, label = { Text("Name") }, readOnly = true)
                OutlinedTextField(value = vendor.email, onValueChange = {}, label = { Text("Email") }, readOnly = true)
                OutlinedTextField(value = vendor.phoneNo, onValueChange = {}, label = { Text("Phone No") }, readOnly = true)
                OutlinedTextField(value = vendor.approvalStatus, onValueChange = {}, label = { Text("Approval Status") }, readOnly = true)

                Spacer(modifier = Modifier.height(12.dp))
                var showDialog by remember { mutableStateOf(false) }
                Row {
                    Button(
                        onClick = { viewModel.approveVendor(vendor.vendorId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Accept")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Accept")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Reject")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reject")
                    }

                    if (showDialog) {
                        RejectVendorDialog(
                            vendorId = selectedVendor?.vendorId ?: 0,
                            viewModel = viewModel,
                            onDismiss = { showDialog = false }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

                if (rejectionReasons.isNotEmpty()) {
                    Text("Rejection Reasons", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(rejectionReasons) { reason ->
//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 4.dp),
//                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                            ) {
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(16.dp),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Column {
//                                        Text("Reason: ${reason.reason}")
//                                        Text("Status: ${reason.status}", color = if (reason.status == "Pending") Color.Red else Color.Green)
//                                    }
//
//                                    if (reason.status == "Pending") {
//                                        Button(onClick = { viewModel.correctRejectionReason(vendor.vendorId, reason.id) },colors = ButtonDefaults.buttonColors(containerColor = (Color.Transparent))) {
//                                            Text("Correct")
//                                        }
//                                    }
//                                }
//                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    // 📝 **Column for Texts** (Wraps Long Texts)
                                    Column(
                                        modifier = Modifier.weight(1f) // Ensures text takes needed space
                                    ) {
                                        Text(
                                            text = "Reason: ${reason.reason}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Status: ${reason.status}",
                                            color = if (reason.status == "Pending") Color.Red else Color.Green
                                        )
                                    }

                                    // ✅ **Button Stays in Place & Doesn’t Shrink**
                                    if (reason.status == "Pending") {
                                        Button(
                                            onClick = { viewModel.correctRejectionReason(vendor.vendorId, reason.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id=R.color.pink)),
                                            modifier = Modifier.wrapContentWidth(Alignment.End) // Keeps button width stable
                                        ) {
                                            Text("Correct")
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))



            // Show correction status as a Snackbar
            correctionStatus?.let { message ->
                LaunchedEffect(message) {
                    delay(3000) // Auto-dismiss after 3 seconds
                    viewModel.clearCorrectionStatus()
                }
                Snackbar {
                    Text(message)
                }
            }
        }
    }
}

@Composable
fun RejectVendorDialog(vendorId: Int, viewModel: VendorApprovalViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val rejectionReasons = listOf("Invalid documents", "CNIC Not Valid", "Fake Info", "Invalid Profile Picture")
    val selectedReasons = remember { mutableStateListOf<String>() }
    val rejectResult by viewModel.rejectResult.observeAsState()

    LaunchedEffect(rejectResult) {
        rejectResult?.let { result ->
            result.onSuccess {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                onDismiss() // Close dialog after success
            }.onFailure {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Reject Vendor") },
//        text = {
//            Column {
//                Text("Select rejection reasons:")
//                rejectionReasons.forEach { reason ->
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Checkbox(
//                            checked = selectedReasons.contains(reason),
//                            onCheckedChange = { isChecked ->
//                                if (isChecked) selectedReasons.add(reason) else selectedReasons.remove(reason)
//                            }
//                        )
//                        Text(text = reason)
//                    }
//                }
//            }
//        },
//        confirmButton = {
//            Button(
//                onClick = {
//                    if (selectedReasons.isEmpty()) {
//                        Toast.makeText(context, "Please select at least one reason", Toast.LENGTH_SHORT).show()
//                    } else {
//                        viewModel.rejectVendor(vendorId, selectedReasons)
//                    }
//                }
//            ) {
//                Text("Confirm Rejection")
//            }
//        },
//        dismissButton = {
//            Button(onClick = onDismiss) {
//                Text("Cancel")
//            }
//        }
//    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reject Vendor",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.pink) // Pink Title
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column {
                Text("Select rejection reasons:", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                rejectionReasons.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = selectedReasons.contains(reason),
                            onCheckedChange = { isChecked ->
                                if (isChecked) selectedReasons.add(reason) else selectedReasons.remove(reason)
                            }
                        )
                        Text(text = reason, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedReasons.isEmpty()) {
                        Toast.makeText(context, "Please select at least one reason", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.rejectVendor(vendorId, selectedReasons)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            ) {
                //Icon(imageVector = Icons.Filled.Warning, contentDescription = "Reject", tint = Color.White)
                //Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Rejection", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                //Icon(imageVector = Icons.Filled.Close, contentDescription = "Cancel", tint = Color.White)
                //Spacer(modifier = Modifier.width(8.dp))
                Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )

}

