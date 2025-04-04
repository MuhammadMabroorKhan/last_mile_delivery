package com.example.lastmiledelivery.data.models.organization

import okhttp3.MultipartBody

data class OrganizationSignupRequest(
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

data class OrganizationSignupResponse(
    val message: String,
    val user_id: Int?
)