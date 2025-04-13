package com.example.lastmiledelivery.data.remote.api

import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyDataResponse
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyToggleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface DeliveryBoysApiService {

    @GET("api/deliveryboy/{id}")
    suspend fun getDeliveryBoyData(@Path("id") deliveryBoy_ID: Int): Response<DeliveryBoyDataResponse>

    @PUT("api/deliveryboys/status/{id}")
    suspend fun deliveryBoyToggleStatus(@Path("id") deliveryBoy_ID:Int):Response<DeliveryBoyToggleResponse>
}
