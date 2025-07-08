package com.example.lastmiledelivery.data.models.organization

data class DeliveryEarning(
    val suborder_id: Int,
    val deliveryboy_id: Int? = null, // Optional for deliveryboy response
    val distance_km: Double,
    val rate_per_km: Double,
    val total_earning: Double,
    val created_at: String // ISO datetime, format: "2025-07-08T14:30:00"
)

//data class OrgEarningsResponse(
//    val success: Boolean,
//    val organization_id: Int,
//    val total_earnings: Double,
//    val earnings: List<DeliveryEarning>,
//    val message: String? = null
//)
data class OrgEarningsResponse(
    val success: Boolean = false,
    val organization_id: Int = 0,
    val total_earnings: Double = 0.0,
    val earnings: List<DeliveryEarning> = emptyList(), // ✅ Default to empty list
    val message: String? = null
)


//data class DeliveryBoyEarningsResponse(
//    val success: Boolean,
//    val organization_id: Int,
//    val deliveryboy_id: Int,
//    val total_earnings: Double,
//    val earnings: List<DeliveryEarning>,
//    val message: String? = null
//)
data class DeliveryBoyEarningsResponse(
    val deliveryboy_id: Int = 0,
    val organization_id: Int = 0,
    val total_earnings: Double = 0.0,
    val earnings: List<DeliveryEarning> = emptyList()
)

data class ApiErrorResponse(
    val success: Boolean = false,
    val error: String
)
