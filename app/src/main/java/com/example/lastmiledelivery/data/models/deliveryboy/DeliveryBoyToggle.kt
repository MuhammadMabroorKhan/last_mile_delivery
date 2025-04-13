package com.example.lastmiledelivery.data.models.deliveryboy

data class DeliveryBoyToggleResponse(
    val message: String,
    val deliveryBoyONOFF: DeliverBoyONOFF? = null
)

data class DeliverBoyONOFF(
    val id: Int,
    val previous_status: String,
    val new_status: String
)

