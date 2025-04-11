package com.example.lastmiledelivery.data.models.organization

import okhttp3.MultipartBody

data class DeliveryBoySignupResponse(
    val message: String,
    val delivery_boy_id: Int? = null,
    val errors: Map<String, List<String>>? = null
)

data class DeliveryBoySignupRequest(
    val name: String,
    val email: String,
    val phone_no: String,
    val password: String,
    val cnic: String,
    val profile_picture: MultipartBody.Part,
    val license_no: String,
    val license_expiration_date: String?,
    val license_front: MultipartBody.Part,
    val license_back: MultipartBody.Part,
    val address_type: String,
    val street: String,
    val city: String,
    val zip_code: String?,
    val country: String = "Pakistan",
    val latitude: Double?,
    val longitude: Double?,
    val organization_id: Int?
)
