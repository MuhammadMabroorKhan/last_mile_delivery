package com.example.lastmiledelivery.viewmodels.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.admin.LmdEarning
import com.example.lastmiledelivery.data.models.admin.LmdSetting
import com.example.lastmiledelivery.data.repository.admin.VendorApprovalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LmdViewModel @Inject constructor(private val repo: VendorApprovalRepository) : ViewModel() {
    var settings by mutableStateOf<LmdSetting?>(null)
//    var earnings by mutableStateOf<List<LmdEarning>>(emptyList())
//    var totalEarningAfterTax by mutableStateOf(0.0)

    fun fetchSettings() = viewModelScope.launch {
        val response = repo.getLmdSettings()
        if (response.isSuccessful) {
            settings = response.body()?.data
        }
    }


    fun updateOrderCharge(value: Double) = viewModelScope.launch {
        val response = repo.updateOrderCharge(value)
        if (response.isSuccessful) settings = response.body()
    }

    fun updateTaxPercentage(value: Double) = viewModelScope.launch {
        val response = repo.updateTaxPercentage(value)
        if (response.isSuccessful) settings = response.body()
    }

    fun updatePickupRadius(value: Double) = viewModelScope.launch {
        val response = repo.updatePickupRadius(value)
        if (response.isSuccessful) settings = response.body()
    }

//    fun fetchEarnings() = viewModelScope.launch {
//        val response = repo.getLmdEarnings()
//        if (response.isSuccessful) {
//            earnings = response.body()?.data ?: emptyList()
//            totalEarningAfterTax = response.body()?.total_earning_after_tax ?: 0.0
//        }
//    }
var earnings by mutableStateOf<List<LmdEarning>>(emptyList())
    private set

    var totalEarningAfterTax by mutableStateOf(0.0)
        private set

    private val _startDate = MutableStateFlow<LocalDate?>(null)
    val startDate: StateFlow<LocalDate?> = _startDate

    private val _endDate = MutableStateFlow<LocalDate?>(null)
    val endDate: StateFlow<LocalDate?> = _endDate

    fun setStartDate(date: LocalDate) {
        _startDate.value = date
    }

    fun setEndDate(date: LocalDate) {
        _endDate.value = date
    }

    fun clearDates() {
        _startDate.value = null
        _endDate.value = null
    }

    fun fetchEarnings() = viewModelScope.launch {
        val response = repo.getLmdEarnings()
        if (response.isSuccessful) {
            val body = response.body()
            earnings = body?.data ?: emptyList()
            totalEarningAfterTax = body?.total_earning_after_tax ?: 0.0
        }
    }


}