package com.example.lastmiledelivery.data.repository.common

import com.example.lastmiledelivery.data.models.Cities
import com.example.lastmiledelivery.data.remote.api.AdminApiService
import javax.inject.Inject

class CitiesRepository @Inject constructor(private val apiService: AdminApiService) {

    suspend fun getAllCities():List<Cities>? {
        return try{
            val response = apiService.getAllCities()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        }
        catch (ex:Exception){
            null
        }
    }
}