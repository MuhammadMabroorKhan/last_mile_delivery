package com.example.lastmiledelivery.data.repository.admin

import android.util.Log
import com.example.lastmiledelivery.data.models.admin.ApiVendorRegisterWebsite
import com.example.lastmiledelivery.data.models.admin.CorrectRejectionRequest
import com.example.lastmiledelivery.data.models.admin.PendingBranch
import com.example.lastmiledelivery.data.models.admin.RejectBranchRequest
import com.example.lastmiledelivery.data.models.admin.RejectVendorRequest
import com.example.lastmiledelivery.data.models.admin.RejectionReason
import com.example.lastmiledelivery.data.models.admin.VendorApproval
import com.example.lastmiledelivery.data.remote.api.AdminApiService
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
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch reasons"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun correctRejectionReason(vendorId: Int, reasonId: Int): Result<String> {
        return try {
            val response = apiService.correctRejectionReason(vendorId, CorrectRejectionRequest(reasonId))
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Rejection corrected successfully!")
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to correct reason"))
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
            val response = apiService.correctBranchRejectionReason(branchId, CorrectRejectionRequest(reasonId))
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
}










