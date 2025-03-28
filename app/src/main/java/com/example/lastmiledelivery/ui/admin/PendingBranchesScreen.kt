package com.example.lastmiledelivery.ui.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.admin.PendingBranch
import com.example.lastmiledelivery.viewmodels.admin.VendorApprovalViewModel
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
@Composable
fun PendingBranchesScreen(navController: NavHostController, viewModel: VendorApprovalViewModel = hiltViewModel()) {

    AdminScaffold(navController, title = "Branch Approval") { // ✅ Pass title

        val pendingBranches by viewModel.pendingBranches.collectAsState(initial = emptyList()) // Ensure it starts with an empty list

        val errorMessage by viewModel.errorMessage.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//            Text("Pending Branches", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
            if (errorMessage != null) {
                Text(errorMessage!!, color = Color.Red, modifier = Modifier.padding(8.dp))
            }

            LazyColumn {
                items(pendingBranches) { branch ->
                    BranchItem(branch, navController)
                }
            }
        }

    }
}


@Composable
fun BranchItem(branch: PendingBranch, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("branchDetail/${branch.branchId}") }
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = branch.branchPicture,
                contentDescription = branch.shopName,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(branch.shopName,style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Text("Category: ${branch.shopCategory}", style = MaterialTheme.typography.bodySmall)
                Text("Approval: ${branch.branchApprovalStatus}", style = MaterialTheme.typography.bodySmall)
                Text("Vendor: ${branch.vendorName}", style = MaterialTheme.typography.bodySmall)
            }
            Button(
                onClick = { navController.navigate("branchDetail/${branch.branchId}") },
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id=R.color.pink)),
            ) {
                Text("Preview")
            }
        }
    }
}


@Composable
fun BranchDetailScreen(
    branchId: Int,
    viewModel: VendorApprovalViewModel = hiltViewModel(),
    navController: NavController
) {
    val selectedBranch by viewModel.selectedBranch.collectAsState()
    val correctionStatus by viewModel.branchCorrectionStatus.collectAsState()
    val rejectionReasons by viewModel.branchRejectionReasons.collectAsState()



    LaunchedEffect(branchId) {
        val branch = viewModel.pendingBranches.value.find { it.branchId == branchId }
        if (branch != null) {
            viewModel.selectBranch(branch)
        }
        viewModel.getBranchRejectionReasons(branchId)
    }


    LaunchedEffect(rejectionReasons) {
        Log.d("RejectionReasons", "Updated Reasons: $rejectionReasons")
        Log.d("RejectionReasons", "Branch id is $branchId selectedBranch.branchId: $selectedBranch.branchId")
    }

    selectedBranch?.let { branch ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Branch Details", style = MaterialTheme.typography.headlineSmall)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AsyncImage(
                        model = branch.branchPicture,
                        contentDescription = branch.shopName,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(value = branch.shopName, onValueChange = {}, label = { Text("Shop Name") }, readOnly = true)
                        OutlinedTextField(value = branch.shopCategory, onValueChange = {}, label = { Text("Category") }, readOnly = true)
                        OutlinedTextField(value = branch.branchDescription, onValueChange = {}, label = { Text("Description") }, readOnly = true)
                        OutlinedTextField(value = branch.vendorName, onValueChange = {}, label = { Text("Vendor") }, readOnly = true)
                        OutlinedTextField(value = branch.vendorEmail, onValueChange = {}, label = { Text("Email") }, readOnly = true)
                        OutlinedTextField(value = branch.branchApprovalStatus, onValueChange = {}, label = { Text("Branch Approval Status") }, readOnly = true)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item {
                    var showDialog by remember { mutableStateOf(false) }
                    Row {
                        Button(
                            onClick = { viewModel.approveBranch(branchId) },
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
                    }

                    if (showDialog) {
                        RejectBranchDialog(
                            branchId = branchId,
                            viewModel = viewModel,
                            onDismiss = { showDialog = false }
                        )
                    }
                }

                if (rejectionReasons.isNotEmpty()) {
                    item {
                        Text(
                            "Rejection Reasons",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    items(rejectionReasons) { reason ->
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
                                        onClick = { viewModel.correctBranchRejectionReason(branchId, reason.id) },
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

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    correctionStatus?.let { message ->
                        LaunchedEffect(message) {
                            delay(3000)
                            viewModel.clearBranchCorrectionStatus()
                        }
                        Snackbar {
                            Text(message)
                        }
                    }
                }
            }
        }
    }


}



@Composable
fun RejectBranchDialog(branchId: Int, viewModel: VendorApprovalViewModel= hiltViewModel(), onDismiss: () -> Unit) {
    val context = LocalContext.current
    val rejectionReasons = listOf("Invalid documents", "Incorrect location", "Fake Information", "Policy Violation")
    val selectedReasons = remember { mutableStateListOf<String>() }
    val rejectionResult by viewModel.rejectionState.collectAsState()

    LaunchedEffect(rejectionResult) {
        rejectionResult?.let { result ->
            result.onSuccess {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                onDismiss() // Close dialog after success
            }.onFailure {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reject Branch",
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
                        viewModel.rejectBranch(branchId, selectedReasons)
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
               // Spacer(modifier = Modifier.width(8.dp))
                Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )

}


@Composable
fun BranchRejectionScreen(viewModel: VendorApprovalViewModel, branchId: Int) {
    val rejectionReasons by viewModel.branchRejectionReasons.collectAsState()
    val correctionStatus by viewModel.branchCorrectionStatus.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getBranchRejectionReasons(branchId)
    }




    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Branch Rejection Reasons", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        if (rejectionReasons.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(rejectionReasons) { reason ->
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Reason: ${reason.reason}")
                                Text("Status: ${reason.status}", color = if (reason.status == "Pending") Color.Red else Color.Green)
                            }

                            if (reason.status == "Pending") {
                                Button(onClick = { viewModel.correctBranchRejectionReason(branchId, reason.id) }) {
                                    Text("Correct")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Text("No rejection reasons found", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show correction status as a Snackbar
        correctionStatus?.let { message ->
            LaunchedEffect(message) {
                delay(3000)
                viewModel.clearBranchCorrectionStatus()
            }
            Snackbar {
                Text(message)
            }
        }
    }
}



