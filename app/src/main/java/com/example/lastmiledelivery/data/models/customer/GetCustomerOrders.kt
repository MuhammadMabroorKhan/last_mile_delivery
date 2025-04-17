package com.example.lastmiledelivery.data.models.customer

data class CustomerOrdersResponse(
    val success: Boolean,
    val message: String,
    val data: List<Order>?
)

data class Order(
    val id: Int,
    val order_date: String,
    val total_amount: String,
    val order_status: String,
    val order_type: String,
    val payment_status: String,
    val payment_method: String,
    val customers_ID: Int,
    val addresses_ID: Int,
    val created_at: String?,
    val updated_at: String?
)



/////Ordetails >> SUborders

data class OrderDetailsResponse(
    val message: String? = null,
    val order_id: String?,
    val order_date: String?,
    val order_status: String?,
    val order_total_amount: String?,
    val suborders: List<SubOrders>?
)

data class SubOrders(
    val suborder_id: Int,
    val suborder_status: String?,
    val suborder_payment_status: String?,
    val suborder_total_amount: String?,
    val vendor_type: String?,
    val vendor_order_id: String?,
    val estimated_delivery_time: String?,
    val delivery_time: String?,
    val deliveryboys_ID: String?,
    val vendor_ID: Int?,
    val shop_ID: Int?,
    val branch_ID: Int?,
    val suborder_created_at: String?,
    val suborder_updated_at: String?,
    val items: List<Item>?
)


data class Item(
    val item_detail_id: Int,
    val item_name: String?,
    val item_quantity: Int,
    val item_total: String?,
    val item_description: String?,
    val timesensitive: String?,
    val preparation_time: Int?,
    val itemPicture: String?,
    val variation_name: String?,
    val price: String?,
    val additional_info: String?,
    val item_category_id: Int?,
    val item_category_name: String?,
    val attributes: List<Attribute>?,
    val error_message: String?
)

//data class Attribute(
//    val key: String?,
//    val value: String?
//)



// ROUTES INFORMATION
data class RouteInfoResponse(
    val message: String,
    val data: RouteData?
)

data class RouteData(
    val pickup_location: LocationData,
    val drop_location: LocationData,
    val order_date: String?,
    val estimated_delivery_time: String?,
    val delivery_time: String?
)

data class LocationData(
    val latitude: Double,
    val longitude: Double
)


