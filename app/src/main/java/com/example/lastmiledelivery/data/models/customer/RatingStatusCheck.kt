package com.example.lastmiledelivery.data.models.customer

data class RatingsResponse(
    val success: Boolean,
    val message: String,
    val data: RatingData? = null
)

data class RatingData(
    val vendor_type: String = "",
    val has_rated: Boolean = false,
    val delivery_boy_rating: DeliveryBoyRating = DeliveryBoyRating(false),
    val item_ratings: ItemRatings = ItemRatings(false)
)

data class DeliveryBoyRating(
    val has_rated: Boolean,
    val rating_stars: Int? = null,
    val comments: String? = null,
    val rating_date: String? = null
)

data class ItemRatings(
    val has_rated: Boolean,
    val ratings: List<ItemRating>? = null
)

data class ItemRating(
    val itemdetails_ID: Int,
    val rating_stars: String,
    val comments: String,
    val rating_date: String,
    val images: List<String>
)