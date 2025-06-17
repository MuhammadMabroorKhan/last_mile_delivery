package com.example.lastmiledelivery.ui.deliveryboy

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.data.models.deliveryboy.VehicleCategory
import com.example.lastmiledelivery.data.models.deliveryboy.VehicleRequest
import com.example.lastmiledelivery.viewmodels.deliveryboy.DeliveryBoyViewModel
import androidx.compose.ui.zIndex
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.colorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleScreen(deliveryBoyId: Int, navController: NavController,viewModel: DeliveryBoyViewModel = hiltViewModel()) {
    val vehicles = viewModel.vehiclesState
    val categories = viewModel.vehicleCategories

    LaunchedEffect(Unit) {
        viewModel.loadVehicles(deliveryBoyId)
        viewModel.loadVehicleCategories() // ✅ Make sure you load them
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vehicle", color = Color.White) },
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
    if (vehicles == null) {
        CircularProgressIndicator()
    } else if (vehicles.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vehicles) { vehicle ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFDECEF)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = vehicle.category_name.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = colorResource(id = R.color.pink)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Plate No: ${vehicle.plate_no}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Model: ${vehicle.model}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Color: ${vehicle.color}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Per KM Charges: ${vehicle.per_km_charge} Rs/km",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }

    } else {
        Text("Categories loaded: ${categories.size}")

        // Show form to add vehicle
        var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
        var plateNo by remember { mutableStateOf("") }
        var color by remember { mutableStateOf("") }
        var model by remember { mutableStateOf("") }

        Column(Modifier.padding(16.dp)) {
            Text("Add New Vehicle", style = MaterialTheme.typography.titleMedium)



            OutlinedTextField(value = plateNo, onValueChange = { plateNo = it }, label = { Text("Plate No") })
            OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") })
            OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Model") })

//            DropdownMenuBox("Select Vehicle Category", categories, selectedCategoryId) {
//                selectedCategoryId = it
//            }
            VehicleCategoryDropdown("Select Vehicle Category", categories, selectedCategoryId) {
                selectedCategoryId = it
            }

            Button(
                onClick = {
                    if (selectedCategoryId != null) {
                        viewModel.addVehicle(
                            deliveryBoyId,
                            VehicleRequest(
                                plate_no = plateNo,
                                color = color,
                                model = model,
                                vehicle_type = selectedCategoryId!!
                            )
                        ) {
                            // ✅ Refresh after vehicle is added
                            viewModel.loadVehicles(deliveryBoyId)

                            // Optionally clear form fields
                            plateNo = ""
                            color = ""
                            model = ""
                            selectedCategoryId = null

                        }
                    }
                },
                enabled = selectedCategoryId != null && plateNo.isNotBlank()
            ) {
                Text("Submit Vehicle")
            }
        }
    }
}
}
    }

//@Composable
//private fun DropdownMenuBox(
//    label: String,
//    categories: List<VehicleCategory>,
//    selectedId: Int?,
//    onSelect: (Int) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//    val selected = categories.find { it.id == selectedId }
//
//    Box (modifier = Modifier.zIndex(1f)){
//        OutlinedTextField(
//            value = selected?.name ?: "",
//            onValueChange = {},
//            label = { Text(label) },
//            readOnly = true,
//            modifier = Modifier.fillMaxWidth().clickable { expanded = true }
//        )
//
//        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//            categories.forEach { cat ->
//                DropdownMenuItem(
//                    text = { Text(cat.name) },
//                    onClick = {
//                        onSelect(cat.id)
//                        expanded = false
//                    }
//                )
//            }
//        }
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleCategoryDropdown(
    label: String,
    categories: List<VehicleCategory>,
    selectedId: Int?,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = categories.find { it.id == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selected?.name ?: "",
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor() // Important for correct positioning
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onSelect(category.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
