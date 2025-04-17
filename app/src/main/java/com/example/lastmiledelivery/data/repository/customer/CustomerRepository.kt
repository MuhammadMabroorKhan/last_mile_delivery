package com.example.lastmiledelivery.data.repository.customer

import android.util.Log
import com.example.lastmiledelivery.data.models.StatusesResponse
import com.example.lastmiledelivery.data.models.customer.AddCartResponse
import com.example.lastmiledelivery.data.models.customer.AddToCartRequest
import com.example.lastmiledelivery.data.models.customer.Address
import com.example.lastmiledelivery.data.models.customer.ApiException
import com.example.lastmiledelivery.data.models.customer.CartResponse
import com.example.lastmiledelivery.data.models.customer.CategoryResponse
import com.example.lastmiledelivery.data.models.customer.ClearCartRequest
import com.example.lastmiledelivery.data.models.customer.ClearCartResponse
import com.example.lastmiledelivery.data.models.customer.CustomerData
import com.example.lastmiledelivery.data.models.customer.CustomerMainScreenResponse
import com.example.lastmiledelivery.data.models.customer.CustomerOrdersResponse
import com.example.lastmiledelivery.data.models.customer.CustomerSignupResponse
import com.example.lastmiledelivery.data.models.customer.GenericResponse
import com.example.lastmiledelivery.data.models.customer.LiveTrackingResponse
import com.example.lastmiledelivery.data.models.customer.MenuItem
import com.example.lastmiledelivery.data.models.customer.MenuResponse
import com.example.lastmiledelivery.data.models.customer.OrderDetailsResponse
import com.example.lastmiledelivery.data.models.customer.OrderRequest
import com.example.lastmiledelivery.data.models.customer.OrderResponse
import com.example.lastmiledelivery.data.models.customer.PaymentStatusResponse
import com.example.lastmiledelivery.data.models.customer.RouteInfoResponse
import com.example.lastmiledelivery.data.remote.api.CustomerApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject
import retrofit2.Response
import java.io.File

