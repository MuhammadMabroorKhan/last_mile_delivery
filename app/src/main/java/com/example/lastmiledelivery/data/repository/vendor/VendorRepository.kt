package com.example.lastmiledelivery.data.repository.vendor


import android.util.Log
import com.example.lastmiledelivery.data.models.customer.ApiException
import com.example.lastmiledelivery.data.models.vendor.Branch
import com.example.lastmiledelivery.data.models.vendor.VendorResponse
import com.example.lastmiledelivery.data.models.vendor.VendorSignupResponse
import com.example.lastmiledelivery.data.remote.api.VendorApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class VendorRepository @Inject constructor(private val vendorApiService: VendorApiService) {

    suspend fun vendorSignup(
        name: RequestBody,
        email: RequestBody,
        phoneNo: RequestBody,
        password: RequestBody,
        cnic: RequestBody,
        profilePicture: MultipartBody.Part?,
        vendorType: RequestBody,
        addressType: RequestBody,
        street: RequestBody,
        city: RequestBody,
        zipCode: RequestBody?,
        country: RequestBody?,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): Result<VendorSignupResponse> {
        return try {
            val response = vendorApiService.vendorSignup(
                name, email, phoneNo, password, cnic, profilePicture,
                vendorType, addressType, street, city, zipCode, country, latitude, longitude
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

    suspend fun getVendorData(id: Int): Result<VendorResponse> {
        return try {
            val response = vendorApiService.getVendorData(id)
            Log.e("VendorRepository", "API response failed: ${response.errorBody()?.string()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("responseBody","${id} responseBody ${responseBody} ")
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
            Log.e("VendorRepository", "Network error: ${e.localizedMessage}", e)
            Result.failure(ApiException("Network error: ${e.localizedMessage}"))
        }
    }






}
