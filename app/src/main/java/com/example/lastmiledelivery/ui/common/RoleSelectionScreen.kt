package com.example.lastmiledelivery.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun RoleSelectionScreen(navController: NavController) {
    var selectedRole by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Choose Your Role", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        val roles = listOf("Customer", "Vendor", "Delivery Boy")

        roles.forEach { role ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedRole = role }
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = selectedRole == role,
                    onClick = { selectedRole = role }
                )
                Text(text = role, modifier = Modifier.padding(start = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                selectedRole?.let { role ->
                    when (role) {
                        "Customer" -> navController.navigate("customer_signup")
                        "Vendor" -> navController.navigate("vendor_signup")
                        "Delivery Boy" -> navController.navigate("deliveryboy_signup")
                    }
                }
            },
            enabled = selectedRole != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Next")
        }
    }
}
