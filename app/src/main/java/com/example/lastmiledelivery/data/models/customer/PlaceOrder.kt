package com.example.lastmiledelivery.data.models.customer

import com.google.gson.annotations.SerializedName


data class OrderRequest(
    val customer_id: Int,
    val delivery_address_id: Int,
    val order_details: List<OrderDetail>
)

data class OrderDetail(
    val vendor_id: Int,
    val shop_id: Int,
    val branch_id: Int,
    val item_detail_id: Int,
    val quantity: Int,
    val price: Double
)



//Order Response
data class OrderResponse(
    val message: String,
    val order_id: Int?,
    val total_amount: Double?,
    val errors: Map<String, List<String>>? // Handles validation errors
)



//Address
data class AddressResponse(
    val addresses: List<Address>? = emptyList()
)

data class Address(
    val id: Int,
    val address_type: String,
    val street: String,
    val city: String,
    val zip_code: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)




//CLEAR CART
data class ClearCartRequest(
    @SerializedName("customer_id") val customerId: Int
)

data class ClearCartResponse(
    @SerializedName("message") val message: String
)

