package com.example.lastmiledelivery.data.models.vendor

import okhttp3.MultipartBody

data class ItemCategoryResponse(
    val message: String,
    val categories: List<ItemCategory>
)

data class ItemCategory(
    val id: Int,
    val name: String,
    val shop_category_ID: Int
)




//Defaut VARATION
data class ItemVariationResponse(
    val message: String,
    val variations: List<ItemVariation>
)


data class ItemVariation(
    val name: String,
    val itemcategory_ID: Int
)


// default attributes when item category s selected...
data class PredefinedAttributesResponse(
    val message: String,
    val attributes: Map<String, List<String>> // Key-Value Pair
)



//create item
data class CreateItemRequest(
    val name: String,
    val description: String?,
    val category_ID: Int,
    val branches_ID: Int,
    val variation_name: String?,
    val price: Double,
    val additional_info: String?,
    val picture: MultipartBody.Part?, // Optional image
    val attributes: List<ItemAttribute>?, // Nullable list of attributes
    val stock_qty:Int
    )

data class ItemAttribute(
    val key: String,
    val value: String
)



data class VendorItemResponse(
    val item_id: Int,
    val item_name: String,
    val item_description: String,
    val timesensitive: String,
    val preparation_time: Int,
    val itemdetail_id: Int?,
    val variation_name: String?,
    val price: String?,
    val additional_info: String?,
    val picture: String?,
    val attributes: List<Attribute>?
)

data class Attribute(
    val key: String,
    val value: String
)
