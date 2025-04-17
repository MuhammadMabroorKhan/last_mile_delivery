package com.example.lastmiledelivery.data.remote.api

import com.example.lastmiledelivery.data.models.LoginRequest
import com.example.lastmiledelivery.data.models.LoginResponse
import com.example.lastmiledelivery.data.models.ShopCategoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
        @POST("api/login")
        suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

        @GET("api/shopcategories")
        suspend fun getShopCategories(): Response<List<ShopCategoryResponse>>



}

