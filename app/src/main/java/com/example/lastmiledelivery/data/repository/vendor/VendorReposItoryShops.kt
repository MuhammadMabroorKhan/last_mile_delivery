package com.example.lastmiledelivery.data.repository.vendor


import com.example.lastmiledelivery.data.models.vendor.Branch
import com.example.lastmiledelivery.data.models.vendor.CreateBranchResponse
import com.example.lastmiledelivery.data.models.vendor.ShopCreationResponse
import com.example.lastmiledelivery.data.models.vendor.ShopRequest
import com.example.lastmiledelivery.data.models.vendor.ShopResponse
import com.example.lastmiledelivery.data.models.vendor.ToggleBranchResponse
import com.example.lastmiledelivery.data.models.vendor.UpdateBranchResponse
import com.example.lastmiledelivery.data.remote.api.VendorApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

import retrofit2.HttpException
import java.io.File

class VendorRepositoryShops @Inject constructor(private val apiService: VendorApiService) {

    suspend fun getVendorShops(vendorId: Int): Response<ShopResponse> {
        return withContext(Dispatchers.IO) {
            apiService.getVendorShops(vendorId)
        }
    }

    suspend fun createShop(shopRequest: ShopRequest): Flow<Result<ShopCreationResponse>> = flow {
        try {
            val response: Response<ShopCreationResponse> = apiService.createShop(shopRequest)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.success(it))
                } ?: emit(Result.failure(Exception("Empty Response")))
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun getBranchesByShopId(shopId: Int): Response<List<Branch>?> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getBranchesByShopId(shopId)
            if (response.isSuccessful) {
                Response.success(response.body()?.branches)
            } else {
                Response.error(response.code(), response.errorBody()!!)
            }
        }
    }

    suspend fun createBranch(
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
    ): retrofit2.Response<CreateBranchResponse> {
        return apiService.createBranch(
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
    }


    suspend fun updateBranch(
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
    ): Response<UpdateBranchResponse> {
        return apiService.updateBranch(branchId,
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
    }

        // ✅ Fixed: Correct function without conflicts
        private fun String.toRequestBody(): RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), this)




suspend fun toggleBranchStatus(branchId: Int): Flow<Result<ToggleBranchResponse>> = flow {
    try {
        val response = apiService.toggleBranchStatus(branchId)
        if (response.isSuccessful) {
            response.body()?.let {
                emit(Result.success(it))
            } ?: emit(Result.failure(Exception("Empty Response")))
        } else {
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            emit(Result.failure(Exception(errorMessage)))
        }
    } catch (e: Exception) {
        emit(Result.failure(e))
    }
}.flowOn(Dispatchers.IO)


}