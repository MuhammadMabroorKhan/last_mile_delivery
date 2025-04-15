package com.example.lastmiledelivery.data.remote.api

import com.example.lastmiledelivery.data.models.deliveryboy.AcceptOrderResponse
import com.example.lastmiledelivery.data.models.deliveryboy.AssignedSuborderResponse
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyDataResponse
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyToggleResponse
import com.example.lastmiledelivery.data.models.deliveryboy.LatestLocationResponse
import com.example.lastmiledelivery.data.models.deliveryboy.LocationRequest
import com.example.lastmiledelivery.data.models.deliveryboy.LocationResponse
import com.example.lastmiledelivery.data.models.deliveryboy.PickupRequest
import com.example.lastmiledelivery.data.models.deliveryboy.PickupSuccessResponse
import com.example.lastmiledelivery.data.models.deliveryboy.ReachDestinationRequest
import com.example.lastmiledelivery.data.models.deliveryboy.ReachDestinationResponse
import com.example.lastmiledelivery.data.models.deliveryboy.ReadySubordersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DeliveryBoysApiService {

    @GET("api/deliveryboy/{id}")
    suspend fun getDeliveryBoyData(@Path("id") deliveryBoy_ID: Int): Response<DeliveryBoyDataResponse>

    @PUT("api/deliveryboys/status/{id}")
    suspend fun deliveryBoyToggleStatus(@Path("id") deliveryBoy_ID: Int): Response<DeliveryBoyToggleResponse>

    @GET("api/deliveryboy/ready-suborders/{deliveryBoyLmdUserId}")
    suspend fun getReadySubordersForDeliveryBoy(
        @Path("deliveryBoyLmdUserId") deliveryBoyLmdUserId: Int
    ): Response<ReadySubordersResponse>

    //Assign / Accept Order
    @POST("api/deliveryboy/{deliveryBoyId}/accept-order/{suborderId}")
    suspend fun acceptOrder(
        @Path("deliveryBoyId") deliveryBoyId: Int,
        @Path("suborderId") suborderId: Int
    ): Response<AcceptOrderResponse>

    //this is is lmd user dleivery boy id
    @GET("api/deliveryboy/{deliveryBoyId}/assigned-suborders")
    suspend fun getAssignedSuborders(
        @Path("deliveryBoyId") deliveryBoyId: Int
    ): Response<AssignedSuborderResponse>


    @PATCH("api/deliveryboy/order/{suborderId}/pickup")
    suspend fun confirmPickup(
        @Path("suborderId") suborderId: Int,
        @Body request: PickupRequest
    ): Response<PickupSuccessResponse>



        @PUT("api/deliveryboy/order/{suborderId}/location")
        suspend fun updateLocation(
            @Path("suborderId") suborderId: Int,
            @Body location: LocationRequest
        ): Response<LocationResponse>

    @POST("api/deliveryboy/reach-destination/{deliveryBoyId}/{suborderId}")
    suspend fun reachDestination(
        @Path("deliveryBoyId") deliveryBoyId: Int,
        @Path("suborderId") suborderId: Int,
        @Body location: ReachDestinationRequest
    ): Response<ReachDestinationResponse>

    @GET("api/suborder/{suborderId}/latest-location")
    suspend fun getLatestLocation(@Path("suborderId") suborderId: Int): Response<LatestLocationResponse>
}
