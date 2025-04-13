package com.example.lastmiledelivery.ui.vendor

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.vendor.AvailableOrganization
import com.example.lastmiledelivery.data.models.vendor.RequestedOrganization
import com.example.lastmiledelivery.viewmodels.vendor.VendorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorOrganizationsConnection(
    vendorId: Int,
    navController: NavHostController,
    viewModel: VendorViewModel = hiltViewModel()
) {
    val available = viewModel.availableOrganizations
    val requested = viewModel.requestedOrganizations
    val isLoading = viewModel.isLoadingOrganization
    val error = viewModel.errorMessage
    var selectedOrg by remember { mutableStateOf<AvailableOrganization?>(null) }
    var reqConOrg by remember { mutableStateOf<RequestedOrganization?>(null) }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.loadOrganizations(vendorId)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Organization", color = Color.White) },
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
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (available.isNotEmpty()) {
                            Text(
                                "Available Organizations",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            available.forEach { org ->
                                OrganizationCard(
                                    name = org.name,
                                    email = org.email,
                                    profileUrl = org.profilePicture,
                                    showButton = true,
                                    onClick = {
                                        selectedOrg = org
                                    },
                                    onRequestClick = {
                                        // You can call a ViewModel function here to request connection
//
                                        viewModel.connectToOrganization(
                                            vendorId,
                                            org.organizationId
                                        )

                                    }
                                )
                            }

                            Spacer(Modifier.height(16.dp))
                        }

                        if (requested.isNotEmpty()) {
                            Text(
                                "Requested / Connected Organizations",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            requested.forEach { org ->

//                                OrganizationCard(
//                                    it.name,
//                                    it.email,
//                                    it.profilePicture,
//                                    it.approvalStatus
//                                )
                                OrganizationCard(
                                    name = org.name,
                                    email = org.email,
                                    profileUrl = org.profilePicture,
                                    showButton = true,
                                    onClick = {
                                        reqConOrg = org
                                    }
                                )
                            }
                        }
                    }
                    selectedOrg?.let {
                        OrganizationDetailDialog(
                            organization = it,
                            onDismiss = { selectedOrg = null })
                    }
                    reqConOrg?.let {
                        OrganizationDetailDialogReq(
                            organization = it,
                            onDismiss = { reqConOrg = null })
                    }
                    viewModel.connectMessage?.let {
                        Toast.makeText(context, "\"✅ $it\"", Toast.LENGTH_LONG).show()
                    }

                    viewModel.connectError?.let {
                        Toast.makeText(context, "\"❌ $it\"", Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
    }

}


@Composable
fun OrganizationCard(
    name: String,
    email: String,
    profileUrl: String?,
    approvalStatus: String? = null,
    showButton: Boolean = false,
    onClick: () -> Unit = {},
    onRequestClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            if (!profileUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = profileUrl,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.bodyLarge)
                Text(text = email, style = MaterialTheme.typography.bodySmall)
                approvalStatus?.let {
                    Text("Approval Status: $it", style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (showButton && onRequestClick != null) {
                Button(
                    onClick = onRequestClick,
                    modifier = Modifier.padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.pink))
                ) {
                    Text("Request", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun OrganizationDetailDialog(
    organization: AvailableOrganization,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Text(organization.name)
        },
        text = {
            Column {
                if (!organization.profilePicture.isNullOrEmpty()) {
                    AsyncImage(
                        model = organization.profilePicture,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                }
                Text("Email: ${organization.email}")
                Text("Phone: ${organization.phoneNo}")
                Text("CNIC: ${organization.cnic}")
                Text("Role: ${organization.lmdUserRole}")

            }
        }
    )
}

@Composable
fun OrganizationDetailDialogReq(
    organization: RequestedOrganization,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Text(organization.name)
        },
        text = {
            Column {
                if (!organization.profilePicture.isNullOrEmpty()) {
                    AsyncImage(
                        model = organization.profilePicture,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                }
                Text("Email: ${organization.email}")
                Text("Phone: ${organization.phoneNo}")
                Text("CNIC: ${organization.cnic}")
                Text("Role: ${organization.lmdUserRole}")
                Text("Approval Status: ${organization.approvalStatus}")
            }
        }
    )
}