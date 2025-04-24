package com.example.lastmiledelivery.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lastmiledelivery.data.models.Cities
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdown(
    cities: List<Cities>,
    selectedCity: Cities?,
    onCitySelected: (Cities) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedCity?.name ?: "Select a City",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city.name) },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    }
                )
            }
        }
    }
}


//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//
//    var redColor = remember { mutableStateOf(true) }
//    var yellowColor = remember { mutableStateOf(false) }
//    var greenColor = remember { mutableStateOf(false) }
//    var color = remember { mutableStateOf(Color.Gray) }
//
//    var isStart by remember { mutableStateOf(true) }
//    var switch by remember { mutableStateOf("OFF") }
//
//    LaunchedEffect(Unit) {
//        while (isStart) {
//            redColor.value = true
//            yellowColor.value = false
//            greenColor.value = false
//            delay(2000)
//
//            yellowColor.value = true
//            redColor.value = false
//            greenColor.value = false
//            delay(1000)
//
//            yellowColor.value = false
//            redColor.value = false
//            greenColor.value = true
//            delay(2000)
//        }
//    }
//    Box(
//        modifier = Modifier
//            .padding(10.dp)
//            .fillMaxSize()
//            .background(color = Color.Black)
//    ) {
//
//        Row(
//            modifier = Modifier
//                .padding(20.dp)
//                .align(Alignment.TopCenter)
//        ) {
//            Text(
//                text = "$name!",
//                modifier = modifier,
//                color = Color.White,
//                fontSize = 26.sp,
//                fontWeight = FontWeight.ExtraBold
//            )
//        }
//
//        Row(
//            modifier = Modifier
//                .padding(20.dp)
//                .align(Alignment.Center)
//        ) {
//            Column(
//                modifier = Modifier
//                    .padding(30.dp)
////                .background(color = Color.Black)
//                    .height(250.dp)
//                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
//
//            ) {
//
//
//                Row {
//                    Box(
//                        modifier = Modifier
//                            .height(50.dp)
//                            .width(50.dp)
//                            .background(color = if (redColor.value) Color.Red else color.value)
//                    )
//                }
//                Row {
//                    Box(
//                        modifier = Modifier
//                            .height(50.dp)
//                            .width(50.dp)
//                            .background(color = if (yellowColor.value) Color.Yellow else color.value)
//                    )
//                }
//                Row {
//                    Box(
//                        modifier = Modifier
//                            .height(50.dp)
//                            .width(50.dp)
//                            .background(color = if (greenColor.value) Color.Green else color.value)
//                    )
//                }
//
//            }
//
//        }
//        Button(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(40.dp)
//                .width(100.dp)
//                .height(60.dp),
//            onClick = {
//                if (isStart) {
//                    isStart = false
//                    switch = "ON"
//                }
//            },
//            shape = RectangleShape,
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color.White,
//                contentColor = Color.Black
//            )
//        ) {
//            Text(switch)
//        }
//    }
//
//
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//
//    Greeting("Traffic Lights")
//
//}