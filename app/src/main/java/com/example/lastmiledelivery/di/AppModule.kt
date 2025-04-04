package com.example.lastmiledelivery.di


import com.example.lastmiledelivery.data.remote.api.AdminApiService
import com.example.lastmiledelivery.data.remote.api.ApiService
import com.example.lastmiledelivery.data.remote.api.CustomerApiService
import com.example.lastmiledelivery.data.remote.api.OrganizationApiService
import com.example.lastmiledelivery.data.remote.api.VendorApiService
import com.example.lastmiledelivery.data.repository.AuthRepository
import com.example.lastmiledelivery.data.repository.admin.VendorApprovalRepository
import com.example.lastmiledelivery.data.repository.common.CitiesRepository
import com.example.lastmiledelivery.data.repository.common.ShopCategoryRepository
import com.example.lastmiledelivery.data.repository.vendor.In_APPVendor.IN_APPVENDORItemRepository
import com.example.lastmiledelivery.data.repository.vendor.VendorRepository
import com.example.lastmiledelivery.data.repository.vendor.VendorRepositoryShops
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

//object AppModule {
//
//    private const val BASE_URL = "http://192.168.43.63:8000/"
//
//
//    private val gson = GsonBuilder().setLenient().create()
//
//    private val okHttpClient = OkHttpClient.Builder()
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .readTimeout(30, TimeUnit.SECONDS)
//        .writeTimeout(30, TimeUnit.SECONDS)
//        .build()
//
//    private val retrofit: Retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create(gson))
//        .client(okHttpClient)
//        .build()
//
//    val apiService: ApiService = retrofit.create(ApiService::class.java)
//}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "http://192.168.43.63:8000/"

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCustomerApiService(retrofit: Retrofit): CustomerApiService {
        return retrofit.create(CustomerApiService::class.java)
    }

    // ✅ Provide Vendor API Service
    @Provides
    @Singleton
    fun provideVendorApiService(retrofit: Retrofit): VendorApiService {
        return retrofit.create(VendorApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService): AuthRepository {
        return AuthRepository(apiService)
    }



    // ✅ Provide Vendor Repository
    @Provides
    @Singleton
    fun provideVendorRepository(vendorApiService: VendorApiService): VendorRepository {
        return VendorRepository(vendorApiService)
    }

    @Provides
    @Singleton
    fun provideAdminApiService(retrofit: Retrofit): AdminApiService {
        return retrofit.create(AdminApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideVendorApprovalRepository(apiService: AdminApiService): VendorApprovalRepository {
        return VendorApprovalRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideShopCategoryRepository(apiService: ApiService): ShopCategoryRepository {
        return ShopCategoryRepository(apiService)
    }
//Vendor SCreens
    @Provides
    @Singleton
    fun provideVendorRepositoryShops(apiService: VendorApiService): VendorRepositoryShops {
        return VendorRepositoryShops(apiService)
    }

    //CIties
    @Provides
    @Singleton
    fun provideCities(apiService: AdminApiService):CitiesRepository{
        return CitiesRepository(apiService)
    }



    @Provides
    @Singleton
    fun provideIN_APPVENDORItemRepository(apiService: VendorApiService): IN_APPVENDORItemRepository {
        return IN_APPVENDORItemRepository(apiService)
    }



    @Provides
    @Singleton
    fun provideOrganizationApiService(retrofit: Retrofit): OrganizationApiService {
        return retrofit.create(OrganizationApiService::class.java)
    }





}

