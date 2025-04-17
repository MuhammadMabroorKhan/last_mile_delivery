package com.example.lastmiledelivery.data.models.deliveryboy

data class DeliveryBoyDataResponse(
    val delivery_boy_id: Int,
    val name: String,
    val email: String,
    val phone_no: String,
    val cnic: String,
    val lmd_user_role: String,
    val profile_picture: String?, // nullable
    val license_no: String,
    val license_expiration_date: String,
    val license_front: String?, // nullable
    val license_back: String?, // nullable
    val status: String,
    val approval_status: String,
    val address_type: String?, // nullable
    val street: String?,       // nullable
    val city: String?,         // nullable
    val zip_code: String?,     // nullable
    val country: String?,      // nullable
    val latitude: Double?,     // nullable
    val longitude: Double?     // nullable
)


data class ErrorResponse(
    val error:String
)