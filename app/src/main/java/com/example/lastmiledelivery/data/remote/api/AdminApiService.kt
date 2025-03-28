package com.example.lastmiledelivery.data.remote.api

import com.example.lastmiledelivery.data.models.Cities
import com.example.lastmiledelivery.data.models.admin.CorrectRejectionRequest
import com.example.lastmiledelivery.data.models.admin.MessageResponse
import com.example.lastmiledelivery.data.models.admin.PendingBranchesResponse
import com.example.lastmiledelivery.data.models.admin.RejectBranchRequest
import com.example.lastmiledelivery.data.models.admin.RejectVendorRequest
import com.example.lastmiledelivery.data.models.admin.RejectionReason
import com.example.lastmiledelivery.data.models.admin.VendorApproval
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AdminApiService {
    @GET("api/admin/vendors")
    suspend fun getVendorsApproval(): List<VendorApproval>

    @PUT("api/vendors/{id}/approve")
    suspend fun approveVendor(@Path("id") vendorId: Int): Response<MessageResponse>

    @POST("api/admin/vendors/{id}/reject")
    suspend fun rejectVendor(
        @Path("id") vendorId: Int,
        @Body requestBody: RejectVendorRequest
    ): Response<MessageResponse>

    @GET("api/admin/vendors/{vendorId}/rejection-reasons")
    suspend fun getRejectionReasons(@Path("vendorId") vendorId: Int): Response<List<RejectionReason>>

    @POST("api/admin/vendors/{vendorId}/correct-rejection")
    suspend fun correctRejectionReason(
        @Path("vendorId") vendorId: Int,
        @Body request: CorrectRejectionRequest
    ): Response<MessageResponse>

    @GET("api/admin/branches/pendingBranches")
    suspend fun getPendingBranches(): Response<PendingBranchesResponse>

    @PUT("api/admin/branches/{branchId}/approve")
    suspend fun approveBranch(@Path("branchId") branchId: Int): Response<MessageResponse>

    @PUT("api/admin/branches/{branchId}/reject")
    suspend fun rejectBranch(
        @Path("branchId") branchId: Int,
        @Body requestBody: RejectBranchRequest
    ): Response<MessageResponse>


    @GET("api/admin/branches/{branchId}/rejection-reasons")
    suspend fun getBranchRejectionReasons(@Path("branchId") branchId: Int): Response<List<RejectionReason>>

    @POST("api/admin/branches/{branchId}/correct-rejection-reason")
    suspend fun correctBranchRejectionReason(
        @Path("branchId") branchId: Int,
        @Body request: CorrectRejectionRequest
    ): Response<MessageResponse>

    @GET("api/cities")
    suspend fun getAllCities():Response<List<Cities>>
}