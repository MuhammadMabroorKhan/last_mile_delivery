package com.example.lastmiledelivery.data.models.deliveryboy

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class AssignedSuborderResponse(
    val status: String?,
    val data: List<AssignedSuborder>?
):Parcelable

@Parcelize
data class AssignedSuborder(
    val suborder_id: Int?,
    val vendor_type: String?,
    val vendor_order_id: String?, // Nullable
    val status: String?,
    val payment_status: String?,
    val total_amount: String?,
    val estimated_delivery_time: String?,
    val delivery_time: String?,
    val deliveryboys_ID: Int?,
    val orders_ID: Int?,
    val vendor_ID: Int?,
    val shop_ID: Int?,
    val branch_ID: Int?,
    val order_date: String?,
    val shop: AssignedSuborderShopInfo?,
    val customer: AssignedSuborderCustomerInfo?
):Parcelable

@Parcelize
data class AssignedSuborderShopInfo(
    val name: String?,
    val branch: AssignedSuborderBranchInfo?
):Parcelable

@Parcelize
data class AssignedSuborderBranchInfo(
    val name: String?,
    val picture: String?,
    val pickup_location: AssignedSuborderLocationInfo?
):Parcelable

@Parcelize
data class AssignedSuborderLocationInfo(
    val latitude: String?,
    val longitude: String?,
    val area: String?,
    val city: String?
):Parcelable

@Parcelize
data class AssignedSuborderCustomerInfo(
    val name: String?,
    val phone: String?,
    val customer_picture: String?,
    val delivery_address: AssignedSuborderDeliveryAddress?
):Parcelable

@Parcelize
data class AssignedSuborderDeliveryAddress(
    val addresses_ID: Int?,
    val street: String?,
    val city: String?,
    val latitude: Double?,
    val longitude: Double?
):Parcelable



//PICKUP SUBORDER
// PickupRequest.kt
data class PickupRequest(
    val latitude: Double,
    val longitude: Double
)

// Success response
data class PickupSuccessResponse(
    val message: String
)

// Error response
data class PickupErrorResponse(
    val error: String
)



//Location UPdate Request and response
data class LocationRequest(
    val latitude: Double,
    val longitude: Double
)

data class LocationResponse(
    val message: String? = null,
    val error: String? = null
)




////Reach Destination>>>
data class ReachDestinationRequest(
    val latitude: Double,
    val longitude: Double
)

data class ReachDestinationResponse(
    val message: String?,
    val error: String?
)


//Get latest location updates from location tracking for teh reacheddestination

data class LatestLocationResponse(
    val latitude: Double?,
    val longitude: Double?,
    val status: String?,
    @SerializedName("time_stamp") val timeStamp: String?
)




//COnfirm Payment By deliveryboy
data class DeliveryBoyPaymentConfirmResponse(
    val message: String
)