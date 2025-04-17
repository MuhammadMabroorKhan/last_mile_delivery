package com.example.lastmiledelivery.data.repository.deliveryboy

import com.example.lastmiledelivery.data.models.customer.CustomerData
import com.example.lastmiledelivery.data.models.deliveryboy.AcceptOrderResponse
import com.example.lastmiledelivery.data.models.deliveryboy.AssignedSuborderResponse
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyDataResponse
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyToggleResponse
import com.example.lastmiledelivery.data.models.deliveryboy.LatestLocationResponse
import com.example.lastmiledelivery.data.models.deliveryboy.LocationRequest
import com.example.lastmiledelivery.data.models.deliveryboy.LocationResponse
import com.example.lastmiledelivery.data.models.deliveryboy.PickupRequest
import com.example.lastmiledelivery.data.models.deliveryboy.ReachDestinationRequest
import com.example.lastmiledelivery.data.models.deliveryboy.ReachDestinationResponse
import com.example.lastmiledelivery.data.models.deliveryboy.ReadySuborder
import com.example.lastmiledelivery.data.remote.api.DeliveryBoysApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import retrofit2.Response
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


    suspend fun acceptOrder(deliveryBoyId: Int, suborderId: Int): Response<AcceptOrderResponse> {
        return api.acceptOrder(deliveryBoyId, suborderId)
    }


    suspend fun getAssignedSuborders(deliveryBoyId: Int): AssignedSuborderResponse? {
        return try {
            val response = api.getAssignedSuborders(deliveryBoyId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun confirmPickup(suborderId: Int, lat: Double, lng: Double): Result<String> {
        return try {
            val response = api.confirmPickup(suborderId, PickupRequest(lat, lng))
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Success")
            } else {
                val errorMessage = response.errorBody()?.string()?.let {
                    JSONObject(it).optString("error", "Unknown error")
                } ?: "Unknown error"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updateLocation(
        suborderId: Int,
        latitude: Double,
        longitude: Double
    ): LocationResponse? {
        return try {
            val response = api.updateLocation(suborderId, LocationRequest(latitude, longitude))
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }


    suspend fun reachDestination(
        deliveryBoyId: Int,
        suborderId: Int,
        latitude: Double,
        longitude: Double
    ): ReachDestinationResponse? {
        return try {
            val response = api.reachDestination(
                deliveryBoyId,
                suborderId,
                ReachDestinationRequest(latitude, longitude)
            )
            response.body()
        } catch (e: Exception) {
            ReachDestinationResponse(null, e.localizedMessage ?: "Unknown error")
        }
    }


    suspend fun fetchLatestLocation(suborderId: Int): Result<LatestLocationResponse> {
        return try {
            val response = api.getLatestLocation(suborderId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun confirmPaymentByDeliveryBoy(suborderId: Int): Result<String> {
        return try {
            val response = api.confirmPaymentByDeliveryBoy(suborderId)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Success")
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}