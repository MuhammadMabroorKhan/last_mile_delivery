package com.example.lastmiledelivery.data.models.customer

import com.google.gson.annotations.SerializedName

// Customer.kt
data class CustomerData(
    @SerializedName("customer_id") val customerId: Int,
    val name: String,
    val email: String,
    @SerializedName("phone_no") val phoneNo: String,
    val cnic: String,
    val password: String, // Include if needed, but be cautious with sensitive info.
    @SerializedName("lmd_user_role") val role: String,
    @SerializedName("profile_picture") val profilePicture: String? // Nullable to handle nulls.
)


//Customer Main Screen Info
data class CustomerMainScreenResponse(
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("shop_id") val shopId: Int,
    @SerializedName("branch_id") val branchId: Int,
    @SerializedName("shopcategory_ID") val shopCategoryId: Int,
    @SerializedName("shop_category_name") val shopCategoryName: String,
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("shop_description") val shopDescription: String,
    @SerializedName("shop_status") val shopStatus: String,
    @SerializedName("branch_description") val branchDescription: String,
    @SerializedName("contact_number") val contactNumber: String,
    @SerializedName("opening_hours") val openingHours: String?,
    @SerializedName("closing_hours") val closingHours: String?,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String,
    @SerializedName("branch_status") val branchStatus: String,
    @SerializedName("approval_status") val approvalStatus: String,
    @SerializedName("branch_picture") val branchPicture: String?,
    @SerializedName("vendor_type") val vendorType: String,
    @SerializedName("reviews_count") val reviewsCount: Int,
    @SerializedName("avg_Rating") val avgRating: Float
)



//SHop detail screen fethc categories for items
data class CategoryResponse(
    val id: Int,
    val name: String,
    val shop_category_ID: Int
)




//Ftech Item for branch
data class MenuResponse(
    val error: String? = null,
    val items: List<MenuItem>? = emptyList()  // Can be null if an error occurs
)

data class MenuItem(
    val item_id: Int,
    val item_name: String,
    val item_description: String,
    val timesensitive: String,
    val preparation_time: Int,
    val itemPicture: String?, // ✅ Nullable
    val itemdetail_id: Int,
    val variation_name: String,
    val price: String,
    val additional_info: String?, // ✅ Nullable
    val item_category_id: Int,
    val item_category_name: String,
    val attributes: List<Attribute>? = emptyList() // ✅ Default to empty list to avoid null errors
)

data class Attribute(
    val key: String,
    val value: String
)





data class GenericResponse(
    val message: String
)



///////////////
//CUSTOMER CART
//////////////
data class CartResponse(
    val cart: Cart?,
    val suborders: List<Suborder> = emptyList()
)

data class Cart(
    val id: Int,
    val cart_date: String,
    val total_amount: String,
    val cart_status: String,
    val customers_ID: Int,
    val updated_at: String
)

data class Suborder(
    val id: Int,
    val vendor_type: String,
    val vendor_ID: Int,
    val shop_ID: Int,
    val branch_ID: Int,
    val total_amount: String,
    val delivery_fee: String,
    val cart_ID: Int,
    val items: List<CartItem>
)

data class CartItem(
    val id: Int,
    val quantity: Int,
    val price: String,
    val total: String,
    val itemdetails_ID: Int,
    val cart_suborders_ID: Int,
    val item_name: String,
    val item_detail_id: Int,
    val item_description: String,
    val timesensitive: String,
    val preparation_time: Int,
    val itemPicture: String,
    val variation_name: String,
    val additional_info: String,
    val item_category_id: Int,
    val item_category_name: String,
    val attributes: List<ItemAttribute>,
    val error_message: String?
)

data class ItemAttribute(
    val key: String,
    val value: String
)
///////////////
//MenuItem CART
//////////////
data class CartMenuItem(
    val id: Int,
    val item_name: String,
    val item_description: String,
    val price: Double,
    val itemPicture: String,
    val vendor_id: Int,
    val shop_id: Int,
    val branch_id: Int,
    val itemdetails_id: Int
)
///////////////
//AddToCartRequest
//////////////
data class AddToCartRequest(
    val customer_id: Int,
    val vendor_id: Int,
    val shop_id: Int,
    val branch_id: Int,
    val itemdetails_id: Int,
    val quantity: Int,
    val price: Double
)

data class AddCartResponse(
    val success: Boolean,
    val message: String
)
