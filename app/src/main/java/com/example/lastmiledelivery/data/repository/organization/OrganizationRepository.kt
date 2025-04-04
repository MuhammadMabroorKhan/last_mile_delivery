package com.example.lastmiledelivery.data.repository.organization

import com.example.lastmiledelivery.data.models.customer.ApiException
import com.example.lastmiledelivery.data.models.customer.CustomerSignupResponse
import com.example.lastmiledelivery.data.models.organization.OrganizationSignupResponse
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
}