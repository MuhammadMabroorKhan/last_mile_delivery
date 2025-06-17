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
import com.example.lastmiledelivery.data.models.deliveryboy.AssignedSuborder
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyDataResponse
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyToggleResponse
import com.example.lastmiledelivery.data.models.deliveryboy.LatestLocationResponse
import com.example.lastmiledelivery.data.models.deliveryboy.ReachDestinationResponse
import com.example.lastmiledelivery.data.models.deliveryboy.ReadySuborder
import com.example.lastmiledelivery.data.models.deliveryboy.Vehicle
import com.example.lastmiledelivery.data.models.deliveryboy.VehicleCategory
import com.example.lastmiledelivery.data.models.deliveryboy.VehicleRequest
import com.example.lastmiledelivery.data.repository.deliveryboy.DeliveryBoyRepository
import com.example.lastmiledelivery.ui.deliveryboy.interpolateLatLng
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun startAutoRefreshReadyOrders(deliveryBoyId: Int, intervalMillis: Long = 15000L) {
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


    var assignedOrders by mutableStateOf<List<AssignedSuborder>>(emptyList())
        private set

    var isLoadingassignedOrders by mutableStateOf(false)
    var errorMessageassignedOrders by mutableStateOf<String?>(null)

    fun fetchAssignedSuborders(deliveryBoyId: Int) {
        viewModelScope.launch {
            isLoadingassignedOrders = true
            errorMessageassignedOrders = null

            val response = repository.getAssignedSuborders(deliveryBoyId)
            if (response?.status == "success") {
                assignedOrders = response.data ?: emptyList()
            } else {
                errorMessageassignedOrders = "Failed to load orders"
            }
            isLoadingassignedOrders = false
        }
    }


    var selectedSuborderPayment by mutableStateOf<AssignedSuborder?>(null)
        private set

//    fun fetchSingleAssignedSuborder(deliveryBoyId: Int, suborderId: Int) {
//        viewModelScope.launch {
//            isLoadingassignedOrders = true
//            val response = repository.getAssignedSuborders(deliveryBoyId)
//            if (response?.status == "success") {
//                selectedSuborderPayment = response.data?.find { it.suborder_id == suborderId }
//            } else {
//                selectedSuborderPayment = null
//            }
//            isLoadingassignedOrders = false
//        }
//    }
fun fetchSingleAssignedSuborder(deliveryBoyId: Int, suborderId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
        val response = repository.getAssignedSuborders(deliveryBoyId)

        withContext(Dispatchers.Main) {
            if (response?.status == "success") {
                selectedSuborderPayment = response.data?.find { it.suborder_id == suborderId }
            } else {
                selectedSuborderPayment = null
            }
            isLoadingassignedOrders = false
        }
    }
}

    var pickupResponse by mutableStateOf<String?>(null)
    var pickupError by mutableStateOf<String?>(null)
    var isLoadingpickupResponse by mutableStateOf(false)

    fun confirmPickup(suborderId: Int, lat: Double, lng: Double) {
        viewModelScope.launch {
            isLoadingpickupResponse = true
            val result = repository.confirmPickup(suborderId, lat, lng)
            isLoadingpickupResponse = false
            result.onSuccess {
                pickupResponse = it
            }.onFailure {
                pickupError = it.message
            }
        }
    }


    private var locationJob: Job? = null

    fun startLocationUpdates(
        suborderId: Int,
        orderStatus: String,
        getCurrentLocation: suspend () -> Pair<Double, Double>
    ) {
        if (orderStatus.equals("handover_confirmed", true) || orderStatus.equals(
                "in_transit",
                true
            )
        ) {
            locationJob?.cancel()
            locationJob = viewModelScope.launch {
                while (isActive) {
                    val (lat, lng) = getCurrentLocation()
                    repository.updateLocation(suborderId, lat, lng)
                    delay(10000) // 10 seconds
                }
            }
        }
    }

    fun stopLocationUpdates() {
        locationJob?.cancel()
    }


    //For SHowing SImulation
    fun startSimulatedLocationUpdates(
        suborderId: Int,
        pickupLatLng: LatLng,
        dropLatLng: LatLng
    ) {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            val steps = 20 // total updates before reaching destination
            for (i in 0..steps) {
                val fraction = i / steps.toFloat()
                val simulatedLatLng = interpolateLatLng(pickupLatLng, dropLatLng, fraction)
                repository.updateLocation(suborderId, simulatedLatLng.latitude, simulatedLatLng.longitude)
                delay(3000) // 3 seconds delay to simulate movement
            }
        }
    }



    fun sendLocationToBackend(suborderId: Int, latLng: LatLng) {
        viewModelScope.launch {
            repository.updateLocation(suborderId, latLng.latitude, latLng.longitude)
        }
    }


    var isLoadingDestination by mutableStateOf(false)
    var reachDestinationResponse by mutableStateOf<ReachDestinationResponse?>(null)
    var destinationError by mutableStateOf<String?>(null)

    fun reachDestination(deliveryBoyId: Int, suborderId: Int, lat: Double, lng: Double) {
        viewModelScope.launch {
            isLoadingDestination = true
            val result = repository.reachDestination(deliveryBoyId, suborderId, lat, lng)
            reachDestinationResponse = result
            destinationError = result?.error
            isLoadingDestination = false
        }
    }


    var latestLocation by mutableStateOf<LatestLocationResponse?>(null)
        private set

    var latestLocationError by mutableStateOf<String?>(null)
        private set

    fun getLatestLocation(suborderId: Int) {
        viewModelScope.launch {
            val result = repository.fetchLatestLocation(suborderId)
            result.onSuccess {
                latestLocation = it
                latestLocationError = null
            }.onFailure {
                latestLocation = null
                latestLocationError = it.message
            }
        }
    }


    var isLoadingPaymentConfirm by mutableStateOf(false)
    var paymentConfirmResponse by mutableStateOf<String?>(null)
    var paymentConfirmError by mutableStateOf<String?>(null)

    fun confirmPaymentByDeliveryBoy(suborderId: Int) {
        viewModelScope.launch {
            isLoadingPaymentConfirm = true
            paymentConfirmResponse = null
            paymentConfirmError = null

            val result = repository.confirmPaymentByDeliveryBoy(suborderId)
            result.onSuccess {
                paymentConfirmResponse = it
            }.onFailure {
                paymentConfirmError = it.message
            }
            isLoadingPaymentConfirm = false
        }
    }



    var vehiclesState by mutableStateOf<List<Vehicle>?>(null)
