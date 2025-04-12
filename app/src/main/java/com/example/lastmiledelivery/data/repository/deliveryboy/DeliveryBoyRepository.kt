package com.example.lastmiledelivery.data.repository.deliveryboy

import com.example.lastmiledelivery.data.models.customer.CustomerData
import com.example.lastmiledelivery.data.models.deliveryboy.DeliveryBoyDataResponse
import com.example.lastmiledelivery.data.remote.api.DeliveryBoysApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


class DeliveryBoyRepository @Inject constructor(private val api:DeliveryBoysApiService){


    suspend fun getDeliveryBoyData(id:Int):DeliveryBoyDataResponse?{
        val response = api.getDeliveryBoyData(id)
        return if (response.isSuccessful){
            response.body()
        }
        else{
            null
        }
    }

}