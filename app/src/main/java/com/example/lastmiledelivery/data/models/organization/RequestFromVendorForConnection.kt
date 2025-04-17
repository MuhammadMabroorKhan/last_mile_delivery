package com.example.lastmiledelivery.data.models.organization

import com.google.gson.annotations.SerializedName

data class VendorRequestOrganizationResponse(
    @SerializedName("pending_requests") val pendingRequests: List<VendorConnectionRequest>,
    @SerializedName("approved_requests") val approvedRequests: List<VendorConnectionRequest>,
    @SerializedName("rejected_requests") val rejectedRequests: List<VendorConnectionRequest>
)

data class VendorConnectionRequest(
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("approval_status") val approvalStatus: String,
    @SerializedName("status") val status: String,
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("vendor_name") val vendorName: String,
    @SerializedName("vendor_email") val vendorEmail: String,
    @SerializedName("vendor_phone") val vendorPhone: String,
    @SerializedName("vendor_profile_picture") val vendorProfilePicture: String?,
    @SerializedName("organization_id") val organizationId: Int,
    @SerializedName("org_user_name") val orgUserName: String,
    @SerializedName("org_user_email") val orgUserEmail: String
)



data class SimpleResponse(
    val message: String
)


//GET REJSTION REASONS

data class RejectionReasonResponse(
    @SerializedName("rejection_reasons") val rejectionReasons: List<VendorOrganizationRejectionReason>?
)
data class VendorOrganizationRejectionReason(
    val id: Int,
    val reason: String,
    val status: String,
    val vendor_ID: Int,
    val organization_ID: Int
)


data class MessageResponse(
    val message: String
)


data class RejectVendorRequestBody(
    val rejection_reasons: List<String>
)