//    var vehicleCategories by mutableStateOf<List<VehicleCategory>>(emptyList())
var vehicleCategories by mutableStateOf<List<VehicleCategory>>(emptyList())
    private set
    var errorMessageVehicle by mutableStateOf<String?>(null)

    fun loadVehicles(deliveryBoyId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getVehicles(deliveryBoyId)
                if (response.isSuccessful && response.body()?.status == true) {
                    vehiclesState = response.body()?.data?.vehicles
                } else {
                    vehiclesState = emptyList()
                    loadVehicleCategories()
                }
            } catch (e: Exception) {
                errorMessageVehicle = e.localizedMessage
            }
        }
    }

//    fun loadVehicleCategories() {
//        viewModelScope.launch {
//            val res = repository.getVehicleCategories()
//            if (res.isSuccessful && res.body()?.status == true) {
//                vehicleCategories = res.body()?.data ?: emptyList()
//            }
//        }
//    }
fun loadVehicleCategories() {
    viewModelScope.launch {
        val res = repository.getVehicleCategories()
        if (res.isSuccessful && res.body()?.status == true) {
            val list = res.body()?.data ?: emptyList()
            Log.d("DEBUG_VM", "Categories fetched: ${list.size}")
            vehicleCategories = list
        } else {
            Log.e("DEBUG_VM", "Failed to load categories")
        }
    }
}


    fun addVehicle(deliveryBoyId: Int, vehicle: VehicleRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val res = repository.addVehicle(deliveryBoyId, vehicle)
            if (res.isSuccessful) {
                onSuccess()
                loadVehicles(deliveryBoyId)
            }
        }
    }



}

