package com.example.lastmiledelivery.viewmodels.vendor

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.vendor.VendorResponse
import com.example.lastmiledelivery.data.models.vendor.VendorSignupResponse
import com.example.lastmiledelivery.data.repository.vendor.VendorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel   //private val context: Application
class VendorViewModel @Inject constructor(private val repository: VendorRepository ,private val context: Application) : ViewModel() {
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
        Log.d("RecievedIDHEREViewModel"," ${id}")
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
            putInt("vendor_id",id)
            apply()
        }
        Log.d("viewmodel_VendorID","$id")
    }

    fun getVendorId(): Int? {
        return sharedPreferences.getInt("vendor_id", -1)
    }

}
