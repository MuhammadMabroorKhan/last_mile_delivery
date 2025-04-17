package com.example.lastmiledelivery.data.models.deliveryboy

data class ReadySubordersResponse(
    val status: String,
    val data: List<ReadySuborder>
)

data class ReadySuborder(
    val suborder_id: Int,
    val vendor_type: String,
    val vendor_order_id: String,
    val status: String,
    val payment_status: String,
    val total_amount: String,
    val estimated_delivery_time: String?,
    val delivery_time: String?,
    val deliveryboys_ID: Int?,
    val orders_ID: Int,
    val vendor_ID: Int,
    val shop_ID: Int,
    val branch_ID: Int,
    val order_date: String,
    val shop: Shop,
    val customer: Customer
)

data class Shop(
    val name: String,
    val branch: Branch
)

data class Branch(
    val name: String,
    val picture: String?,
    val pickup_location: PickupLocation
)

data class PickupLocation(
    val latitude: String,
    val longitude: String,
    val area: String,
    val city: String
)

data class Customer(
    val name: String,
    val phone: String,
    val customer_picture: String?,
    val delivery_address: DeliveryAddress
)

data class DeliveryAddress(
    val addresses_ID: Int,
    val street: String,
    val city: String,
    val latitude: Double,
    val longitude: Double
)

