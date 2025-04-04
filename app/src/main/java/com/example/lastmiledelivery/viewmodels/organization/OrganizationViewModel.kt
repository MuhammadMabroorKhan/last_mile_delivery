package com.example.lastmiledelivery.viewmodels.organization

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.customer.CustomerSignupResponse
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


}