class CustomerRepository @Inject constructor(private val api: CustomerApiService) {
    suspend fun customerSignup(
        name: RequestBody, email: RequestBody, phoneNo: RequestBody,
        password: RequestBody, cnic: RequestBody, addressType: RequestBody,
        street: RequestBody, city: RequestBody, zipCode: RequestBody?,
        country: RequestBody, latitude: RequestBody?, longitude: RequestBody?,
        profilePicture: MultipartBody.Part?
    ): Result<CustomerSignupResponse> {
        return try {
            val response = api.customerSignup(
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


    suspend fun getCustomerData(id: Int): CustomerData? {
        val response = api.getCustomerData(id)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun getCustomerMainScreen(customerId: Int): Response<List<CustomerMainScreenResponse>> {
        return api.getCustomerMainScreen(customerId)
    }


    suspend fun getCategories(
        vendorId: Int,
        shopId: Int,
        branchId: Int
    ): Result<List<CategoryResponse>> {
        return try {
            val response = api.getCategories(vendorId, shopId, branchId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No categories found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getVendorMenu(vendorId: Int, shopId: Int, branchId: Int): Result<MenuResponse> {
        return try {
            val response = api.getVendorMenu(vendorId, shopId, branchId)

            if (response.isSuccessful) {
                val jsonElement = response.body()

                jsonElement?.let {
                    when {
                        it.isJsonArray -> {  // ✅ Handle list of items
                            val menuItems: List<MenuItem> =
                                Gson().fromJson(it, object : TypeToken<List<MenuItem>>() {}.type)
                            Result.success(MenuResponse(items = menuItems))
                        }

                        it.isJsonObject && it.asJsonObject.has("error") -> {  // ✅ Handle error object
                            val errorMessage = it.asJsonObject.get("error").asString
                            Result.success(MenuResponse(error = errorMessage))
                        }

                        else -> {
                            Result.failure(Exception("Unexpected response format"))
                        }
                    }
                } ?: Result.failure(Exception("Null response body"))

            } else {
                Result.failure(Exception("API call failed: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updateCustomer(
        customerId: Int,
        name: String?,
        email: String?,
        phoneNo: String?,
        password: String?,
        cnic: String?,
        profilePicture: MultipartBody.Part?
    ): Result<GenericResponse> {
        return try {
            Log.d("UpdateCustomerRepo", "Sending Update Request for Customer ID: $customerId")
            Log.d("UpdateCustomerRepo", "Name: $name, Email: $email, Phone No: $phoneNo")
            Log.d("UpdateCustomerRepo", "Password: $password, CNIC: $cnic")
            Log.d(
                "UpdateCustomerRepo",
                "Profile Picture Part: ${profilePicture?.body?.contentLength()} bytes"
            )

            val response = api.updateCustomer(
                customerId,
                name?.toRequestBody("text/plain".toMediaTypeOrNull()),
                email?.toRequestBody("text/plain".toMediaTypeOrNull()),
                phoneNo?.toRequestBody("text/plain".toMediaTypeOrNull()),
                password?.toRequestBody("text/plain".toMediaTypeOrNull()),
                cnic?.toRequestBody("text/plain".toMediaTypeOrNull()),
                profilePicture
            )

            if (response.isSuccessful) {
                Log.d("UpdateCustomerRepo", "API Response Success: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.e("UpdateCustomerRepo", "API Error Response: $errorBody")
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Log.e("UpdateCustomerRepo", "API Exception: ${e.localizedMessage}")
            Result.failure(e)
        }
    }


    suspend fun getCartDetails(customerId: Int): Result<CartResponse?> {
        return try {
            val response = api.getCartDetails(customerId)
            if (response.isSuccessful) {
                val cartResponse = response.body()
                if (cartResponse != null) {
                    Result.success(cartResponse)  // ✅ Success with actual cart data
                } else {
                    Result.success(null)  // ✅ Return null instead of failure
                }
            } else {
                Result.failure(Exception("Failed to fetch cart. Server returned error."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun addItemToCart(request: AddToCartRequest): Result<AddCartResponse> {
        return try {
            val response = api.addItemToCart(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun placeOrder(orderRequest: OrderRequest): Response<OrderResponse> {
        return api.placeOrder(orderRequest)
    }

    suspend fun getCustomerAddresses(customerId: Int): Result<List<Address>> {
        return try {
            val response = api.getCustomerAddresses(customerId)
            if (response.isSuccessful) {
                val addresses = response.body()?.addresses
                if (!addresses.isNullOrEmpty()) {
                    Result.success(addresses)
                } else {
                    Result.failure(Exception("No addresses found"))
                }
            } else {
                Result.failure(Exception("Error: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun clearCart(customerId: Int): Result<ClearCartResponse> {
        return try {
            val response = api.clearCart(ClearCartRequest(customerId))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCustomerOrders(customerId: Int): Response<CustomerOrdersResponse> {
        return api.getCustomerOrders(customerId)
    }

    suspend fun getOrderDetails(orderId: Int): OrderDetailsResponse? {
        try {
            val response = api.getOrderDetails(orderId) // Retrofit call to your API
            if (response.isSuccessful) {
                return response.body() // Return the response body if successful
            } else {
                // Handle the error case, return default values for missing data
                return OrderDetailsResponse(
                    message = "Order not found or no details available",
                    order_id = null, // or provide a default value if you want
                    order_date = null, // or provide a default value if you want
                    order_status = null, // or provide a default value if you want
                    order_total_amount = null, // or provide a default value if you want
                    suborders = emptyList() // or null, based on your design
                )
            }
        } catch (e: Exception) {
            // Handle any network or conversion errors and return default values
            return OrderDetailsResponse(
                message = "An error occurred: ${e.message}",
                order_id = null,
                order_date = null,
                order_status = null,
                order_total_amount = null,
                suborders = emptyList()
            )
        }
    }


    suspend fun fetchRouteInfo(suborderId: Int): RouteInfoResponse? {
        return try {
            val response = api.getRouteInfo(suborderId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }


    suspend fun confirmOrderDelivery(suborderId: Int): Result<String> {
        return try {
            val response = api.confirmOrderDelivery(suborderId)
            if (response.isSuccessful) {
                response.body()?.message?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty success response"))
            } else {
                val errorMsg = response.errorBody()?.string()
                Result.failure(Exception(errorMsg ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchPaymentStatus(suborderId: Int): PaymentStatusResponse? {
        return try {
            val response = api.getPaymentStatus(suborderId)
            if (response.isSuccessful) {
                response.body()
            } else {
                PaymentStatusResponse(null, null, "Error fetching payment status.")
            }
        } catch (e: Exception) {
            PaymentStatusResponse(null, null, e.message ?: "Unknown error")
        }
    }


    suspend fun confirmPaymentByCustomer(suborderId: Int): Result<String> {
        return try {
            val response = api.confirmPaymentByCustomer(suborderId)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Payment confirmed.")
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelOrder(orderId: Int): Result<String> {
        return try {
            val response = api.cancelOrder(orderId)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Order cancelled")
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLiveTracking(suborderId: Int): LiveTrackingResponse? {
        return try {
            val response = api.getLiveTracking(suborderId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}


