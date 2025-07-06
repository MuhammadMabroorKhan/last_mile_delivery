package com.example.lastmiledelivery.data.models.admin

data class LmdSetting(
    val id: Int,
    val order_charge: Double,
    val tax_percentage: Double,
    val pickup_radius_km: Double
)


data class LmdSettingResponse(
    val success: Boolean,
    val data: LmdSetting
)

// 📁 data/models/LmdEarning.kt
data class LmdEarning(
    val id: Int,
    val suborder_id: Int,
    val order_total: Double,
    val lmd_charge_percentage: Double,
    val lmd_earning_amount: Double,
    val tax_percentage: Double,
    val tax_amount: Double,
    val created_at: String,
    val updated_at: String
)


data class LmdEarningsResponse(
    val success: Boolean,
    val total_earning_after_tax: Double,
    val data: List<LmdEarning>
)