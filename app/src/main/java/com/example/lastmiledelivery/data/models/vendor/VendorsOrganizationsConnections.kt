package com.example.lastmiledelivery.data.models.vendor

import com.google.gson.annotations.SerializedName

data class OrganizationResponse(
    @SerializedName("available_organizations") val availableOrganizations: List<AvailableOrganization>,
    @SerializedName("requested_or_connected_organizations") val requestedOrConnectedOrganizations: List<RequestedOrganization>
)

data class AvailableOrganization(
    @SerializedName("organization_id") val organizationId: Int,
    val name: String,
    val email: String,
    @SerializedName("phone_no") val phoneNo: String,
    val cnic: String,
    @SerializedName("lmd_user_role") val lmdUserRole: String,
    @SerializedName("profile_picture") val profilePicture: String?
)

data class RequestedOrganization(
    @SerializedName("organization_id") val organizationId: Int,
    val name: String,
    val email: String,
    @SerializedName("phone_no") val phoneNo: String,
    val cnic: String,
    @SerializedName("lmd_user_role") val lmdUserRole: String,
    @SerializedName("approval_status") val approvalStatus: String,
    @SerializedName("profile_picture") val profilePicture: String?
)





//Vendor Organization COnnection Request and ersponse
data class ConnectVendorRequest(
    @SerializedName("vendor_ID") val vendorId: Int,
    @SerializedName("organization_ID") val organizationId: Int
)

data class ConnectVendorResponse(
    val success: Boolean? = null, // In case of validation errors
    val errors: Map<String, List<String>>? = null, // Validation
    val message: String? = null, // Success or failure message
    val error: String? = null // Exception error (500)
)

