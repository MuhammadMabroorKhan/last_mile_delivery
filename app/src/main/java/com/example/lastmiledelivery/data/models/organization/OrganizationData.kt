package com.example.lastmiledelivery.data.models.organization

import com.google.gson.annotations.SerializedName

data class OrganizationData(
    @SerializedName("organization_id") val organizationId: Int,
    val name: String,
    val email: String,
    @SerializedName("phone_no") val phoneNo: String,
    val cnic: String,
    val password: String,
    @SerializedName("lmd_user_role") val userRole: String,
    @SerializedName("profile_picture") val profilePicture: String
)


//SUmmary
data class OrganizationStats(
    @SerializedName("total_delivery_boys") val totalDeliveryBoys: Int,
    @SerializedName("total_vendors") val totalVendors: Int,
    @SerializedName("vendor_approval_status") val vendorApprovalStatus: VendorApprovalStatus,
    @SerializedName("total_delivered_orders") val totalDeliveredOrders: Int
)

data class VendorApprovalStatus(
    val pending: Int,
    val approved: Int,
    val rejected: Int
)

