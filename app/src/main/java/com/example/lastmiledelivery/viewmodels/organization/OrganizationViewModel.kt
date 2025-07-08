package com.example.lastmiledelivery.viewmodels.organization

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.customer.CustomerSignupResponse
import com.example.lastmiledelivery.data.models.organization.DeliveryBoy
import com.example.lastmiledelivery.data.models.organization.DeliveryBoyEarningsResponse
import com.example.lastmiledelivery.data.models.organization.DeliveryBoySignupResponse
import com.example.lastmiledelivery.data.models.organization.OrgEarningsResponse
import com.example.lastmiledelivery.data.models.organization.OrganizationData
import com.example.lastmiledelivery.data.models.organization.OrganizationSignupResponse
import com.example.lastmiledelivery.data.models.organization.OrganizationStats
import com.example.lastmiledelivery.data.models.organization.VendorOrganizationRejectionReason
import com.example.lastmiledelivery.data.models.organization.VendorRequestOrganizationResponse
import com.example.lastmiledelivery.data.repository.customer.CustomerRepository
import com.example.lastmiledelivery.data.repository.organization.OrganizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


@HiltViewModel
class OrganizationViewModel @Inject constructor(
    private val repository: OrganizationRepository,
    private val context: Application
) : ViewModel() {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)


    private val _signupState = MutableLiveData<Result<OrganizationSignupResponse>?>()
    val signupState: MutableLiveData<Result<OrganizationSignupResponse>?> get() = _signupState

    fun organizationSignup(
        name: RequestBody, email: RequestBody, phoneNo: RequestBody,
        password: RequestBody, cnic: RequestBody, addressType: RequestBody,
        street: RequestBody, city: RequestBody, zipCode: RequestBody?,
        country: RequestBody, latitude: RequestBody?, longitude: RequestBody?,
        profilePicture: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            val result = repository.organizationSignup(
                name,
                email,
                phoneNo,
                password,
                cnic,
                addressType,
                street,
                city,
                zipCode,
                country,
                latitude,
                longitude,
                profilePicture
            )
            _signupState.value = result
        }
    }

    fun clearSignupState() {
        _signupState.value = null  // Reset the state
    }


    private val _deliveryBoySignupState = MutableLiveData<Result<DeliveryBoySignupResponse>?>()
    val deliveryBoySignupState: LiveData<Result<DeliveryBoySignupResponse>?> get() = _deliveryBoySignupState

    fun deliveryBoySignup(
        name: RequestBody, email: RequestBody, phoneNo: RequestBody,
        password: RequestBody, cnic: RequestBody,
        profilePicture: MultipartBody.Part,
        licenseNo: RequestBody, licenseExpDate: RequestBody?,
        licenseFront: MultipartBody.Part, licenseBack: MultipartBody.Part,
        addressType: RequestBody, street: RequestBody, city: RequestBody,
        zipCode: RequestBody?, country: RequestBody,
        latitude: RequestBody?, longitude: RequestBody?,
        organizationId: RequestBody?
    ) {
        // Log the data being sent
        Log.d("DeliveryBoySignup", "Sending Data: ")
        Log.d("DeliveryBoySignup", "Name: ${name.toString()}")
        Log.d("DeliveryBoySignup", "Email: ${email.toString()}")
        Log.d("DeliveryBoySignup", "Phone No: ${phoneNo.toString()}")
        Log.d("DeliveryBoySignup", "Password: ${password.toString()}")
        Log.d("DeliveryBoySignup", "CNIC: ${cnic.toString()}")
        Log.d("DeliveryBoySignup", "License No: ${licenseNo.toString()}")
        Log.d("DeliveryBoySignup", "License Exp Date: ${licenseExpDate.toString()}")
        Log.d("DeliveryBoySignup", "Address Type: ${addressType.toString()}")
        Log.d("DeliveryBoySignup", "Street: ${street.toString()}")
        Log.d("DeliveryBoySignup", "City: ${city.toString()}")
        Log.d("DeliveryBoySignup", "Zip Code: ${zipCode.toString()}")
        Log.d("DeliveryBoySignup", "Country: ${country.toString()}")
        Log.d("DeliveryBoySignup", "Latitude: ${latitude.toString()}")
        Log.d("DeliveryBoySignup", "Longitude: ${longitude.toString()}")
        Log.d("DeliveryBoySignup", "Organization Id: ${organizationId.toString()}")
        Log.d("DeliveryBoySignup", "Profle : ${profilePicture.toString()}")
        Log.d("DeliveryBoySignup", "licenseback : ${licenseBack.toString()}")
        Log.d("DeliveryBoySignup", "licence front : ${licenseFront.toString()}")

        viewModelScope.launch {
            val result = repository.deliveryBoySignup(
                name, email, phoneNo, password, cnic, profilePicture,
                licenseNo, licenseExpDate, licenseFront, licenseBack,
                addressType, street, city, zipCode, country,
                latitude, longitude, organizationId
            )

            Log.d("DeliveryBoySignup", "Response: $result")
            _deliveryBoySignupState.value = result
        }
    }

    fun clearDeliveryBoySignupState() {
        _deliveryBoySignupState.value = null
    }


    var organizationState by mutableStateOf<OrganizationData?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchOrganizationData(id: Int) {
        viewModelScope.launch {
            try {
                val organization = repository.getOrganizationData(id)
                if (organization != null) {
                    organizationState = organization
                    with(sharedPreferences.edit()) {
                        putInt("organization_id", organization.organizationId)
                        apply()
                    }
                    Log.d("ORG_ID", "${organization.organizationId}")
                } else {
                    errorMessage = "Organization not found"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
            }
        }
    }

    fun getOrganizationId(): Int? {
        val id = sharedPreferences.getInt("organization_id", -1)
        return if (id != -1) id else null
    }


    var deliveryBoyList by mutableStateOf<List<DeliveryBoy>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var _errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchDeliveryBoys(orgId: Int) {
        viewModelScope.launch {
            isLoading = true
            _errorMessage = null

            val result = repository.getDeliveryBoysByOrganization(orgId)
            if (result.isSuccess) {
                deliveryBoyList = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
            }

            isLoading = false
        }
    }

    var vendorRequestsOrganizationForCOnnection by mutableStateOf<VendorRequestOrganizationResponse?>(
        null
    )
        private set

    var errorMessageVendorRequest by mutableStateOf<String?>(null)
        private set

    var isLoadingVendorRequest by mutableStateOf(false)
        private set

    fun loadVendorRequests(orgId: Int) {
        viewModelScope.launch {
            isLoadingVendorRequest = true
            errorMessageVendorRequest = null

            val result = repository.fetchVendorRequests(orgId)

            if (result.isSuccess) {
                vendorRequestsOrganizationForCOnnection = result.getOrNull()
            } else {
                errorMessageVendorRequest = result.exceptionOrNull()?.message ?: "Unknown error"
            }

            isLoadingVendorRequest = false
        }
    }

    var acceptRequestMessage by mutableStateOf<String?>(null)
        private set

    fun acceptVendorRequest(requestId: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            val result = repository.acceptVendorRequest(requestId)
//            acceptRequestMessage = when (result) {
//                is Result.Success -> result.getOrNull()?.message
//                is Result.Failure -> result.exceptionOrNull()?.message ?: "Unknown error"
//            }
            if (result.isSuccess) {
                acceptRequestMessage = result.getOrNull()?.message
            } else {
                acceptRequestMessage = result.exceptionOrNull()?.message ?: "Unknown error"
            }


            onComplete()
        }
    }

    var rejectionReasons by mutableStateOf<List<VendorOrganizationRejectionReason>>(emptyList())
    var rejectionError by mutableStateOf<String?>(null)

    fun fetchRejectionReasons(organizationId: Int) {
        viewModelScope.launch {
            val result = repository.getRejectionReasons(organizationId)
            if (result.isSuccess) {
                rejectionReasons = result.getOrNull() ?: emptyList()
                rejectionError = null
            } else {
                rejectionReasons = emptyList()
                rejectionError = result.exceptionOrNull()?.message ?: "Error fetching data"
            }
        }
    }


    var correctReasonMessage by mutableStateOf<String?>(null)

    fun correctRejectionReason(reasonId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.correctRejectionReason(reasonId)
            if (result.isSuccess) {
                correctReasonMessage = result.getOrNull()
                // Refresh the list after correction
                fetchRejectionReasons(rejectionReasons.firstOrNull()?.organization_ID ?: 0)
                onResult(true)
            } else {
                correctReasonMessage = result.exceptionOrNull()?.message
                onResult(false)
            }
        }
    }


    var rejectMessage by mutableStateOf<String?>(null)

    fun rejectVendorRequest(requestId: Int, reasons: List<String>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.rejectVendorRequest(requestId, reasons)
            if (result.isSuccess) {
                rejectMessage = result.getOrNull()
                onResult(true)
            } else {
                rejectMessage = result.exceptionOrNull()?.message
                onResult(false)
            }
        }
    }


    //Summary STats
    var statsState by mutableStateOf<OrganizationStats?>(null)
        private set

    var errorMessageStats by mutableStateOf<String?>(null)
        private set

