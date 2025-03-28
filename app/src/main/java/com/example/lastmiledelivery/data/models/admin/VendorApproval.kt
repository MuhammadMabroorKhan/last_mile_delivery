package com.example.lastmiledelivery.data.models.admin


import com.google.gson.annotations.SerializedName

data class VendorApproval(
    @SerializedName("lmd_user_id") val userId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone_no") val phoneNo: String,
    @SerializedName("cnic") val cnic: String,
    @SerializedName("profile_picture") val profilePicture: String?,
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("vendor_type") val vendorType: String,
    @SerializedName("approval_status") val approvalStatus: String
)


//Accept Vendor
data class MessageResponse(
    val message: String
)

//Reject Vendor
data class RejectVendorRequest(
    val rejection_reasons: List<String>
)


data class RejectionReason(
    val id: Int,
    val reason: String,
    val status: String
)

data class CorrectRejectionRequest(
    val rejection_reason_id: Int
)

//get Pending branches
data class PendingBranchesResponse(
    @SerializedName("pending_branches") val pendingBranches: List<PendingBranch>
)

data class PendingBranch(
    @SerializedName("branch_id") val branchId: Int,
    @SerializedName("branch_description") val branchDescription: String,
    @SerializedName("branch_picture") val branchPicture: String,
    @SerializedName("branch_approval_status") val branchApprovalStatus: String,
    @SerializedName("shop_id") val shopId: Int,
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("shop_category_id") val shopCategoryId: Int,
    @SerializedName("shop_category") val shopCategory: String,
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("vendor_type") val vendorType: String,
    @SerializedName("vendor_approval_status") val vendorApprovalStatus: String,
    @SerializedName("lmd_user_id") val lmdUserId: Int,
    @SerializedName("vendor_name") val vendorName: String,
    @SerializedName("vendor_email") val vendorEmail: String,
    @SerializedName("vendor_profile_picture") val vendorProfilePicture: String,
    @SerializedName("area") val area: String,
    @SerializedName("city") val city: String
)

data class RejectBranchRequest(
    val rejection_reasons: List<String>
)
