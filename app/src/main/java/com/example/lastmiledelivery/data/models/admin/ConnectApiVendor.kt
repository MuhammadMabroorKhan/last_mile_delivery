package com.example.lastmiledelivery.data.models.admin

data class ApiVendorRegisterWebsite(
    val vendor_ID: Int,
    val vendor_type: String,
    val approval_status: String,
    val lmd_users_ID: Int,
    val name: String,
    val email: String,
    val phone_no: String,
    val cnic: String,
    val profile_picture: String?,
    val account_creation_date: String
)
