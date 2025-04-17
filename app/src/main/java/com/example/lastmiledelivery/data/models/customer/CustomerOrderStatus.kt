package com.example.lastmiledelivery.data.models.customer

import com.google.gson.annotations.SerializedName

data class ConfirmDeliveryResponse(
    val message: String? = null,
    val error: String? = null
)



data class PaymentStatusResponse(
    @SerializedName("suborder_id") val suborderId: Int?,
    @SerializedName("payment_status") val paymentStatus: String?,
    @SerializedName("error") val error: String? = null
)



data class ConfirmPaymentResponse(
    val message: String?
)

data class CancelOrderResponse(
    val message: String
)



//Customer Live Tracking
data class LiveTrackingResponse(
    val message: String?,
    val data: LiveLocationData?
)

data class LiveLocationData(
    val latitude: Double,
    val longitude: Double,
    val status: String
)