package com.example.lastmiledelivery.data.models.customer

import com.google.gson.annotations.SerializedName

data class RatingSubOrder(
    val id: Int,
    @SerializedName("vendor_type") val vendorType: String,
    @SerializedName("vendor_order_id") val vendorOrderId: String? = null,
    val status: String,
    @SerializedName("payment_status") val paymentStatus: String,
    @SerializedName("total_amount") val totalAmount: String,
    @SerializedName("estimated_delivery_time") val estimatedDeliveryTime: String? = null,
    @SerializedName("delivery_time") val deliveryTime: String? = null,
    @SerializedName("deliveryboys_ID") val deliveryBoyId: Int,
    @SerializedName("orders_ID") val orderId: Int,
    @SerializedName("vendor_ID") val vendorId: Int,
    @SerializedName("shop_ID") val shopId: Int,
    @SerializedName("branch_ID") val branchId: Int,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class DeliveryBoyInfo(
    @SerializedName("deliveryboy_id") val deliveryBoyId: Int,
    @SerializedName("total_deliveries") val totalDeliveries: Int,
    @SerializedName("license_no") val licenseNo: String,
    val status: String,
    @SerializedName("approval_status") val approvalStatus: String,
    @SerializedName("license_front") val licenseFront: String,
    @SerializedName("license_back") val licenseBack: String,
    val name: String,
    val email: String,
    @SerializedName("phone_no") val phoneNo: String,
    @SerializedName("profile_picture") val profilePicture: String
)

data class RatingItemAttribute(
    val key: String,
    val value: String
)

data class RatingItem(
    val id: Int,
    val quantity: Int,
    val price: String,
    val total: String,
    @SerializedName("itemdetails_ID") val itemDetailsId: Int,
    @SerializedName("suborders_ID") val subordersId: Int,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("item_name") val itemName: String? = null,
    @SerializedName("item_detail_id") val itemDetailId: Int? = null,
    @SerializedName("item_description") val itemDescription: String? = null,
    val timesensitive: String? = null,
    @SerializedName("preparation_time") val preparationTime: Int? = null,
    @SerializedName("itemPicture") val itemPicture: String? = null,
    @SerializedName("variation_name") val variationName: String? = null,
    @SerializedName("menu_price") val menuPrice: String? = null,
    @SerializedName("additional_info") val additionalInfo: String? = null,
    @SerializedName("item_category_id") val itemCategoryId: Int? = null,
    @SerializedName("item_category_name") val itemCategoryName: String? = null,
    val attributes: List<RatingItemAttribute> = emptyList(),
    @SerializedName("error_message") val errorMessage: String? = null
)

data class RatingOrderResponse(
    val suborder: RatingSubOrder,
    @SerializedName("delivery_boy_info") val deliveryBoyInfo: DeliveryBoyInfo,
    val items: List<RatingItem>
)
