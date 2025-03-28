package com.example.lastmiledelivery.data.remote.api

import com.example.lastmiledelivery.data.models.admin.MessageResponse
import com.example.lastmiledelivery.data.models.vendor.BranchesResponse
import com.example.lastmiledelivery.data.models.vendor.CreateBranchResponse
import com.example.lastmiledelivery.data.models.vendor.CreateItemRequest
import com.example.lastmiledelivery.data.models.vendor.ItemCategoryResponse
import com.example.lastmiledelivery.data.models.vendor.ItemVariationResponse
import com.example.lastmiledelivery.data.models.vendor.PredefinedAttributesResponse
import com.example.lastmiledelivery.data.models.vendor.ShopCreationResponse
import com.example.lastmiledelivery.data.models.vendor.ShopRequest
import com.example.lastmiledelivery.data.models.vendor.ShopResponse
import com.example.lastmiledelivery.data.models.vendor.ToggleBranchResponse
import com.example.lastmiledelivery.data.models.vendor.UpdateBranchResponse
import com.example.lastmiledelivery.data.models.vendor.VendorItemResponse
import com.example.lastmiledelivery.data.models.vendor.VendorResponse
import com.example.lastmiledelivery.data.models.vendor.VendorSignupResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface VendorApiService {

    @Multipart
    @POST("api/vendor/signup")
    suspend fun vendorSignup(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone_no") phoneNo: RequestBody,
        @Part("password") password: RequestBody,
        @Part("cnic") cnic: RequestBody,
        @Part profile_picture: MultipartBody.Part?,
        @Part("vendor_type") vendorType: RequestBody,
        @Part("address_type") addressType: RequestBody,
        @Part("street") street: RequestBody,
        @Part("city") city: RequestBody,
        @Part("zip_code") zipCode: RequestBody?,
        @Part("country") country: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?
    ): Response<VendorSignupResponse>

    @GET("api/vendor/{id}")
    suspend fun getVendorData(
        @Path("id") id: Int
    ): Response<VendorResponse>


    @GET("api/vendor/{vendorId}/shops")
    suspend fun getVendorShops(@Path("vendorId") vendorId: Int): Response<ShopResponse>

    @POST("api/vendor/shop")
    suspend fun createShop(@Body shopRequest: ShopRequest): Response<ShopCreationResponse>

    @GET("api/shop/{shopId}/branches")
    suspend fun getBranchesByShopId(@Path("shopId") shopId: Int): Response<BranchesResponse>

    @Multipart
    @POST("api/branches")
    suspend fun createBranch(
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("opening_hours") openingHours: RequestBody,
        @Part("closing_hours") closingHours: RequestBody,
        @Part("contact_number") contactNumber: RequestBody,
        @Part("city_ID") cityId: RequestBody,
        @Part("area_name") areaName: RequestBody,
        @Part("postal_code") postalCode: RequestBody?,
        @Part("shops_ID") shopsId: RequestBody,
        @Part branchPicture: MultipartBody.Part?
    ): Response<CreateBranchResponse>


    @Multipart
    @POST("api/branches/{id}")
    suspend fun updateBranch(
        @Path("id") branchId: Int,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("opening_hours") openingHours: RequestBody?,
        @Part("closing_hours") closingHours: RequestBody?,
        @Part("contact_number") contactNumber: RequestBody?,
        @Part("city_ID") cityId: RequestBody?,  // Fix: Ensure naming matches Laravel
        @Part("area_name") areaName: RequestBody?,
        @Part("postal_code") postalCode: RequestBody?,
        @Part("shops_ID") shopsId: RequestBody?,  // Fix: Ensure naming matches Laravel
        @Part branchPicture: MultipartBody.Part?
    ): Response<UpdateBranchResponse>



    //getItemCategories by shop id
        @GET("api/itemcategories/{shopCategoryId}")
        suspend fun getItemCategories(@Path("shopCategoryId") shopCategoryId: Int): Response<ItemCategoryResponse>

        //get item variations wen item category is selected
    @GET("api/item-variations/{itemCategoryId}")
    suspend fun getItemVariations(@Path("itemCategoryId") itemCategoryId: Int): Response<ItemVariationResponse>

    //get Item Deafult Attributes ... key value pair whn it is selected...
    @GET("api/PredefinedAttributes/{itemCategoryId}")
    suspend fun getPredefinedAttributes(
        @Path("itemCategoryId") itemCategoryId: Int
    ): Response<PredefinedAttributesResponse>

    @Multipart
    @POST("api/vendor/{vendorId}/shop/{shopId}/branch/{branchId}/item")
    suspend fun createItem(
        @Path("vendorId") vendorId: Int,
        @Path("shopId") shopId: Int,
        @Path("branchId") branchId: Int,
        @Part("name") name: RequestBody,
        @Part("timesensitive") timesensitive: RequestBody?,
        @Part("preparation_time") preparationTime: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("category_ID") categoryId: RequestBody,
        @Part("branches_ID") branchesId: RequestBody,
        @Part("variation_name") variationName: RequestBody?,
        @Part("price") price: RequestBody,
        @Part("additional_info") additionalInfo: RequestBody?,
        @Part picture: MultipartBody.Part?,
        @PartMap attributes: Map<String, @JvmSuppressWildcards RequestBody>?
    ): Response<MessageResponse>


    @GET("api/vendor/{vendorId}/shop/{shopId}/branch/{branchId}/items")
    suspend fun getVendorItems(
        @Path("vendorId") vendorId: Int,
        @Path("shopId") shopId: Int,
        @Path("branchId") branchId: Int
    ): Response<List<VendorItemResponse>>

    @PUT("api/Vendor/branches/{branchId}/togglestatus")
    suspend fun toggleBranchStatus(@Path("branchId") branchId: Int): Response<ToggleBranchResponse>
}