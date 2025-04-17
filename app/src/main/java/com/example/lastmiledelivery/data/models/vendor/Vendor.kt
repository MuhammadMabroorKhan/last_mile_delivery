package com.example.lastmiledelivery.data.models.vendor

import com.google.gson.annotations.SerializedName

data class Vendor(
    val name: String,
    val email: String,
    val phone_no: String,
    val password: String,
    val cnic: String,
    val profile_picture: String?, // Nullable
    val vendor_type: String,
    val address_type: String,
    val street: String,
    val city: String,
    val zip_code: String?,
    val country: String?,
    val latitude: Double?,
    val longitude: Double?
)


data class VendorSignupResponse(
    val message: String,
    val user_id: Int?
)

//get Vendor data  (also for venortype and according to vendor type switch to screen)
data class VendorResponse(
    @SerializedName("lmd_user_id") val lmdUserId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone_no") val phoneNo: String,
    @SerializedName("cnic") val cnic: String,
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("vendor_type") val vendorType: String,
    @SerializedName("profile_picture") val profilePicture: String?
)


//Fetch Shops
data class ShopResponse(
    val shops: List<Shop>?
)

data class Shop(
    val id: Int,
    val name: String,
    val description: String?,
    val status: String,
    val shopcategory_ID: Int,
    val shopcategory_name: String,
    val vendors_ID: Int
)


//Shop Creation and response
data class ShopRequest(
    val name: String,
    val description: String?,
    val shopcategory_ID: Int,
    val vendors_ID: Int
)

data class ShopCreationResponse(
    val success: Boolean,
    val shop: ShopData?,
    val errors: Map<String, List<String>>?
)

data class ShopData(
    val id: Int,
    val name: String,
    val description: String?,
    val shopcategory_ID: Int,
    val vendors_ID: Int
)


//Branch
data class Branch(
    val branch_id: Int,
    val latitude: String,
    val longitude: String,
    val description: String,
    val opening_hours: String,
    val closing_hours: String,
    val contact_number: String,
    val approval_status: String,
    val branch_picture: String?, // Can be null
    val status: String,
    val area_ID: Int
)
data class BranchesResponse(
    val branches: List<Branch>?
)