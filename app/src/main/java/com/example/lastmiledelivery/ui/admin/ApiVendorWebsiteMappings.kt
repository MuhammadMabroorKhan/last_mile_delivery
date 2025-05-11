package com.example.lastmiledelivery.ui.admin

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.admin.ApiVendorRequest
import com.example.lastmiledelivery.data.models.admin.MappingInput
import com.example.lastmiledelivery.data.models.admin.MethodInputForApiVendor
import com.example.lastmiledelivery.viewmodels.admin.AdminViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiVendorWebsiteMappingInfo(
    navController: NavHostController,
    vendorId: Int,
    shopID: Int,
    branchID: Int,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val integration = viewModel.integrationState
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadIntegrationDetails(branchID)
    }

    // Proper toast trigger only once when integration fails
    LaunchedEffect(integration?.status) {
        if (integration != null && !integration.status) {
            Toast.makeText(context, "Integration not found", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Branch Integration Mapping", color = Color.White) },
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
    }) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Integration not available", color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Navigate to add form */
                    navController.navigate("add_integration/$vendorId/$shopID/$branchID")
                }) {
                    Text("Add Integration")
                }
            }

//            when {
//                viewModel.isLoadingIntegration -> {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                }
//
//                integration == null -> {
//                    Text(
//                        text = "Loading failed or no response",
//                        modifier = Modifier.align(Alignment.Center),
//                        color = Color.Gray
//                    )
//                }
//
//                !integration.status -> {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp),
//                        verticalArrangement = Arrangement.Center,
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text("Integration not available", color = Color.Red)
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Button(onClick = { /* Navigate to add form */
//                            navController.navigate("add_integration/$vendorId/$shopID/$branchID")
//                        }) {
//                            Text("Add Integration")
//                        }
//                    }
//                }
//
//                else -> {
//                    val data = integration.data!!
//                    LazyColumn(modifier = Modifier.fillMaxSize()) {
//                        item {
//                            Text("API Vendor Details", modifier = Modifier.padding(8.dp))
//                            Text(
//                                "Base URL: ${data.apivendor?.api_base_url}",
//                                modifier = Modifier.padding(start = 8.dp)
//                            )
//                            Spacer(modifier = Modifier.height(8.dp))
//                        }
//
//                        item {
//                            Text("API Methods", modifier = Modifier.padding(8.dp))
//                        }
//
//                        items(data.apimethods) { method ->
//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(8.dp)
//                            ) {
//                                Column(Modifier.padding(8.dp)) {
//                                    Text("Name: ${method.method_name}")
//                                    Text("HTTP: ${method.http_method}")
//                                    Text("Endpoint: ${method.endpoint}")
//                                }
//                            }
//                        }
//
//                        item {
//                            Text("Variable Mappings", modifier = Modifier.padding(8.dp))
//                        }
//
//                        items(data.mappings) { mapping ->
//                            val variable = data.variables.find { it.id == mapping.variable_ID }
//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(8.dp)
//                            ) {
//                                Column(Modifier.padding(8.dp)) {
//                                    Text("Variable: ${variable?.tags}")
//                                    Text("API Value: ${mapping.api_values}")
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIntegrationScreen(
    navController: NavHostController,
    vendorId: Int,
    shopID: Int,
    branchID: Int,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val integrationData = viewModel.vendorIntegrationDetails
    val message = viewModel.vendorIntegrationMessage
    val loading = viewModel.isLoadingIntegration

    // Trigger API on first composition
    LaunchedEffect(Unit) {
        viewModel.getVendorIntegration(branchID)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Branch Integration Mapping", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorResource(id = R.color.pink)
            )
        )
    }) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            when {
                loading -> {
                    CircularProgressIndicator()
                }

                integrationData != null -> {
                    // Show existing integration in a card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Toast
                                    .makeText(
                                        context,
                                        "${integrationData.id}Integration already exists ${branchID}",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()

                                navController.navigate("integration_detail_api_methods/${integrationData.id}/${branchID}")
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("API Key: ${integrationData.api_key}")
                            Text("Base URL: ${integrationData.api_base_url}")
                            Text("Version: ${integrationData.api_version}")
                            Text("Status: ${integrationData.vendor_integration_status}")
                        }
                    }
                }

                else -> {
                    // If no integration found, show form
                    AddApiVendorForm(
                        navController = navController,
                        viewModel = viewModel,
                        branchID = branchID
                    )
                }
            }
        }
    }
}

