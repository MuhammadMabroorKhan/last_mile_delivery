package com.example.lastmiledelivery.data.repository.organization

import android.util.Log
import com.example.lastmiledelivery.data.models.customer.ApiException
import com.example.lastmiledelivery.data.models.customer.CustomerSignupResponse
import com.example.lastmiledelivery.data.models.organization.DeliveryBoy
import com.example.lastmiledelivery.data.models.organization.DeliveryBoyEarningsResponse
import com.example.lastmiledelivery.data.models.organization.DeliveryBoySignupResponse
import com.example.lastmiledelivery.data.models.organization.OrgEarningsResponse
import com.example.lastmiledelivery.data.models.organization.OrganizationData
import com.example.lastmiledelivery.data.models.organization.OrganizationSignupResponse
import com.example.lastmiledelivery.data.models.organization.OrganizationStats
import com.example.lastmiledelivery.data.models.organization.RejectVendorRequestBody
import com.example.lastmiledelivery.data.models.organization.SimpleResponse
import com.example.lastmiledelivery.data.models.organization.VendorOrganizationRejectionReason
import com.example.lastmiledelivery.data.models.organization.VendorRequestOrganizationResponse
import com.example.lastmiledelivery.data.remote.api.CustomerApiService
import com.example.lastmiledelivery.data.remote.api.OrganizationApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import javax.inject.Inject


class OrganizationRepository @Inject constructor(private val api: OrganizationApiService) {
    suspend fun organizationSignup(
        name: RequestBody, email: RequestBody, phoneNo: RequestBody,
        password: RequestBody, cnic: RequestBody, addressType: RequestBody,
        street: RequestBody, city: RequestBody, zipCode: RequestBody?,
        country: RequestBody, latitude: RequestBody?, longitude: RequestBody?,
        profilePicture: MultipartBody.Part?
    ): Result<OrganizationSignupResponse> {
        return try {
            val response = api.organizationSignup(
                name,
                email,
                phoneNo,
                password,
                cnic,
                addressType,
                street,
                city,
                zipCode,
                country,
                latitude,
                longitude,
                profilePicture
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Result.success(responseBody)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = extractErrorMessage(errorBody)
                Result.failure(ApiException(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractErrorMessage(errorBody: String?): String {
        return try {
            val jsonObject = JSONObject(errorBody ?: "{}")
            val errors = jsonObject.optJSONObject("errors")
            errors?.keys()?.asSequence()?.joinToString(", ") { key ->
                "${key.capitalize()}: ${errors.getJSONArray(key).join(", ")}"
            } ?: jsonObject.optString("message", "Unknown error")
        } catch (e: Exception) {
            "Error parsing response"
        }
    }


    suspend fun deliveryBoySignup(
        name: RequestBody,
        email: RequestBody,
        phoneNo: RequestBody,
        password: RequestBody,
        cnic: RequestBody,
        profilePicture: MultipartBody.Part,
        licenseNo: RequestBody,
        licenseExpDate: RequestBody?,
        licenseFront: MultipartBody.Part,
        licenseBack: MultipartBody.Part,
        addressType: RequestBody,
        street: RequestBody,
        city: RequestBody,
        zipCode: RequestBody?,
        country: RequestBody,
        latitude: RequestBody?,
        longitude: RequestBody?,
        organizationId: RequestBody?
    ): Result<DeliveryBoySignupResponse> {
        return try {
            val response = api.deliveryBoySignup(
                name, email, phoneNo, password, cnic, profilePicture,
                licenseNo, licenseExpDate, licenseFront, licenseBack,
                addressType, street, city, zipCode, country,
                latitude, longitude, organizationId
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = extractErrorMessage(errorBody)
                Result.failure(ApiException(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getOrganizationData(id: Int): OrganizationData? {
        val response = api.getOrganizationData(id)
        if (response.isSuccessful) {
            return response.body()
        } else if (response.code() == 404) {
            throw Exception("Organization not found")
        } else {
            throw Exception("Failed to fetch organization data")
        }
    }

    suspend fun getDeliveryBoysByOrganization(orgId: Int): Result<List<DeliveryBoy>> {
        return try {
            val response = api.getDeliveryBoys(orgId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.deliveryBoys)
            } else {
                Result.success(emptyList()) // Return empty list safely
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchVendorRequests(orgId: Int): Result<VendorRequestOrganizationResponse> {
        return try {
            val response = api.getVendorRequests(orgId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptVendorRequest(requestId: Int): Result<SimpleResponse> {
        return try {
            val response = api.acceptVendorRequest(requestId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRejectionReasons(organizationId: Int): Result<List<VendorOrganizationRejectionReason>> {
        return try {
            val response = api.getRejectionReasons(organizationId)
            if (response.isSuccessful && response.body()?.rejectionReasons != null) {
                Result.success(response.body()!!.rejectionReasons!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun correctRejectionReason(reasonId: Int): Result<String> {
        return try {
            val response = api.correctRejectionReason(reasonId)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Correction successful")
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Correction failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


//    suspend fun rejectVendorRequest(requestId: Int, reasons: List<String>): Result<String> {
//        return try {
//            val response = api.rejectVendorRequest(requestId, mapOf("rejection_reasons" to reasons))
//            if (response.isSuccessful) {
//                Result.success(response.body()?.message ?: "Rejected successfully")
//            } else {
//                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to reject"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }


    suspend fun rejectVendorRequest(requestId: Int, reasons: List<String>): Result<String> {
        return try {
            val response = api.rejectVendorRequest(
                requestId,
                RejectVendorRequestBody(rejection_reasons = reasons)
            )
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Rejected successfully")
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to reject"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    //Summary stats
//    suspend fun fetchOrganizationStats(orgId: Int): Result<OrganizationStats> {
//        return try {
//            val response = api.getOrganizationStats(orgId)
//            if (response.isSuccessful) {
//                Result.success(response.body()!!)
//            } else {
//                Result.failure(Exception("Failed to load stats"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    suspend fun fetchOrganizationStats(orgId: Int): Result<OrganizationStats> {
        val response = api.getOrganizationStats(orgId)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return Result.success(body)
            } else {
                Log.e("fetchStats", "Response body is null!")
                throw Exception("Failed to load stats: Body is null")
            }
        } else {
            Log.e(
                "fetchStats",
                "Response not successful: code=${response.code()}, error=${
                    response.errorBody()?.string()
                }"
            )
            throw Exception("Failed to load stats: HTTP ${response.code()}")
        }
    }



    suspend fun getOrganizationEarnings(orgId: Int): Result<OrgEarningsResponse> = try {
        val response = api.getOrganizationEarnings(mapOf("organization_id" to orgId))
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else Result.failure(Exception("${response.code()} ${response.message()}"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getDeliveryBoyEarnings(orgId: Int, deliveryBoyId: Int): Result<DeliveryBoyEarningsResponse> = try {
        val response = api.getDeliveryBoyEarnings(
            mapOf("organization_id" to orgId, "deliveryboy_id" to deliveryBoyId)
        )
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else Result.failure(Exception("${response.code()} ${response.message()}"))
    } catch (e: Exception) {
        Result.failure(e)
    }

}