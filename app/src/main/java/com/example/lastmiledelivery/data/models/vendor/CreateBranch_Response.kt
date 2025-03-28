package com.example.lastmiledelivery.data.models.vendor

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class BranchRequest(
    val latitude: Double,
    val longitude: Double,
    val description: String?,
    @SerializedName("opening_hours") val openingHours: String,
    @SerializedName("closing_hours") val closingHours: String,
    @SerializedName("contact_number") val contactNumber: String,
    @SerializedName("city_ID") val cityId: Int,
    @SerializedName("area_name") val areaName: String,
    @SerializedName("postal_code") val postalCode: String?,
    @SerializedName("shops_ID") val shopsId: Int
)

data class CreateBranchResponse(
    val message: String,
    val data: BranchData?
)

data class BranchData(
    val latitude: Double,
    val longitude: Double,
    val description: String?,
    @SerializedName("opening_hours") val openingHours: String,
    @SerializedName("closing_hours") val closingHours: String,
    @SerializedName("contact_number") val contactNumber: String,
    @SerializedName("city_ID") val cityId: Int,
    @SerializedName("area_name") val areaName: String,
    @SerializedName("postal_code") val postalCode: String?,
    @SerializedName("shops_ID") val shopsId: Int,
    @SerializedName("branch_picture") val branchPicture: String?,
    @SerializedName("area_ID") val areaId: Int
)

data class ErrorResponse(
    val message: String,
    val errors: Map<String, List<String>>?
)


//Update Branch
data class UpdateBranchRequest(
    val latitude: RequestBody,
    val longitude: RequestBody,
    val description: RequestBody?,
    val opening_hours: RequestBody,
    val closing_hours: RequestBody,
    val contact_number: RequestBody,
    val city_ID: RequestBody,
    val area_name: RequestBody,
    val postal_code: RequestBody?,
    val shops_ID: RequestBody,
    val branch_picture: MultipartBody.Part?
)



data class UpdateBranchResponse(
    val message: String,
    val data: UpdateBranch?
)


data class UpdateBranch(
    val id: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("description") val description: String?,
    @SerializedName("opening_hours") val openingHours: String,
    @SerializedName("closing_hours") val closingHours: String,
    @SerializedName("contact_number") val contactNumber: String?,
    @SerializedName("city_ID") val cityId: Int,
    @SerializedName("area_name") val areaName: String,
    @SerializedName("postal_code") val postalCode: String?,
    @SerializedName("shops_ID") val shopsId: Int,
    @SerializedName("branch_picture") val branchPicture: String?
)




data class ToggleBranchResponse(
    val message: String,
    val new_status: String
)
