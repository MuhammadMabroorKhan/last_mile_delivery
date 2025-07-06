package com.example.lastmiledelivery.data.remote.api

import com.example.lastmiledelivery.data.models.Cities
import com.example.lastmiledelivery.data.models.admin.AddMapping
import com.example.lastmiledelivery.data.models.admin.AddVariable
import com.example.lastmiledelivery.data.models.admin.AddVariableRequest
import com.example.lastmiledelivery.data.models.admin.AdminStatsResponse
import com.example.lastmiledelivery.data.models.admin.ApiMethodRequest
import com.example.lastmiledelivery.data.models.admin.ApiMethodRequestWrapper
import com.example.lastmiledelivery.data.models.admin.ApiMethodResponse
import com.example.lastmiledelivery.data.models.admin.ApiResponse
import com.example.lastmiledelivery.data.models.admin.ApiVendorRegisterWebsite
import com.example.lastmiledelivery.data.models.admin.ApiVendorRequest
import com.example.lastmiledelivery.data.models.admin.ApiVendorResponse
import com.example.lastmiledelivery.data.models.admin.CorrectRejectionRequest
import com.example.lastmiledelivery.data.models.admin.GenericResponse
import com.example.lastmiledelivery.data.models.admin.GetApiVendorResponse
import com.example.lastmiledelivery.data.models.admin.IntegrationResponse
import com.example.lastmiledelivery.data.models.admin.LmdEarningsResponse
import com.example.lastmiledelivery.data.models.admin.LmdSetting
import com.example.lastmiledelivery.data.models.admin.LmdSettingResponse
import com.example.lastmiledelivery.data.models.admin.MessageResponse
import com.example.lastmiledelivery.data.models.admin.MethodsTemplateResponse
import com.example.lastmiledelivery.data.models.admin.PendingBranchesResponse
import com.example.lastmiledelivery.data.models.admin.RejectBranchRequest
import com.example.lastmiledelivery.data.models.admin.RejectVendorRequest
import com.example.lastmiledelivery.data.models.admin.RejectionReason
import com.example.lastmiledelivery.data.models.admin.SaveApiMethodResponse
import com.example.lastmiledelivery.data.models.admin.SaveApiMethodsRequest
import com.example.lastmiledelivery.data.models.admin.SaveMappingRequest
import com.example.lastmiledelivery.data.models.admin.UpdateApiMethodRequest
import com.example.lastmiledelivery.data.models.admin.UpdateMappingRequest
import com.example.lastmiledelivery.data.models.admin.VendorApproval
import com.example.lastmiledelivery.data.models.admin.VendorMethodResponse
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
    suspend fun getAllCities(): Response<List<Cities>>

    @GET("api/admin/api-vendors")
    suspend fun getApiVendors(): Response<List<ApiVendorRegisterWebsite>>

    @GET("api/integration-details/{branchId}")
    suspend fun getIntegrationDetails(@Path("branchId") branchId: Int): IntegrationResponse

    @POST("api/admin/apivendor/store")
    suspend fun storeApiVendor(
        @Body request: ApiVendorRequest
    ): Response<ApiVendorResponse>

    @PUT("api/admin/apivendor/{id}")
    suspend fun updateApiVendor(
        @Path("id") id: Int,
        @Body request: ApiVendorRequest
    ): Response<ApiVendorResponse>

    @GET("api/admin/api-vendor/{branchId}")
    suspend fun getApiVendorByBranch(
        @Path("branchId") branchId: Int
    ): Response<GetApiVendorResponse>


    @GET("api/admin/apimethod-templates")
    suspend fun getStandardApiMethods(): Response<MethodsTemplateResponse>

    @POST("api/admin/apivendor/{apivendorId}/methods")
    suspend fun saveApiMethods(
        @Path("apivendorId") apiVendorId: Int,
        @Body request: SaveApiMethodsRequest
    ): Response<SaveApiMethodResponse>

    @PUT("api/admin/apimethods/{id}")
    suspend fun updateApiMethod(
        @Path("id") methodId: Int,
        @Body request: UpdateApiMethodRequest
    ): Response<SaveApiMethodResponse>


    @GET("api/admin/apivendor/{apivendorId}/methods")
    suspend fun getMethodsByVendor(
        @Path("apivendorId") vendorId: Int
    ): Response<VendorMethodResponse>

    @GET("api/mappings/{branchId}/{apivendorId}")
    suspend fun getMappings(
        @Path("branchId") branchId: Int,
        @Path("apivendorId") apivendorId: Int
    ): Response<ApiResponse<List<AddMapping>>>

    @GET("api/variables")
    suspend fun getAllVariables(): Response<ApiResponse<List<AddVariable>>>

    @POST("api/admin/mappings/save")
    suspend fun saveVariableMappings(
        @Body request: SaveMappingRequest
    ): Response<ApiResponse<List<AddMapping>>>

    @PUT("api/admin/mapping/{id}")
    suspend fun updateMapping(
        @Path("id") id: Int,
        @Body request: UpdateMappingRequest
    ): ApiResponse<AddMapping>

    //Add Variable by Admin
    @POST("api/admin/add-variable")
    suspend fun addVariable(
        @Body request: AddVariableRequest
    ): Response<GenericResponse>

    //Add Method by Admin
    @POST("api/admin/save-new-api-methods/{apivendorId}")
    suspend fun saveNewApiMethods(
        @Path("apivendorId") apivendorId: Int,
        @Body body: ApiMethodRequestWrapper
    ): Response<ApiMethodResponse>


    //Admin Stats and summary
    @GET("api/admin/admin-stats")
    suspend fun getAdminStats(): Response<AdminStatsResponse>


    //Lmd Setting and Response
    @GET("api/admin/lmd-settings")
    suspend fun getLmdSettings(): Response<LmdSettingResponse>

    @POST("api/admin/lmd-settings/order-charge")
    suspend fun updateOrderCharge(@Body value: Map<String, Double>): Response<LmdSetting>

    @POST("api/admin/lmd-settings/tax-percentage")
    suspend fun updateTaxPercentage(@Body value: Map<String, Double>): Response<LmdSetting>

    @POST("api/admin/lmd-settings/pickup-radius")
    suspend fun updatePickupRadius(@Body value: Map<String, Double>): Response<LmdSetting>

    @GET("api/admin/lmd-earnings")
    suspend fun getLmdEarnings(): Response<LmdEarningsResponse>
}




