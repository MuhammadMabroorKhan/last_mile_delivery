package com.example.lastmiledelivery.viewmodels.deliveryboy

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lastmiledelivery.data.models.customer.CustomerData
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyDataResponse
import com.example.lastmiledelivery.data.repository.deliveryboy.DeliveryBoyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryBoyViewModel @Inject constructor(
    private val repository: DeliveryBoyRepository,
    private val context: Application
) : ViewModel() {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var deliveryBoyState by mutableStateOf<DeliveryBoyDataResponse?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun getDeliveryBoyData(id: Int) {
        viewModelScope.launch {
            try {
                val deliveryboy = repository.getDeliveryBoyData(id)
                if (deliveryboy != null) {
                    deliveryBoyState = deliveryboy
                    with(sharedPreferences.edit()) {
                        putInt("deliveryBoy_ID", deliveryBoyState!!.delivery_boy_id)
                        apply()
                    }
                } else {
                    errorMessage = "Delivery Boy Not Found"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
            }
        }
    }

    fun getDeliveryBoyID():Int?{
        return sharedPreferences.getInt("deliveryBoy_ID",-1)
    }
}

