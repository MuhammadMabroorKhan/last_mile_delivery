package com.example.lastmiledelivery.viewmodels.deliveryboy

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lastmiledelivery.data.models.customer.CustomerData
import com.example.lastmiledelivery.data.models.deliveryboy.AcceptOrderResponse
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyDataResponse
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyToggleResponse
import com.example.lastmiledelivery.data.models.deliveryboy.ReadySuborder
import com.example.lastmiledelivery.data.repository.deliveryboy.DeliveryBoyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
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

    fun getDeliveryBoyID(): Int? {
        return sharedPreferences.getInt("deliveryBoy_ID", -1)
    }

    var deliveryBoyStatusState by mutableStateOf<DeliveryBoyToggleResponse?>(null)
        private set

    var errorMessageStatus by mutableStateOf<String?>(null)
        private set

    fun deliveryBoyToggleStatus(id: Int) {
        viewModelScope.launch {

            try {
                val response = repository.deliveryBoyToggleStatus(id)
                if (response?.deliveryBoyONOFF != null) {
                    deliveryBoyStatusState = response
                    getDeliveryBoyData(id)
                    errorMessageStatus = null
                } else {
                    errorMessageStatus = response?.message ?: "Unknown error"
                }
            } catch (e: Exception) {
                errorMessageStatus = e.localizedMessage ?: "APi Exception"
            }

        }
    }


    var readySuborders by mutableStateOf<List<ReadySuborder>>(emptyList())
        private set

    var isLoadingReadySuborders by mutableStateOf(false)
    var errorMessageReadySuborders by mutableStateOf<String?>(null)

    fun fetchReadySuborders(deliveryBoyId: Int) {
        viewModelScope.launch {
            isLoadingReadySuborders = true
            val result = repository.getReadySubordersForDeliveryBoy(deliveryBoyId)
            isLoadingReadySuborders = false

            result.onSuccess {
                readySuborders = it
            }.onFailure {
                errorMessageReadySuborders = it.localizedMessage
            }
        }
    }


    private val _acceptOrderResponse = mutableStateOf<AcceptOrderResponse?>(null)
    val acceptOrderResponse: State<AcceptOrderResponse?> = _acceptOrderResponse

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    fun acceptOrder(deliveryBoyId: Int, suborderId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.acceptOrder(deliveryBoyId, suborderId)
                if (response.isSuccessful) {
                    _acceptOrderResponse.value = response.body()
                } else {
                    _acceptOrderResponse.value =
                        AcceptOrderResponse(error = "Failed to accept order.")
                }
            } catch (e: Exception) {
                _acceptOrderResponse.value =
                    AcceptOrderResponse(error = "Network error: ${e.message}")
            } finally {
                _loading.value = false
            }
        }

    }

    fun clearAcceptOrderResponse() {
        _acceptOrderResponse.value = null
    }


    private var autoRefreshJob: Job? = null

    fun startAutoRefreshReadyOrders(deliveryBoyId: Int, intervalMillis: Long = 5000L) {
        if (autoRefreshJob?.isActive == true) return // Prevent multiple jobs

        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                fetchReadySuborders(deliveryBoyId)
                delay(intervalMillis)
            }
        }
    }

    fun stopAutoRefreshReadyOrders() {
        autoRefreshJob?.cancel()
    }

}