//    fun fetchStats(orgId: Int) {
//        viewModelScope.launch {
//            val result = repository.fetchOrganizationStats(orgId)
//            result.onSuccess {
//                statsState = it
//            }.onFailure {
//                errorMessageStats = it.message
//            }
//        }
//    }

    fun fetchStats(orgId: Int) {
        Log.d("fetchStats", "Fetching stats for organization ID: $orgId")

        viewModelScope.launch {
            val result = repository.fetchOrganizationStats(orgId)

            result.onSuccess { stats ->
                Log.d("fetchStats", "Stats fetch successful")
                Log.d("fetchStats", "Total Users: ${stats.totalVendors}")
//                Log.d("fetchStats", "Users by Role: ${stats.totalDeliveryBoys}")
//                Log.d("fetchStats", "Total Orders: ${stats.totalDeliveredOrders}")
//                Log.d("fetchStats", "Orders by Status: ${stats.vendorApprovalStatus}")

                statsState = stats
            }.onFailure { exception ->
                Log.e("fetchStats", "Failed to fetch stats: ${exception.message}", exception)
                errorMessageStats = exception.message
            }


        }
    }




    var orgEarningsState by mutableStateOf<OrgEarningsUiState>(OrgEarningsUiState.Loading)
        private set

    var deliveryBoyEarningsState by mutableStateOf<DeliveryBoyEarningsUiState>(DeliveryBoyEarningsUiState.Loading)
        private set

    fun loadOrganizationEarnings(orgId: Int) {
        viewModelScope.launch {
            orgEarningsState = OrgEarningsUiState.Loading
            repository.getOrganizationEarnings(orgId).onSuccess {
                orgEarningsState = OrgEarningsUiState.Success(it)
            }.onFailure {
                orgEarningsState = OrgEarningsUiState.Error(it.message ?: "Unknown error")
            }
        }
    }

    fun loadDeliveryBoyEarnings(orgId: Int, deliveryBoyId: Int) {
        viewModelScope.launch {
            deliveryBoyEarningsState = DeliveryBoyEarningsUiState.Loading
            repository.getDeliveryBoyEarnings(orgId, deliveryBoyId).onSuccess {
                deliveryBoyEarningsState = DeliveryBoyEarningsUiState.Success(it)
            }.onFailure {
                deliveryBoyEarningsState = DeliveryBoyEarningsUiState.Error(it.message ?: "Unknown error")
            }
        }
    }

}


sealed class OrgEarningsUiState {
    object Loading : OrgEarningsUiState()
    data class Success(val data: OrgEarningsResponse) : OrgEarningsUiState()
    data class Error(val message: String) : OrgEarningsUiState()
}

sealed class DeliveryBoyEarningsUiState {
    object Loading : DeliveryBoyEarningsUiState()
    data class Success(val data: DeliveryBoyEarningsResponse) : DeliveryBoyEarningsUiState()
    data class Error(val message: String) : DeliveryBoyEarningsUiState()
}
