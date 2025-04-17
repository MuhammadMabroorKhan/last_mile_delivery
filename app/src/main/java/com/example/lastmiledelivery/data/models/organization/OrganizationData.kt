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

