package com.example.lastmiledelivery.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.lastmiledelivery.viewmodels.admin.LmdViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LmdSettingsScreen(navController: NavHostController, viewModel: LmdViewModel = hiltViewModel()) {
    val settings = viewModel.settings

    LaunchedEffect(Unit) {
        viewModel.fetchSettings()
    }

    AdminScaffold(navController, title = "Lmd Setting") { // ✅ Pass title

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            settings?.let {
                SettingRow("Order Charge (%)", it.order_charge) { viewModel.updateOrderCharge(it) }
                SettingRow("Tax (%)", it.tax_percentage) { viewModel.updateTaxPercentage(it) }
                SettingRow("Pickup Radius (km)", it.pickup_radius_km) { viewModel.updatePickupRadius(it) }
            } ?: Text("Loading...", modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun SettingRow(label: String, value: Double, onUpdate: (Double) -> Unit) {
    var text by remember { mutableStateOf(value.toString()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            label,
            modifier = Modifier.weight(1f)
        )

        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .width(100.dp)
                .padding(end = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(onClick = {
            val updated = text.toDoubleOrNull()
            if (updated != null) onUpdate(updated)
        }) {
            Text("Save")
        }
    }
}

