package com.example.lastmiledelivery.data.repository.common

import com.example.lastmiledelivery.data.models.ShopCategoryResponse
import com.example.lastmiledelivery.data.remote.api.ApiService
import javax.inject.Inject

class ShopCategoryRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getShopCategories(): List<ShopCategoryResponse>? {
        return try {
            val response = apiService.getShopCategories()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
