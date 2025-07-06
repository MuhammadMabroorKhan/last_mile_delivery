package com.example.lastmiledelivery.data.repository.admin

import android.util.Log
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
import com.example.lastmiledelivery.data.models.admin.LmdSettingResponse
import com.example.lastmiledelivery.data.models.admin.MethodsTemplateResponse
import com.example.lastmiledelivery.data.models.admin.PendingBranch
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
import com.example.lastmiledelivery.data.remote.api.AdminApiService
import retrofit2.Response
import javax.inject.Inject

class VendorApprovalRepository @Inject constructor(
    private val apiService: AdminApiService
) {
    suspend fun getVendors(): List<VendorApproval> {
        return apiService.getVendorsApproval()
    }

    suspend fun approveVendor(vendorId: Int): Result<String> {
        return try {
            val response = apiService.approveVendor(vendorId)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Vendor approved successfully!")
            } else {
                Result.failure(Exception("Failed to approve vendor"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectVendor(vendorId: Int, reasons: List<String>): Result<String> {
        return try {
            val response = apiService.rejectVendor(vendorId, RejectVendorRequest(reasons))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception("Failed to reject vendor"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRejectionReasons(vendorId: Int): Result<List<RejectionReason>> {
        return try {
            val response = apiService.getRejectionReasons(vendorId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(
                    Exception(
                        response.errorBody()?.string() ?: "Failed to fetch reasons"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun correctRejectionReason(vendorId: Int, reasonId: Int): Result<String> {
        return try {
            val response =
                apiService.correctRejectionReason(vendorId, CorrectRejectionRequest(reasonId))
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Rejection corrected successfully!")
            } else {
                Result.failure(
                    Exception(
                        response.errorBody()?.string() ?: "Failed to correct reason"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPendingBranches(): Result<List<PendingBranch>> {
        return try {
            val response = apiService.getPendingBranches()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.pendingBranches)
            } else {
                Result.failure(Exception("Failed to fetch pending branches"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun approveBranch(branchId: Int): Result<String> {
        return try {
            val response = apiService.approveBranch(branchId)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Branch approved successfully!")
            } else {
                Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectBranch(branchId: Int, rejectionReasons: List<String>): Result<String> {
        return try {
            val response = apiService.rejectBranch(branchId, RejectBranchRequest(rejectionReasons))
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Branch rejected successfully!")
            } else {
                Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBranchRejectionReasons(branchId: Int): Result<List<RejectionReason>> {
        return try {
            val response = apiService.getBranchRejectionReasons(branchId)
            Log.d("RejectionReasonsINREpo", "Fetched Reasons: ${response.body()}")
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())

            } else {
                Result.failure(Exception("Failed to fetch rejection reasons"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun correctBranchRejectionReason(branchId: Int, reasonId: Int): Result<String> {
        return try {
            val response =
                apiService.correctBranchRejectionReason(branchId, CorrectRejectionRequest(reasonId))
            Log.d("RejectionReasonsINREpo", "Fetched Reasons: ${response.body()}")
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Correction successful")
            } else {
                Result.failure(Exception("Failed to correct rejection reason"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    ///////// REISTER WENSITE FOR API VENDOR
    suspend fun getApiVendors(): List<ApiVendorRegisterWebsite> {
        val response = apiService.getApiVendors()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch vendors")
        }
    }

    suspend fun fetchIntegrationDetails(branchId: Int): IntegrationResponse {
        return apiService.getIntegrationDetails(branchId)
    }


    suspend fun addApiVendor(request: ApiVendorRequest): Result<ApiVendorResponse> {
        return try {
            val response = apiService.storeApiVendor(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateApiVendor(id: Int, request: ApiVendorRequest): ApiVendorResponse {
        val response = apiService.updateApiVendor(id, request)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response")
        } else {
            throw Exception(response.errorBody()?.string() ?: "Unknown error")
        }
    }

    suspend fun getApiVendor(branchId: Int): GetApiVendorResponse? {
        return try {
            val response = apiService.getApiVendorByBranch(branchId)
            if (response.isSuccessful) {
                response.body()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getStandardApiMethods(): Response<MethodsTemplateResponse> {
        return apiService.getStandardApiMethods()
    }

    suspend fun saveApiMethods(
        apiVendorId: Int,
        request: SaveApiMethodsRequest
    ): Response<SaveApiMethodResponse> {
        return apiService.saveApiMethods(apiVendorId, request)
    }


    suspend fun updateApiMethod(methodId: Int, request: UpdateApiMethodRequest): SaveApiMethodResponse {
        val response = apiService.updateApiMethod(methodId, request)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response from server")
        } else {
            throw Exception(response.errorBody()?.string() ?: "Unknown error")
        }
    }


    suspend fun getMethodsByVendor(vendorId: Int): VendorMethodResponse? {
        return try {
            val response = apiService.getMethodsByVendor(vendorId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }


    suspend fun getMappings(branchId: Int, vendorId: Int): ApiResponse<List<AddMapping>> {
        return apiService.getMappings(branchId, vendorId).body() ?: ApiResponse(false, "Error", null)
    }

    suspend fun getVariables(): ApiResponse<List<AddVariable>> {
        return apiService.getAllVariables().body() ?: ApiResponse(false, "Error", null)
    }

    suspend fun saveMappings(request: SaveMappingRequest): ApiResponse<List<AddMapping>> {
        return apiService.saveVariableMappings(request).body() ?: ApiResponse(false, "Error", null)
    }

    suspend fun updateMapping(id: Int, newValue: String): ApiResponse<AddMapping> {
        return apiService.updateMapping(id, UpdateMappingRequest(newValue))
    }

    //Add New Variable by Admin
    suspend fun addVariable(tag: String): Response<GenericResponse> {
        return apiService.addVariable(AddVariableRequest(tag))
    }


    //Add new methods by Admin
    suspend fun saveNewApiMethods(apivendorId: Int, methods: List<ApiMethodRequest>): ApiMethodResponse {
        val body = ApiMethodRequestWrapper(methods = methods)
        val response = apiService.saveNewApiMethods(apivendorId, body)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response")
        } else {
            throw Exception(response.errorBody()?.string() ?: "Unknown error")
        }
    }

    //Summary and STats
    suspend fun getAdminStats(): AdminStatsResponse? {
        return try {
            val response = apiService.getAdminStats()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }


    // Lmd Setting and Earning
//    suspend fun getLmdSettings() = apiService.getLmdSettings()
    suspend fun getLmdSettings(): Response<LmdSettingResponse> {
        return apiService.getLmdSettings()
    }

    suspend fun updateOrderCharge(value: Double) = apiService.updateOrderCharge(mapOf("value" to value))
    suspend fun updateTaxPercentage(value: Double) = apiService.updateTaxPercentage(mapOf("value" to value))
    suspend fun updatePickupRadius(value: Double) = apiService.updatePickupRadius(mapOf("value" to value))
    suspend fun getLmdEarnings() = apiService.getLmdEarnings()

}










