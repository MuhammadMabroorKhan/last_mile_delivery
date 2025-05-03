package com.example.lastmiledelivery.viewmodels.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.admin.ApiVendorRegisterWebsite
import com.example.lastmiledelivery.data.repository.admin.VendorApprovalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: VendorApprovalRepository
) : ViewModel() {

    var vendorList by mutableStateOf<List<ApiVendorRegisterWebsite>>(emptyList())
    var searchQuery by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchVendors()
    }

    fun fetchVendors() {
        viewModelScope.launch {
            isLoading = true
            try {
                vendorList = repository.getApiVendors()
            } catch (e: Exception) {
                errorMessage = e.message
            }
            isLoading = false
        }
    }

    fun filteredVendors(): List<ApiVendorRegisterWebsite> {
        val query = searchQuery.trim().lowercase()
        return vendorList.filter {
            it.email.lowercase().contains(query) ||
                    it.cnic.lowercase().contains(query)
        }
    }
}
