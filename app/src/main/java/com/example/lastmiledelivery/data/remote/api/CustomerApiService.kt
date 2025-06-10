package com.example.lastmiledelivery.data.remote.api

import com.example.lastmiledelivery.data.models.StatusesResponse
import com.example.lastmiledelivery.data.models.customer.AddAddressRequest
import com.example.lastmiledelivery.data.models.customer.AddAddressResponse
import com.example.lastmiledelivery.data.models.customer.AddCartResponse
import com.example.lastmiledelivery.data.models.customer.AddToCartRequest
import com.example.lastmiledelivery.data.models.customer.AddressResponse
import com.example.lastmiledelivery.data.models.customer.CancelOrderResponse
import com.example.lastmiledelivery.data.models.customer.CartResponse
import com.example.lastmiledelivery.data.models.customer.CategoryResponse
import com.example.lastmiledelivery.data.models.customer.ClearCartRequest
import com.example.lastmiledelivery.data.models.customer.ClearCartResponse
import com.example.lastmiledelivery.data.models.customer.ConfirmDeliveryResponse
import com.example.lastmiledelivery.data.models.customer.ConfirmPaymentResponse
import com.example.lastmiledelivery.data.models.customer.CustomerData
import com.example.lastmiledelivery.data.models.customer.CustomerMainScreenResponse
import com.example.lastmiledelivery.data.models.customer.CustomerOrdersResponse
import com.example.lastmiledelivery.data.models.customer.CustomerSignupResponse
import com.example.lastmiledelivery.data.models.customer.DeliveryBoyRatingRequest
import com.example.lastmiledelivery.data.models.customer.DeliveryBoyRatingResponse
import com.example.lastmiledelivery.data.models.customer.GenericResponse
import com.example.lastmiledelivery.data.models.customer.GenericResponseIncreaseDecrease
import com.example.lastmiledelivery.data.models.customer.IncreaseDecreaseQuantityRequest
import com.example.lastmiledelivery.data.models.customer.ItemRatingResponse
import com.example.lastmiledelivery.data.models.customer.LiveRouteTrackingResponse
import com.example.lastmiledelivery.data.models.customer.LiveTrackingResponse
import com.example.lastmiledelivery.data.models.customer.MenuResponse
import com.example.lastmiledelivery.data.models.customer.OrderDetailsResponse
import com.example.lastmiledelivery.data.models.customer.OrderRequest
import com.example.lastmiledelivery.data.models.customer.OrderResponse
import com.example.lastmiledelivery.data.models.customer.PaymentStatusResponse
import com.example.lastmiledelivery.data.models.customer.RatingOrderResponse
import com.example.lastmiledelivery.data.models.customer.RatingsResponse
import com.example.lastmiledelivery.data.models.customer.RemoveCartItemRequest
import com.example.lastmiledelivery.data.models.customer.RemoveCartItemResponse
import com.example.lastmiledelivery.data.models.customer.RouteInfoResponse
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CustomerApiService {
    @Multipart
    @POST("api/signup") // Ensure this matches your Laravel API endpoint
    suspend fun customerSignup(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone_no") phoneNo: RequestBody,
        @Part("password") password: RequestBody,
        @Part("cnic") cnic: RequestBody,
        @Part("address_type") addressType: RequestBody,
        @Part("street") street: RequestBody,
        @Part("city") city: RequestBody,
        @Part("zip_code") zipCode: RequestBody?,
        @Part("country") country: RequestBody,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part profile_picture: MultipartBody.Part?
    ): Response<CustomerSignupResponse>

    @GET("api/customers/{id}")
    suspend fun getCustomerData(
        @Path("id") id: Int
    ): Response<CustomerData>

    @GET("api/customer/main-screen/{customerId}")
    suspend fun getCustomerMainScreen(@Path("customerId") customerId: Int): Response<List<CustomerMainScreenResponse>>

    @GET("api/vendor/{vendorId}/shop/{shopId}/branch/{branchId}/categories")
    suspend fun getCategories(
        @Path("vendorId") vendorId: Int,
        @Path("shopId") shopId: Int,
        @Path("branchId") branchId: Int
    ): Response<List<CategoryResponse>>


    @GET("api/vendor/{vendorId}/shop/{shopId}/branch/{branchId}/menu")
    suspend fun getVendorMenu(
        @Path("vendorId") vendorId: Int,
        @Path("shopId") shopId: Int,
        @Path("branchId") branchId: Int
    ): Response<JsonElement>   // ✅ Change to `Any` to handle both cases dynamically


    @Multipart
    @POST("api/customers/{id}")
    suspend fun updateCustomer(
        @Path("id") customerId: Int,
        @Part("name") name: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("phone_no") phoneNo: RequestBody?,
        @Part("password") password: RequestBody?,
        @Part("cnic") cnic: RequestBody?,
        @Part profilePicture: MultipartBody.Part? // File Upload
    ): Response<GenericResponse>


    @GET("api/cart/details")
    suspend fun getCartDetails(@Query("customer_id") customerId: Int): Response<CartResponse>


    @POST("api/cart/add-item")
    suspend fun addItemToCart(@Body request: AddToCartRequest): Response<AddCartResponse>

    @POST("api/customer/place-order")
    suspend fun placeOrder(@Body orderRequest: OrderRequest): Response<OrderResponse>

    @GET("api/customers/{customerId}/addresses")
    suspend fun getCustomerAddresses(@Path("customerId") customerId: Int): Response<AddressResponse>

    @POST("api/cart/clear")
    suspend fun clearCart(@Body request: ClearCartRequest): Response<ClearCartResponse>


    @POST("api/cart/remove-item")
    suspend fun removeItemFromCart(
        @Body request: RemoveCartItemRequest
    ): Response<RemoveCartItemResponse>

    @POST("api/cart/increase-quantity")
    suspend fun increaseCartItemQuantity(
        @Body request: IncreaseDecreaseQuantityRequest
    ): Response<GenericResponseIncreaseDecrease>

    @POST("api/cart/decrease-quantity")
    suspend fun decreaseCartItemQuantity(
        @Body request: IncreaseDecreaseQuantityRequest
    ): Response<GenericResponseIncreaseDecrease>

    @GET("api/customers/{id}/orders")
    suspend fun getCustomerOrders(@Path("id") customerId: Int): Response<CustomerOrdersResponse>

    @GET("api/orders/{orderId}/details")
    suspend fun getOrderDetails(
        @Path("orderId") orderId: Int
    ): Response<OrderDetailsResponse>  // Return type wrapped in Retrofit's Response

    @GET("api/suborders/{id}/route-info")
    suspend fun getRouteInfo(@Path("id") suborderId: Int): Response<RouteInfoResponse>

    @GET("api/statuses")
    suspend fun getStatuses(): StatusesResponse

    @PATCH("api/customer/order/{suborderId}/confirm-delivery")
    suspend fun confirmOrderDelivery(
        @Path("suborderId") suborderId: Int
    ): Response<ConfirmDeliveryResponse>

    @GET("api/suborder/{suborderId}/payment-status")
    suspend fun getPaymentStatus(
        @Path("suborderId") suborderId: Int
    ): Response<PaymentStatusResponse>

    @POST("api/customer/confirm-payment/{suborderId}")
    suspend fun confirmPaymentByCustomer(
        @Path("suborderId") suborderId: Int
    ): Response<ConfirmPaymentResponse>


    @PUT("api/customers/orders/{orderId}/cancel")
    suspend fun cancelOrder(
        @Path("orderId") orderId: Int
    ): Response<CancelOrderResponse>

    @GET("api/suborders/{suborderId}/live-tracking")
    suspend fun getLiveTracking(
        @Path("suborderId") suborderId: Int
    ): Response<LiveTrackingResponse>

    @GET("api/suborders/{suborderId}/live-route-tracking")
    suspend fun getLiveRouteTracking(
        @Path("suborderId") suborderId: Int
    ): Response<LiveRouteTrackingResponse>


    @POST("api/customers/{customerId}/add-address")
    suspend fun addAddress(
        @Path("customerId") customerId: Int,
        @Body address: AddAddressRequest
    ): Response<AddAddressResponse>


    @GET("api/suborders/{suborderId}/detailsForRating")
    suspend fun getRatingOrderDetails(@Path("suborderId") suborderId: Int): RatingOrderResponse

    @GET("api/suborders/{suborderId}/detailsForRatingStatus")
    suspend fun getRatingsStatusForSuborder(@Path("suborderId") suborderId: Int): Response<RatingsResponse>

    @Multipart
    @POST("api/itemrating")
    suspend fun submitItemRating(
        @Part("suborders_ID") suborderId: RequestBody,
        @Part("itemdetails_ID") itemDetailId: RequestBody,
        @Part("rating_stars") ratingStars: RequestBody,
        @Part("comments") comments: RequestBody?,
        @Part images: List<MultipartBody.Part>? = null
    ): Response<ItemRatingResponse>

    @POST("api/customer/rate-delivery-boy")
    suspend fun rateDeliveryBoy(
        @Body request: DeliveryBoyRatingRequest
    ): Response<DeliveryBoyRatingResponse>

}

