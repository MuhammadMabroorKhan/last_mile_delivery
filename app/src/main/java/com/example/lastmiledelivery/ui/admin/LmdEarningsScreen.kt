package com.example.lastmiledelivery.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.lastmiledelivery.viewmodels.admin.LmdViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.ui.input.pointer.pointerInput


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LmdEarningsScreen(navController: NavHostController, viewModel: LmdViewModel = hiltViewModel()) {
    val earnings = viewModel.earnings
    val totalEarning = viewModel.totalEarningAfterTax
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()

    val filteredEarnings = remember(earnings, startDate, endDate) {
        earnings.filter {
            val created = LocalDateTime.parse(it.created_at, DateTimeFormatter.ISO_DATE_TIME).toLocalDate()
            val afterStart = startDate?.let { created >= it } ?: true
            val beforeEnd = endDate?.let { created <= it } ?: true
            afterStart && beforeEnd
        }
    }


    val totalFiltered = filteredEarnings.sumOf {
        it.lmd_earning_amount - it.tax_amount
    }

    LaunchedEffect(Unit) {
        viewModel.fetchEarnings()
    }

    AdminScaffold(navController, title = "LMD Earnings") {
        Column(modifier = Modifier.fillMaxSize()) {
            FilterSection(
                startDate = startDate,
                endDate = endDate,
                onStartChange = { viewModel.setStartDate(it) },
                onEndChange = { viewModel.setEndDate(it) },
                onClear = { viewModel.clearDates() }
            )

            Divider()

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(filteredEarnings) { earning ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("Suborder #${earning.suborder_id} - Rs. ${earning.lmd_earning_amount} (Tax: ${earning.tax_amount})")
                        val dateTime = try {
                            LocalDateTime.parse(earning.created_at, DateTimeFormatter.ISO_DATE_TIME)
                        } catch (e: Exception) {
                            null
                        }

                        if (dateTime != null) {
                            val dateOnly = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            val timeOnly = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                            Text("Date: $dateOnly", style = MaterialTheme.typography.bodySmall)
//                            Text("Time: $timeOnly", style = MaterialTheme.typography.bodySmall)
                        } else {
                            Text("Date: ${earning.created_at}", style = MaterialTheme.typography.bodySmall)
                        }

                    }
                }
            }

            Divider()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Total Earnings: Rs. ${if (startDate != null && endDate != null) totalFiltered else totalEarning}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    startDate: LocalDate?,
    endDate: LocalDate?,
    onStartChange: (LocalDate) -> Unit,
    onEndChange: (LocalDate) -> Unit,
    onClear: () -> Unit
) {
    val showStartDialog = remember { mutableStateOf(false) }
    val showEndDialog = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Clear",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onClear() }
                    .padding(end = 4.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DatePickerField("Start Date", startDate, onClick = { showStartDialog.value = true }, modifier = Modifier.weight(1f))
            DatePickerField("End Date", endDate, onClick = { showEndDialog.value = true }, modifier = Modifier.weight(1f))

        }
    }

    if (showStartDialog.value) {
//        val state = rememberDatePickerState(
//            initialSelectedDateMillis = startDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
//        )
        val state = rememberDatePickerState(
            initialSelectedDateMillis = startDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val today = LocalDate.now()
                    val selectedDate = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    return !selectedDate.isAfter(today)
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showStartDialog.value = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onStartChange(date)
                    }
                    showStartDialog.value = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDialog.value = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }

    if (showEndDialog.value) {
//        val state = rememberDatePickerState(
//            initialSelectedDateMillis = endDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
//        )
        val state = rememberDatePickerState(
            initialSelectedDateMillis = endDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val selected = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    val today = LocalDate.now()

                    // ✅ Only allow date if it is: >= startDate AND <= today
                    return (startDate == null || !selected.isBefore(startDate)) && !selected.isAfter(today)
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showEndDialog.value = false }, // ✅ fix: correct dismiss handler
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onEndChange(date) // ✅ fix: call correct end-date handler
                    }
                    showEndDialog.value = false // ✅ fix: close correct dialog
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDialog.value = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }

}



@Composable
private fun DatePickerField(
    label: String,
    value: LocalDate?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateText = value?.format(formatter) ?: ""

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        OutlinedTextField(
            value = dateText,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = "Calendar")
            },
            modifier = Modifier
                .fillMaxWidth()
                // Disable text field interactions
                .pointerInput(Unit) {},
            enabled = false
        )
    }
}










//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LmdEarningsScreen(navController: NavHostController, viewModel: LmdViewModel = hiltViewModel()) {
//    val earnings = viewModel.earnings
//    val totalEarning = viewModel.totalEarningAfterTax
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchEarnings()
//    }
//    AdminScaffold(navController, title = "Lmd Earning") { // ✅ Pass title
//
//        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//            Text(
//                "LMD Earnings (After Tax): Rs. $totalEarning",
//                style = MaterialTheme.typography.titleLarge
//            )
//
//            LazyColumn {
//                items(earnings) { earning ->
//                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
//                        Text("Suborder #${earning.suborder_id} - Earned: Rs. ${earning.lmd_earning_amount} (Tax: Rs. ${earning.tax_amount})")
//                        Text(
//                            "Date: ${earning.created_at}",
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
