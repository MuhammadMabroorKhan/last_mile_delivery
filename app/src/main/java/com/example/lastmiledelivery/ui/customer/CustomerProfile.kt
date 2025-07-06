package com.example.lastmiledelivery.ui.customer


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    customerViewModel: CustomerViewModel = hiltViewModel()
) {
    val user = remember { authViewModel.getUserDetails() }

    // Trigger data fetch when the composable enters composition
    LaunchedEffect(key1 = user.id) {
        customerViewModel.fetchCustomerData(user.id)
    }
    // Observe customer data and error messages
    val customer = customerViewModel.customerState
//    LaunchedEffect(Unit) {
//        val customerId = customerViewModel.getCustomerId()
//        if (customerId != -1) {
//            if (customerId != null) {
//                customerViewModel.fetchCustomerData(customerId)
//            }
//        }
//    }
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
            customer?.let {
                // Profile Image
                val profileImage = it.profilePicture ?: "drawable/default_profile"
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
                ProfileOption(icon = Icons.Default.Person, title = "Personal Info") {
                    navController.navigate("personal_info")
                }
                ProfileOption(icon = Icons.Default.Favorite, title = "Favourite") {}
                ProfileOption(icon = Icons.Default.History, title = "Order History") {
                    navController.navigate("customer_order_history")
                }
//                ProfileOption(icon = Icons.Default.Help, title = "Help & Support") {}
//                ProfileOption(icon = Icons.Default.Star, title = "Rating & Review") {}
//                ProfileOption(icon = Icons.Default.Menu, title = "Create Menu") {}
//                ProfileOption(icon = Icons.Default.LocalShipping, title = "Courier") {}
            } ?: Text(text = "Loading...", fontSize = 18.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ProfileOption(icon: ImageVector, title: String, onClick: () -> Unit) {
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
            Icon(
                icon,
                contentDescription = title,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go",
                tint = Color.Gray
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPersonalInfoScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    customerViewModel: CustomerViewModel = hiltViewModel()
) {
    val user = remember { authViewModel.getUserDetails() }
    val context = LocalContext.current

    LaunchedEffect(user.id) {
        customerViewModel.fetchCustomerData(user.id)
    }

    val customer = customerViewModel.customerState

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var cnic by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var profilePicUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { profilePicUri = it }
        }

    LaunchedEffect(customer) {
        customer?.let {
            name = it.name
            email = it.email
            phone = it.phoneNo
            cnic = it.cnic
            password = it.password
            profilePicUri =
                it.profilePicture?.let { Uri.parse(it) } // Convert string to URI if available
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
            Image(
                painter = rememberAsyncImagePainter(profilePicUri ?: R.drawable.account_circle),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable {
                        imagePickerLauncher.launch("image/*") // Open image picker when clicked
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = cnic, onValueChange = { cnic = it }, label = { Text("CNIC") })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") })

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {

                    customerViewModel.updateCustomer(
                        customerId = user.id,
                        name = name,
                        email = email,
                        phoneNo = phone,
                        password = password,
                        cnic = cnic,
                        profilePictureUri = profilePicUri,
                        context = context // Pass context to handle file conversion
                    )


                    //navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Save Changes", color = Color.White)
            }
        }

        // Observe update result
        LaunchedEffect(customerViewModel.updateState) {
            customerViewModel.updateState?.let { result ->
                result.onSuccess {
                    Toast.makeText(context, "Customer updated successfully!", Toast.LENGTH_LONG)
                        .show()
                }.onFailure { error ->
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

