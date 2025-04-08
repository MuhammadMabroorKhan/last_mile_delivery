package com.example.lastmiledelivery.data.models.vendor

data class VendorOrdersResponse(
    val orders: List<VendorOrder>? = null,
    val message: String? = null
)

data class VendorOrder(
    val order_id: Int,
    val order_date: String,
    val order_status: String,
    val payment_status: String,
    val payment_method: String,
    val total_amount: String,
    val customer: VendorCustomerInfo,
    val suborders: List<VendorSuborder>
)

data class VendorCustomerInfo(
    val name: String,
    val email: String,
    val phone_no: String,
    val picture: String,
    val address: CustomerAddressInfo
)

data class CustomerAddressInfo(
    val type: String,
    val street: String,
    val latitude: Double,
    val longitude: Double
)

data class VendorSuborder(
    val suborder_id: Int,
    val vendor_order_id:Int?,
    val status: String,
    val payment_status: String,
    val total: String,
    val vendor_type: String,
    val shop_id: Int,
    val branch_id: Int
)



////////Vendor SUBORDER DETAIL
data class VendorSuborderDetailResponse(
    val message: String? = null,
    val suborder_info: VendorSuborderDetailInfo? = null,
    val order_detail_info: List<VendorOrderDetailInfo>? = null
)

data class VendorSuborderDetailInfo(
    val suborder_id: Int,
    val status: String,
    val payment_status: String,
    val total_amount: String,
    val estimated_delivery_time: String?,
    val delivery_time: String?,
    val deliveryboy_id: Int?,
    val vendor_type: String,
    val vendor_order_id: String?
)



data class VendorOrderDetailInfo(
    val order_detail_id: Int,
    val quantity: Int,
    val order_detail_price: String,
    val order_detail_total: String,
    val item: VendorItemDetailInfo
)

data class VendorItemDetailInfo(
    val item_id: Int,
    val item_name: String,
    val item_description: String,
    val item_detail_id: Int,
    val variation_name: String,
    val item_detail_price: String,
    val additional_info: String,
    val item_picture: String,
    val attributes: List<Any>,
    val timesensitive: String,
    val preparation_time: Int,
    val item_category_id: Int,
    val item_category_name: String,
    val error_message: String?
)
