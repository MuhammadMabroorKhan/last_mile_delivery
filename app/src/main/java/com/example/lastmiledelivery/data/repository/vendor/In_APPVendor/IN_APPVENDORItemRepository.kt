package com.example.lastmiledelivery.data.repository.vendor.In_APPVendor

import android.util.Log
import com.example.lastmiledelivery.data.models.admin.MessageResponse
import com.example.lastmiledelivery.data.models.vendor.CreateItemRequest
import com.example.lastmiledelivery.data.models.vendor.ItemCategory
import com.example.lastmiledelivery.data.models.vendor.ItemVariation
import com.example.lastmiledelivery.data.models.vendor.PredefinedAttributesResponse
import com.example.lastmiledelivery.data.models.vendor.VendorItemResponse
import com.example.lastmiledelivery.data.remote.api.VendorApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class IN_APPVENDORItemRepository @Inject constructor(
    private val apiService: VendorApiService
) {
    suspend fun getItemCategories(shopCategoryId: Int): Result<List<ItemCategory>> {
        return try {
            val response = apiService.getItemCategories(shopCategoryId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.categories)
            } else {
                Result.failure(Exception("Failed to fetch item categories"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun getItemVariations(itemCategoryId: Int): List<ItemVariation> {
        return try {
            val response = apiService.getItemVariations(itemCategoryId)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.variations
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error fetching item variations: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchPredefinedAttributes(itemCategoryId: Int): PredefinedAttributesResponse? {
        return try {
            val response = apiService.getPredefinedAttributes(itemCategoryId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }



    suspend fun createItem(
        vendorId: Int,
        shopId: Int,
        branchId: Int,
        name: RequestBody,
        timesensitive: RequestBody?,
        preparationTime: RequestBody?,
        description: RequestBody?,
        categoryId: RequestBody,
        branchesId: RequestBody,
        variationName: RequestBody?,
        price: RequestBody,
        additionalInfo: RequestBody?,
        picture: MultipartBody.Part?,
        attributes: Map<String, RequestBody>?,
        stock_qty:RequestBody
    ): Result<MessageResponse> {
        return try {
            val response = apiService.createItem(
                vendorId, shopId, branchId, name,timesensitive,preparationTime, description, categoryId, branchesId,
                variationName, price, additionalInfo, picture = picture, attributes =  attributes, stock_qty = stock_qty
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVendorItems(vendorId: Int, shopId: Int, branchId: Int): Result<List<VendorItemResponse>> {
        return try {
            val response = apiService.getVendorItems(vendorId, shopId, branchId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No items found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
