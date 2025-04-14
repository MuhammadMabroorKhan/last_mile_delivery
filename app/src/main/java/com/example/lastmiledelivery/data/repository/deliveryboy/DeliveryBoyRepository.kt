package com.example.lastmiledelivery.data.repository.deliveryboy

import com.example.lastmiledelivery.data.models.customer.CustomerData
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyDataResponse
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyToggleResponse
import com.example.lastmiledelivery.data.models.deliveryboy.ReadySuborder
import com.example.lastmiledelivery.data.remote.api.DeliveryBoysApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


class DeliveryBoyRepository @Inject constructor(private val api: DeliveryBoysApiService) {


    suspend fun getDeliveryBoyData(id: Int): DeliveryBoyDataResponse? {
        val response = api.getDeliveryBoyData(id)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }


    suspend fun deliveryBoyToggleStatus(id: Int): DeliveryBoyToggleResponse? {
        val response = api.deliveryBoyToggleStatus(id)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }


    suspend fun getReadySubordersForDeliveryBoy(id: Int): Result<List<ReadySuborder>> {
        return try {
            val response = api.getReadySubordersForDeliveryBoy(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.status == "success") {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Unexpected API response"))
                }
            } else {
                Result.failure(Exception("API error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}