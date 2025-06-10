package com.example.lastmiledelivery.data.models.customer

import android.net.Uri
import com.google.gson.annotations.SerializedName
import java.io.File

data class ItemRatingRequest(
    val itemdetails_ID: Int,
    val suborders_ID: Int,
    val rating: Int,
    val comment: String,
    val images: List<Uri> = emptyList()
)


data class ItemRatingResponse(
    val message: String,
    val error: String? = null
)



//Delivery BOy
data class DeliveryBoyRatingRequest(
    @SerializedName("suborder_ID") val suborderId: Int,
    @SerializedName("rating_stars") val ratingStars: Int,
    @SerializedName("comments") val comments: String? = null
)
data class DeliveryBoyRatingResponse(
    val success: Boolean,
    val message: String
)
