package com.example.lastmiledelivery.data.remote.api

import com.example.lastmiledelivery.data.models.customer.CustomerSignupResponse
import com.example.lastmiledelivery.data.models.organization.DeliveryBoyResponse
import com.example.lastmiledelivery.data.models.organization.DeliveryBoySignupResponse
import com.example.lastmiledelivery.data.models.organization.OrganizationData
import com.example.lastmiledelivery.data.models.organization.OrganizationSignupResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface OrganizationApiService {
    @Multipart
    @POST("api/organization/signup") // Ensure this matches your Laravel API endpoint
    suspend fun organizationSignup(
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
    ): Response<OrganizationSignupResponse>


    @Multipart
    @POST("api/deliveryboys/signup")
    suspend fun deliveryBoySignup(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone_no") phoneNo: RequestBody,
        @Part("password") password: RequestBody,
        @Part("cnic") cnic: RequestBody,
        @Part profile_picture: MultipartBody.Part,
        @Part("license_no") licenseNo: RequestBody,
        @Part("license_expiration_date") licenseExpDate: RequestBody?,
        @Part license_front: MultipartBody.Part,
        @Part license_back: MultipartBody.Part,
        @Part("address_type") addressType: RequestBody,
        @Part("street") street: RequestBody,
        @Part("city") city: RequestBody,
        @Part("zip_code") zipCode: RequestBody?,
        @Part("country") country: RequestBody,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("organization_id") organizationId: RequestBody?
    ): Response<DeliveryBoySignupResponse>

    @GET("api/organizations/{id}")
    suspend fun getOrganizationData(@Path("id") id: Int): Response<OrganizationData>

    @GET("api/organizations/{organization_id}/deliveryboys")
    suspend fun getDeliveryBoys(
        @Path("organization_id") organizationId: Int
    ): Response<DeliveryBoyResponse>
}