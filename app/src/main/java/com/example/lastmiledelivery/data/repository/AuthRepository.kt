package com.example.lastmiledelivery.data.repository

import android.util.Log
import com.example.lastmiledelivery.data.models.LoginRequest
import com.example.lastmiledelivery.data.models.LoginResponse
import com.example.lastmiledelivery.data.remote.api.ApiService
import com.example.lastmiledelivery.di.AppModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class AuthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))

            response.body()?.let {
                if (response.isSuccessful) Result.success(it)
                else Result.failure(Exception("Invalid Credentials"))
            } ?: Result.failure(Exception("Invalid response body"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}


