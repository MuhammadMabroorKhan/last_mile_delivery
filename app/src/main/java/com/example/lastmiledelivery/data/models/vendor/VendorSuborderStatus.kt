package com.example.lastmiledelivery.data.models.vendor

data class SuborderStatusUpdateRequest(
    val status: String
)

data class SuborderStatusUpdateResponse(
    val success: Boolean,
    val message: String,
    val data: StatusData?
)

data class StatusData(
    val status: String
)