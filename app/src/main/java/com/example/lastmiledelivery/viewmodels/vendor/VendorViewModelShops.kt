package com.example.lastmiledelivery.viewmodels.vendor

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.vendor.Branch
import com.example.lastmiledelivery.data.models.vendor.CreateBranchResponse
import com.example.lastmiledelivery.data.models.vendor.Shop
import com.example.lastmiledelivery.data.models.vendor.ShopCreationResponse
import com.example.lastmiledelivery.data.models.vendor.ShopRequest
import com.example.lastmiledelivery.data.models.vendor.ToggleBranchResponse
import com.example.lastmiledelivery.data.models.vendor.UpdateBranchResponse
import com.example.lastmiledelivery.data.repository.vendor.VendorRepository
import com.example.lastmiledelivery.data.repository.vendor.VendorRepositoryShops
import com.google.android.gms.common.api.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VendorViewModelShops @Inject constructor(private val repository: VendorRepositoryShops) : ViewModel() {

    private val _shops = MutableStateFlow<List<Shop>?>(null)
    val shops: StateFlow<List<Shop>?> = _shops

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    fun fetchShops(vendorId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getVendorShops(vendorId)
                if (response.isSuccessful) {
                    _shops.value = response.body()?.shops
                } else {
                    _errorMessage.value = "No shops found for this vendor"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }


    private val _shopState = MutableStateFlow<ShopCreationResponse?>(null)
    val shopState: StateFlow<ShopCreationResponse?> = _shopState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState


    fun createShop(shopRequest: ShopRequest) {
        viewModelScope.launch {
            repository.createShop(shopRequest).collect { result ->
                result.onSuccess {
                    _shopState.value = it
                    fetchShops(shopRequest.vendors_ID) // Fetch updated shops immediately
                }
                result.onFailure { _errorState.value = it.message }
            }
        }
    }


    private val _branches = MutableStateFlow<List<Branch>?>(null)
    val branches: StateFlow<List<Branch>?> get() = _branches


    fun fetchBranches(shopId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getBranchesByShopId(shopId)
                if (response.isSuccessful) {
                    _branches.value = response.body()
                } else {
                    _errorMessage.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch branches"
            }
        }
    }

    private val _selectedBranch = MutableStateFlow<Branch?>(null)
    val selectedBranch: StateFlow<Branch?> = _selectedBranch

    fun selectBranch(branch: Branch) {
        _selectedBranch.value = branch
    }



    private val _branchState = MutableStateFlow<CreateBranchResponse?>(null)
    val branchState: StateFlow<CreateBranchResponse?> get() = _branchState

    fun createBranch(
        latitude: RequestBody,
        longitude: RequestBody,
        description: RequestBody?,
        openingHours: RequestBody,
        closingHours: RequestBody,
        contactNumber: RequestBody,
        cityId: RequestBody,
        areaName: RequestBody,
        postalCode: RequestBody?,
        shopsId: RequestBody,
        branchPicture: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createBranch(
                    latitude,
                    longitude,
                    description,
                    openingHours,
                    closingHours,
                    contactNumber,
                    cityId,
                    areaName,
                    postalCode,
                    shopsId,
                    branchPicture
                )

                if (response.isSuccessful && response.body() != null) {
                    _branchState.value = response.body()
//                    response.body()!!.data?.shopsId?.let { fetchBranches(it.toInt()) }
                    fetchBranches(shopsId.toStringValue().toInt())
                } else {
                    Log.e("CreateBranchError", "Error: ${response.errorBody()?.string()}")
                    _branchState.value = null // Indicate an error
                }
            } catch (e: Exception) {
                Log.e("CreateBranchError", "Exception: ${e.localizedMessage}")
                _branchState.value = null // Indicate an error
            }
        }
    }


private val _updateBranchState = MutableStateFlow<UpdateBranchResponse?>(null)
    val updateBranchState: StateFlow<UpdateBranchResponse?> get() = _updateBranchState


fun updateBranch(
    branchId: Int,
    latitude: RequestBody,
    longitude: RequestBody,
    description: RequestBody?,
    openingHours: RequestBody,
    closingHours: RequestBody,
    contactNumber: RequestBody,
    cityId: RequestBody?,
    areaName: RequestBody?,
    postalCode: RequestBody?,
    shopsId: RequestBody,
    branchPicture: MultipartBody.Part?
) {
    viewModelScope.launch {
        try {
            Log.d("UpdateBranch", "Latitude: ${latitude?.toStringValue()}")
            Log.d("UpdateBranch", "Longitude: ${longitude?.toStringValue()}")
            Log.d("UpdateBranch", "Description: ${description?.toStringValue()}")
            Log.d("UpdateBranch", "Opening Hours: ${openingHours?.toStringValue()}")
            Log.d("UpdateBranch", "Closing Hours: ${closingHours?.toStringValue()}")
            Log.d("UpdateBranch", "Contact Number: ${contactNumber?.toStringValue()}")
            Log.d("UpdateBranch", "Shops ID: ${shopsId?.toStringValue()}")

            Log.d("UpdateBranch", "Branch Picture: ${branchPicture?.body?.contentType()}")

            val response = repository.updateBranch(
                branchId,
                latitude,
                longitude,
                description,
                openingHours,
                closingHours,
                contactNumber,
                cityId,
                areaName,
                postalCode,
                shopsId,
                branchPicture
            )

            if (response.isSuccessful) {
                response.body()?.let { result ->
                    Log.d("UpdateBranch", "Success: ${result.message}")
                    fetchBranches(shopsId.toStringValue().toInt())
                    Log.d("AGAINFETCH", " id is ${shopsId.toStringValue().toInt()}}")
                    _updateBranchState.value = result
                }
            } else {
                Log.e("UpdateBranchError", "Failed: ${response.errorBody()?.string()}")
                _updateBranchState.value = null
            }
        } catch (e: Exception) {
            Log.e("UpdateBranchException", "Exception: ${e.localizedMessage}", e)
            _updateBranchState.value = null
        }
    }
}


    fun RequestBody.toStringValue(): String {
        return try {
            this.contentLength().takeIf { it > 0 }?.let {
                val buffer = okio.Buffer()
                this.writeTo(buffer)
                buffer.readUtf8()
            } ?: "Empty Body"
        } catch (e: Exception) {
            "Error reading body"
        }
    }


    private val _toggleBranchStatus = MutableLiveData<Result<ToggleBranchResponse>>()
    val toggleBranchStatus: LiveData<Result<ToggleBranchResponse>> = _toggleBranchStatus

    fun toggleBranchStatus(branchId: Int) {
        viewModelScope.launch {
            repository.toggleBranchStatus(branchId).collect { result ->
                _toggleBranchStatus.postValue(result)
            }
        }
    }
}