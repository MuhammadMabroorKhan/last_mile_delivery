package com.example.lastmiledelivery.viewmodels.common

import com.example.lastmiledelivery.data.models.ShopCategoryResponse
import com.example.lastmiledelivery.data.repository.common.ShopCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopCategoryViewModel @Inject constructor(private val repository: ShopCategoryRepository) : ViewModel() {
    private val _categories = MutableStateFlow<List<ShopCategoryResponse>>(emptyList())
    val categories: StateFlow<List<ShopCategoryResponse>> = _categories

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        viewModelScope.launch {
            repository.getShopCategories()?.let {
                _categories.value = it
            }
        }
    }
}
