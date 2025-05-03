package com.example.lastmiledelivery.data.models.customer

data class AddAddressRequest(
    val address_type: String,
    val street: String,
    val city: String,
    val zip_code: String?,
    val country: String?,
    val latitude: Double?,
    val longitude: Double?
)

data class AddAddressResponse(
    val message: String
)