package com.example.lastmiledelivery.data.models.customer

import okhttp3.MultipartBody

data class CustomerSignupRequest(
    val name: String,
    val email: String,
    val phone_no: String,
    val password: String,
    val cnic: String,
    val address_type: String,
    val street: String,
    val city: String,
    val zip_code: String?,
    val country: String = "Pakistan",
    val latitude: Double?,
    val longitude: Double?,
    val profile_picture: MultipartBody.Part?
)

data class CustomerSignupResponse(
    val message: String,
    val user_id: Int?
)

class ApiException(message: String) : Exception(message)

