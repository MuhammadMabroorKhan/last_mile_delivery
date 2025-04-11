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
import com.example.lastmiledelivery.data.models.organization.DeliveryBoySignupResponse
import com.example.lastmiledelivery.data.models.organization.OrganizationData
import com.example.lastmiledelivery.data.models.organization.OrganizationSignupResponse
import com.example.lastmiledelivery.data.repository.customer.CustomerRepository
import com.example.lastmiledelivery.data.repository.organization.OrganizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


@HiltViewModel
class OrganizationViewModel @Inject constructor(private val repository: OrganizationRepository, private val context: Application) : ViewModel() {
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
}