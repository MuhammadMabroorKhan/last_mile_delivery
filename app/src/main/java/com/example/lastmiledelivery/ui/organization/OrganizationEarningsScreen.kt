package com.example.lastmiledelivery.ui.organization

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.lastmiledelivery.R
import com.example.lastmiledelivery.viewmodels.organization.OrgEarningsUiState
import com.example.lastmiledelivery.viewmodels.organization.OrganizationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationEarningsScreen(
    orgId: Int,
    navController: NavHostController,
    viewModel: OrganizationViewModel = hiltViewModel()
) {
    val state = viewModel.orgEarningsState

    LaunchedEffect(Unit) {
        viewModel.loadOrganizationEarnings(orgId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Organization Earnings", color = Color.White) },
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

            when (state) {
                is OrgEarningsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }

                is OrgEarningsUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is OrgEarningsUiState.Success -> {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Total Earnings: Rs ${state.data.total_earnings}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn {
                            val earningsList = state.data.earnings ?: emptyList()
                            items(earningsList) { earning ->
                            Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Suborder ID: ${earning.suborder_id}")
                                        Text("Delivery Boy ID: ${earning.deliveryboy_id}")
                                        Text("Distance: ${earning.distance_km} km")
                                        Text("Rate/km: Rs ${earning.rate_per_km}")
                                        Text("Total: Rs ${earning.total_earning}")
                                        Text("Date: ${earning.created_at}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}