package com.example.lastmiledelivery.viewmodels.vendor

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.vendor.AvailableOrganization
import com.example.lastmiledelivery.data.models.vendor.RequestedOrganization
import com.example.lastmiledelivery.data.models.vendor.SuborderStatusUpdateResponse
import com.example.lastmiledelivery.data.models.vendor.VendorOrder
import com.example.lastmiledelivery.data.models.vendor.VendorOrderDetailInfo
import com.example.lastmiledelivery.data.models.vendor.VendorResponse
import com.example.lastmiledelivery.data.models.vendor.VendorSignupResponse
import com.example.lastmiledelivery.data.models.vendor.VendorSuborderDetailInfo
import com.example.lastmiledelivery.data.models.vendor.VendorSuborderDetailResponse
import com.example.lastmiledelivery.data.models.vendor.VendorSummaryResponse
import com.example.lastmiledelivery.data.repository.vendor.VendorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel   //private val context: Application
class VendorViewModel @Inject constructor(
    private val repository: VendorRepository,
    private val context: Application
) : ViewModel() {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)


    private val _signupState = MutableLiveData<Result<VendorSignupResponse>?>()
    val signupState: MutableLiveData<Result<VendorSignupResponse>?> get() = _signupState

    fun vendorSignup(
        name: RequestBody,
        email: RequestBody,
        phoneNo: RequestBody,
        password: RequestBody,
        cnic: RequestBody,
        profilePicture: MultipartBody.Part?,
        vendorType: RequestBody,
        addressType: RequestBody,
        street: RequestBody,
        city: RequestBody,
        zipCode: RequestBody?,
        country: RequestBody?,
        latitude: RequestBody?,
        longitude: RequestBody?
    ) {
        viewModelScope.launch {
            val result = repository.vendorSignup(
                name, email, phoneNo, password, cnic, profilePicture,
                vendorType, addressType, street, city, zipCode, country, latitude, longitude
            )
            _signupState.value = result
        }
    }

    fun clearSignupState() {
        _signupState.value = null  // Reset the state
    }


    private val _vendorData = MutableLiveData<Result<VendorResponse>?>()
    val vendorData: MutableLiveData<Result<VendorResponse>?> get() = _vendorData

    private val _isLoading = MutableLiveData(true)  // Loading state
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getVendorData(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true  // Start loading
            val result = repository.getVendorData(id)
            Log.d("RecievedIDHEREViewModel", " ${id}")
            _vendorData.value = result
            _isLoading.value = false  // Stop loading
        }
    }


    fun clearVendorState() {
        _vendorData.value = null  // Reset the state
    }

    private val _vendorId = MutableLiveData<Int?>()
    val vendorId: LiveData<Int?> = _vendorId

    fun setVendorId(id: Int) {
        _vendorId.value = id
        with(sharedPreferences.edit()) {
            putInt("vendor_id", id)
            apply()
        }
        Log.d("viewmodel_VendorID", "$id")
    }

    fun getVendorId(): Int? {
        return sharedPreferences.getInt("vendor_id", -1)
    }


    private val _orders = mutableStateOf<List<VendorOrder>>(emptyList())
    val orders: State<List<VendorOrder>> = _orders

    private val _isLoadingOrder = mutableStateOf(false)
    val isLoadingOrder: State<Boolean> = _isLoadingOrder

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun loadVendorOrders(vendorId: Int) {
        viewModelScope.launch {
            _isLoadingOrder.value = true
            val result = repository.getVendorOrders(vendorId)
            _orders.value = result?.orders ?: emptyList()
            _error.value = result?.message
            _isLoadingOrder.value = false
        }
    }

    // State for suborder and order details
    private val _suborderDetails = MutableStateFlow<VendorSuborderDetailInfo?>(null)
    val suborderDetails: StateFlow<VendorSuborderDetailInfo?> = _suborderDetails

    private val _orderDetails = MutableStateFlow<List<VendorOrderDetailInfo>?>(null)
    val orderDetails: StateFlow<List<VendorOrderDetailInfo>?> = _orderDetails

    var isLoadingSuborderDetails = mutableStateOf(false)
    var errors = mutableStateOf<String?>(null)

    fun loadSuborderDetails(vendorId: Int, shopId: Int, branchId: Int, suborderId: Int) {
        isLoadingSuborderDetails.value = true
        errors.value = null

        // Call the repository to load suborder details
        viewModelScope.launch {
            try {
                // Fetch the details and extract the response
                val response = repository.getSuborderDetails(vendorId, shopId, branchId, suborderId)

                if (response != null) {
                    // Set suborder_info and order_detail_info
                    _suborderDetails.value = response.suborder_info
                    _orderDetails.value = response.order_detail_info
                } else {
                    errors.value = "No suborder details found"
                }

                isLoadingSuborderDetails.value = false
            } catch (e: Exception) {
                errors.value = e.message
                isLoadingSuborderDetails.value = false
            }
        }
    }


    var availableOrganizations by mutableStateOf<List<AvailableOrganization>>(emptyList())
        private set

    var requestedOrganizations by mutableStateOf<List<RequestedOrganization>>(emptyList())
        private set

    var isLoadingOrganization by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadOrganizations(vendorId: Int) {
        viewModelScope.launch {
            isLoadingOrganization = true
            errorMessage = null
            try {
                val result = repository.fetchOrganizations(vendorId)
                availableOrganizations = result?.availableOrganizations ?: emptyList()
                requestedOrganizations = result?.requestedOrConnectedOrganizations ?: emptyList()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
            isLoadingOrganization = false
        }
    }


    var connectMessage by mutableStateOf<String?>(null)
    var connectError by mutableStateOf<String?>(null)

    fun connectToOrganization(vendorId: Int, orgId: Int) {
        viewModelScope.launch {
            connectMessage = null
            connectError = null

            try {
                val result = repository.connectVendorToOrganization(vendorId, orgId)
                if (result.success == true || result.message?.contains("success", true) == true) {
                    connectMessage = result.message
                    // Optionally reload organizations
                    loadOrganizations(vendorId)
                } else {
                    connectError = result.errors?.values?.flatten()?.joinToString("\n")
                        ?: result.message ?: result.error ?: "Unknown error"
                }
            } catch (e: Exception) {
                connectError = e.localizedMessage ?: "Unexpected error"
            }
        }
    }

    private val _statusUpdateResponse = mutableStateOf<SuborderStatusUpdateResponse?>(null)
    val statusUpdateResponse: State<SuborderStatusUpdateResponse?> = _statusUpdateResponse

    fun updateSuborderStatus(suborderId: Int, currentStatus: String) {
        val nextStatus = getNextStatus(currentStatus)
        if (nextStatus != null) {
            viewModelScope.launch {
                val result = repository.updateSuborderStatus(suborderId, nextStatus)
                _statusUpdateResponse.value = result
            }
        }
    }

    private fun getNextStatus(currentStatus: String): String? {
        return when (currentStatus.lowercase()) {
            "pending" -> "in_progress"
            "in_progress" -> "ready"
            "picked_up" -> "handover_confirmed" // show button only at picked_up
            else -> null // returns null for "ready", "assigned", etc.
        }
    }


    fun updatePaymentStatus(suborderId: Int, currentPaymentStatus: String) {
        val nextPaymentStatus = when (currentPaymentStatus.lowercase()) {
            "confirmed_by_deliveryboy" -> "confirmed_by_vendor"
            else -> null
        }

        if (nextPaymentStatus != null) {
            viewModelScope.launch {
                val result = repository.updateSuborderStatus(suborderId, nextPaymentStatus)
                _statusUpdateResponse.value = result
            }
        }
    }


    private val _summary = MutableLiveData<Result<VendorSummaryResponse>>()
    val summary: LiveData<Result<VendorSummaryResponse>> = _summary

    fun loadSummary(vendorId: Int) {
        viewModelScope.launch {
            _summary.value = repository.fetchVendorSummary(vendorId)
        }
    }


}
