package com.example.lastmiledelivery.data.repository.common

import com.example.lastmiledelivery.data.models.Cities
import com.example.lastmiledelivery.data.models.StatusesResponse
import com.example.lastmiledelivery.data.remote.api.AdminApiService
import com.example.lastmiledelivery.data.remote.api.CustomerApiService
import javax.inject.Inject

class StatusRepository @Inject constructor(private val apiService: CustomerApiService) {

    suspend fun fetchStatuses(): StatusesResponse {
        return apiService.getStatuses()
    }
}