@Composable
fun AddApiVendorForm(
    navController: NavHostController,
    viewModel: AdminViewModel,
    branchID: Int
) {
    var apiKey by remember { mutableStateOf("") }
    var apiBaseUrl by remember { mutableStateOf("") }
    var responseFormat by remember { mutableStateOf("JSON") }

    val loading = viewModel.loadingApiVendor
    val error = viewModel.errorMessageApiVendor
    val response = viewModel.integrationResponse

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("API Key") },
            singleLine = true
        )

        OutlinedTextField(
            value = apiBaseUrl,
            onValueChange = { apiBaseUrl = it },
            label = { Text("API Base URL") },
            singleLine = true
        )

        OutlinedTextField(
            value = "Bearer",
            onValueChange = {},
            label = { Text("Auth Method") },
            readOnly = true,
            enabled = false
        )
        OutlinedTextField(
            value = "v1",
            onValueChange = {},
            label = { Text("API Version") },
            readOnly = true,
            enabled = false
        )
        OutlinedTextField(
            value = "Active",
            onValueChange = {},
            label = { Text("Status") },
            readOnly = true,
            enabled = false
        )

        Text("Response Format")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = responseFormat == "JSON",
                onClick = {}, // Prevent changes for now
                enabled = false // Visually indicates it's fixed
            )
            Text("JSON")

            // ==== Uncomment this block when XML support is ready ====
            /*
                            Spacer(modifier = Modifier.width(16.dp))

                            RadioButton(
                                selected = responseFormat == "XML",
                                onClick = { responseFormat = "XML" }
                            )
                            Text("XML")

                            Spacer(modifier = Modifier.width(16.dp))

                            RadioButton(
                                selected = responseFormat == "JSON",
                                onClick = { responseFormat = "JSON" }
                            )
                            Text("JSON")
            */
        }


        Button(
            onClick = {
                val request = ApiVendorRequest(
                    api_key = apiKey,
                    api_base_url = apiBaseUrl.ifBlank { null },
                    api_auth_method = "Bearer",
                    api_version = "v1",
                    vendor_integration_status = "Active",
                    response_format = responseFormat,
                    branches_ID = branchID
                )
                viewModel.addApiVendor(request)
            },
            enabled = !loading
        ) {
            Text(if (loading) "Saving..." else "Submit")
        }

        response?.let {
            Text("Response: ${it.message}", color = Color.Green)

            // Refresh the integration details after successful add
            LaunchedEffect(Unit) {
                viewModel.getVendorIntegration(branchID)
                viewModel.clearIntegrationResponse() // optional: to avoid re-triggering
            }
        }

        error?.let { Text("Error: $it", color = Color.Red) }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddIntegrationScreen(
//    navController: NavHostController,
//    vendorId: Int,
//    shopID: Int,
//    branchID: Int,
//    viewModel: AdminViewModel = hiltViewModel()
//) {
//    var selectedTab by remember { mutableStateOf("API_INFO") } // <-- NEW
//    var apiKey by remember { mutableStateOf("") }
//    var apiBaseUrl by remember { mutableStateOf("") }
//    var responseFormat by remember { mutableStateOf("JSON") }
//
//    val loading = viewModel.loadingApiVendor
//    val error = viewModel.errorMessageApiVendor
//    val response = viewModel.integrationResponse
//
//    Scaffold(topBar = {
//        TopAppBar(
//            title = { Text("Branch Integration Mapping", color = Color.White) },
//            navigationIcon = {
//                IconButton(onClick = { navController.popBackStack() }) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
//                }
//            },
//            colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = colorResource(id = R.color.pink)
//            )
//        )
//    }) { padding ->
//
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//        ) {
//            // === Tabs ===
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                Button(
//                    onClick = { selectedTab = "API_INFO" },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = if (selectedTab == "API_INFO") Color.Gray else Color.LightGray
//                    )
//                ) {
//                    Text("API Info")
//                }
//                Button(
//                    onClick = { selectedTab = "METHODS" },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = if (selectedTab == "METHODS") Color.Gray else Color.LightGray
//                    )
//                ) {
//                    Text("Methods")
//                }
//                Button(
//                    onClick = { selectedTab = "MAPPING" },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = if (selectedTab == "MAPPING") Color.Gray else Color.LightGray
//                    )
//                ) {
//                    Text("Mapping")
//                }
//            }
//
//            // === Screen Switching ===
//            when (selectedTab) {
//                "API_INFO" -> {
//                    // Your existing API Info form
//                    Column(
//                        modifier = Modifier
////                            .padding(padding)
//                            .padding(16.dp)
//                            .fillMaxSize(),
//                        verticalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        OutlinedTextField(
//                            value = apiKey,
//                            onValueChange = { apiKey = it },
//                            label = { Text("API Key") },
//                            singleLine = true
//                        )
//
//                        OutlinedTextField(
//                            value = apiBaseUrl,
//                            onValueChange = { apiBaseUrl = it },
//                            label = { Text("API Base URL") },
//                            singleLine = true
//                        )
//
//                        // Read-only values
//                        OutlinedTextField(
//                            value = "Bearer",
//                            onValueChange = {},
//                            label = { Text("Auth Method") },
//                            readOnly = true,
//                            enabled = false
//                        )
//
//                        OutlinedTextField(
//                            value = "v1",
//                            onValueChange = {},
//                            label = { Text("API Version") },
//                            readOnly = true,
//                            enabled = false
//                        )
//
//                        OutlinedTextField(
//                            value = "Active",
//                            onValueChange = {},
//                            label = { Text("Status") },
//                            readOnly = true,
//                            enabled = false
//                        )
//
//                        // Response Format (Radio Button)
//                        Text("Response Format")
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            RadioButton(
//                                selected = responseFormat == "JSON",
//                                onClick = {}, // Prevent changes for now
//                                enabled = false // Visually indicates it's fixed
//                            )
//                            Text("JSON")
//
//                            // ==== Uncomment this block when XML support is ready ====
//                            /*
//                                            Spacer(modifier = Modifier.width(16.dp))
//
//                                            RadioButton(
//                                                selected = responseFormat == "XML",
//                                                onClick = { responseFormat = "XML" }
//                                            )
//                                            Text("XML")
//
//                                            Spacer(modifier = Modifier.width(16.dp))
//
//                                            RadioButton(
//                                                selected = responseFormat == "JSON",
//                                                onClick = { responseFormat = "JSON" }
//                                            )
//                                            Text("JSON")
//                            */
//                        }
//
//                        Button(
//                            onClick = {
//                                val request = ApiVendorRequest(
//                                    api_key = apiKey,
//                                    api_base_url = apiBaseUrl.ifBlank { null },
//                                    api_auth_method = "Bearer",
//                                    api_version = "b1",
//                                    vendor_integration_status = "Active",
//                                    response_format = responseFormat,
//                                    branches_ID = branchID
//                                )
//                                viewModel.addApiVendor(request)
//                            },
//                            enabled = !loading
//                        ) {
//                            Text(if (loading) "Saving..." else "Submit")
//                        }
//
//                        response?.let {
//                            Text("Response: ${it.message}", color = Color.Green)
//                        }
//
//                        error?.let {
//                            Text("Error: $it", color = Color.Red)
//                        }
//                    }
//                }
//
//                "METHODS" -> {
//                    // Placeholder for Methods
//                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        Text("Methods Screen (Coming Soon)")
//                    }
//                }
//
//                "MAPPING" -> {
//                    // Placeholder for Mapping
//                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        Text("Mapping Screen (Coming Soon)")
//                    }
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntegrationDetailScreen(
    apiVendorId: Int,
    branchId: Int,
    navController: NavHostController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val selectedMethods = remember { mutableStateListOf<MethodInputForApiVendor>() }
    val scrollState = rememberScrollState()

    // Load both saved methods and method templates
    LaunchedEffect(Unit) {
        viewModel.loadSavedMethods(apiVendorId)
        viewModel.loadMethodTemplates()
    }

    LaunchedEffect(viewModel.saveSuccess) {
        viewModel.saveSuccess?.let {
            viewModel.loadSavedMethods(apiVendorId)
            selectedMethods.clear() // Clear added methods if needed
        }
    }

    val savedMethods = viewModel.savedVendorMethods
    val isLoading = viewModel.isLoadingSavedMethods || viewModel.isLoadingMethod
    val error = viewModel.methodLoadError

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Method & Variables", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.pink))
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    navController.navigate("variable_mapping_screen/$branchId/$apiVendorId")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Variable")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
//            Button(
//                onClick = {
//                    navController.navigate("variable_mapping_screen/$branchId/$apiVendorId")
//                }
//            ) {
//                Text("Variable")
//            }

            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                error != null -> {
                    Text(
                        "Error loading data: $error",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                savedMethods.isNotEmpty() -> {
                    // Show existing saved methods
                    LazyColumn {
                        items(savedMethods) { method ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        Toast
                                            .makeText(
                                                context,
                                                "Clicked: ${method.method_name}",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    },
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Method Name: ${method.method_name}",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("HTTP Method: ${method.http_method}")
                                    Text("Endpoint: ${method.endpoint}")
                                    Text("Description: ${method.description}")
                                }
                            }
                        }
                    }
                }

                else -> {
                    // Show form to input new methods
                    Column(modifier = Modifier.verticalScroll(scrollState)) {
                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View Added Methods")
                        }

                        viewModel.methodTemplates.forEach { template ->
                            var endpoint by remember { mutableStateOf("") }
                            var description by remember { mutableStateOf("") }

                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Method: ${template.method_name} (${template.http_method})")

                                OutlinedTextField(
                                    value = endpoint,
                                    onValueChange = { endpoint = it },
                                    label = { Text("Endpoint") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    label = { Text("Description") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(onClick = {
                                    val existingIndex = selectedMethods.indexOfFirst {
                                        it.method_name == template.method_name
                                    }
                                    val newMethod = MethodInputForApiVendor(
                                        method_name = template.method_name,
                                        http_method = template.http_method,
                                        endpoint = endpoint,
                                        description = description
                                    )
                                    if (existingIndex != -1) {
                                        selectedMethods[existingIndex] = newMethod
                                    } else {
                                        selectedMethods.add(newMethod)
                                    }
                                }) {
                                    Text("Add")
                                }

                                Divider()
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.saveApiMethods(apiVendorId, selectedMethods)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save All Methods")
                        }

                        viewModel.saveSuccess?.let {
                            Text("✅ $it", color = Color.Green)
                        }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Close")
                        }
                    },
                    title = { Text("Added Methods") },
                    text = {
                        if (selectedMethods.isEmpty()) {
                            Text("No methods added yet.")
                        } else {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                selectedMethods.forEachIndexed { index, method ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                "Method ${index + 1}",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text("Method Name: ${method.method_name}")
                                            Text("HTTP Method: ${method.http_method}")
                                            Text("Endpoint: ${method.endpoint}")
                                            Text("Description: ${method.description}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MappingScreen(
//    branchId: Int,
//    vendorId: Int,
//    navController: NavHostController,
//    viewModel: AdminViewModel = hiltViewModel()
//) {
//    val mappings by remember { derivedStateOf { viewModel.mappings } }
//    val variables by remember { derivedStateOf { viewModel.variables } }
//    val errorMessage = viewModel.errorMessageMapping
//    var mappingInputs by remember { mutableStateOf(mapOf<Int, String>()) }
//    val isMappingsLoaded by remember { derivedStateOf { viewModel.isMappingsLoaded } }
//    val isVariablesLoaded by remember { derivedStateOf { viewModel.isVariablesLoaded } }
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchMappings(branchId, vendorId)
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Variable Mapping", color = Color.White) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            Icons.Default.ArrowBack,
//                            contentDescription = "Back",
//                            tint = Color.White
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.pink))
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .padding(16.dp)
//        ) {
//
//            when {
//                isMappingsLoaded && mappings.isNotEmpty() -> {
//                    Text("Existing Mappings", style = MaterialTheme.typography.titleMedium)
//                    Spacer(Modifier.height(8.dp))
//                    mappings.forEach {
//                        Text("Variable ID: ${it.variable_ID} → API Value: ${it.api_values}")
//                        Spacer(Modifier.height(4.dp))
//                    }
//                }
//
//                isMappingsLoaded && isVariablesLoaded && mappings.isEmpty() && variables.isNotEmpty() -> {
//                    Text("Provide Mappings", style = MaterialTheme.typography.titleMedium)
//                    Spacer(Modifier.height(8.dp))
//
//                    variables.forEach { variable ->
//                        OutlinedTextField(
//                            value = mappingInputs[variable.id] ?: "",
//                            onValueChange = {
//                                mappingInputs = mappingInputs.toMutableMap().apply {
//                                    put(variable.id, it)
//                                }
//                            },
//                            label = { Text(variable.tags) },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 4.dp)
//                        )
//                    }
//
//                    Spacer(Modifier.height(16.dp))
//                    Button(
//                        onClick = {
//                            val inputs = mappingInputs.mapNotNull { (variableId, value) ->
//                                if (value.isNotBlank()) MappingInput(variableId, value) else null
//                            }
//                            viewModel.saveMappings(branchId, vendorId, inputs) {
//                                viewModel.fetchMappings(branchId, vendorId) // Refresh mappings
//                            }
//                        },
//                        modifier = Modifier.align(Alignment.End)
//                    ) {
//                        Text("Save Mappings")
//                    }
//                }
//
//                errorMessage != null -> {
//                    Text("Error: $errorMessage", color = Color.Red)
//                }
//
//                else -> {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//                }
//            }
//
//
//        }
//    }
//}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MappingScreen(
    branchId: Int,
    vendorId: Int,
    navController: NavHostController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val mappings by remember { derivedStateOf { viewModel.mappings } }
    val variables by remember { derivedStateOf { viewModel.variables } }
    val errorMessage = viewModel.errorMessageMapping
    var mappingInputs by remember { mutableStateOf(mapOf<Int, String>()) }
    val isMappingsLoaded by remember { derivedStateOf { viewModel.isMappingsLoaded } }
    val isVariablesLoaded by remember { derivedStateOf { viewModel.isVariablesLoaded } }

    LaunchedEffect(Unit) {
        Log.d("MappingScreen", "Fetching mappings for branchId=$branchId, vendorId=$vendorId")
        viewModel.fetchMappings(branchId, vendorId)
        viewModel.fetchVariables()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Variable Mapping", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.pink))
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            when {
                isMappingsLoaded && mappings.isNotEmpty() -> {
//                    Text("Existing Mappings", style = MaterialTheme.typography.titleMedium)
//                    Spacer(Modifier.height(8.dp))
//                    mappings.forEach {
//                        Text("Variable ID: ${it.variable_ID} → API Value: ${it.api_values}")
//                        Spacer(Modifier.height(4.dp))
//                    }
                    Text("Existing Mappings", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    mappings.forEach {
                        val variableTag = variables.find { v -> v.id == it.variable_ID }?.tags ?: "Unknown"
                        Text("$variableTag: ${it.api_values}")
                        Spacer(Modifier.height(4.dp))
                    }
                }

                isMappingsLoaded && isVariablesLoaded && mappings.isEmpty() && variables.isNotEmpty() -> {
                    Text("Provide Mappings", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    variables.forEach { variable ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "${variable.tags}:",
                                modifier = Modifier.width(130.dp),
                                maxLines = 1
                            )
                            TextField(
                                value = mappingInputs[variable.id] ?: "",
                                onValueChange = {
                                    mappingInputs = mappingInputs.toMutableMap().apply {
                                        put(variable.id, it)
                                    }
                                    Log.d("MappingInput", "Variable ${variable.id} (${variable.tags}) = $it")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp), // reduced height
                                singleLine = true,
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.Transparent
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val inputs = mappingInputs.mapNotNull { (variableId, value) ->
                                if (value.isNotBlank()) MappingInput(variableId, value) else null
                            }
                            Log.d("SaveMappings", "Saving ${inputs.size} mappings for branchId=$branchId vendorId=$vendorId")
                            viewModel.saveMappings(branchId, vendorId, inputs) {
                                viewModel.fetchMappings(branchId, vendorId) // Refresh
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save Mappings")
                    }
                }

                errorMessage != null -> {
                    Text("Error: $errorMessage", color = Color.Red)
                }

                else -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }

        }
    }
}




