package com.example.lastmiledelivery.viewmodels.vendor.IN_APPVendor
import android.app.Application
import android.content.Context
import android.os.Message
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.admin.MessageResponse
import com.example.lastmiledelivery.data.models.vendor.ItemAttribute
import com.example.lastmiledelivery.data.models.vendor.ItemCategory
import com.example.lastmiledelivery.data.models.vendor.ItemVariation
import com.example.lastmiledelivery.data.models.vendor.VendorItemResponse

import com.example.lastmiledelivery.data.repository.vendor.In_APPVendor.IN_APPVENDORItemRepository
import com.example.lastmiledelivery.data.repository.vendor.VendorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import javax.inject.Inject


@HiltViewModel
class IN_APPVENDORItemViewModel @Inject constructor(
    private val repository: IN_APPVENDORItemRepository
) : ViewModel() {

    private val _categories = mutableStateOf<List<ItemCategory>>(emptyList())
    val categories: State<List<ItemCategory>> = _categories

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun fetchItemCategories(shopCategoryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getItemCategories(shopCategoryId)
            result.onSuccess { categoryList ->
                _categories.value = categoryList
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
            _isLoading.value = false
        }
    }



    private val _variations = mutableStateOf<List<ItemVariation>>(emptyList())
    val variations: State<List<ItemVariation>> = _variations

    private val _isVariationLoading = mutableStateOf(false)
    val isVariationLoading: State<Boolean> = _isVariationLoading

    private val _variationError = mutableStateOf<String?>(null)
    val variationError: State<String?> = _variationError

    fun fetchItemVariations(itemCategoryId: Int) {
        viewModelScope.launch {
            _isVariationLoading.value = true
            _variationError.value = null
            _variations.value = repository.getItemVariations(itemCategoryId)
            _isVariationLoading.value = false

            if (_variations.value.isEmpty()) {
                _variationError.value = "No variations found for the selected category."
            }
        }
    }

    private val _attributes = mutableStateOf<Map<String, List<String>>>(emptyMap())
    val attributes: State<Map<String, List<String>>> = _attributes

    private val _isLoadingAttributes = mutableStateOf(false)
    val isLoadingAttributes: State<Boolean> = _isLoadingAttributes

    private val _attributesError = mutableStateOf<String?>(null)
    val attributesError: State<String?> = _attributesError

    fun fetchPredefinedAttributes(itemCategoryId: Int) {
        viewModelScope.launch {
            _isLoadingAttributes.value = true
            _attributesError.value = null

            val response = repository.fetchPredefinedAttributes(itemCategoryId)
            if (response != null) {
                _attributes.value = response.attributes
            } else {
                _attributesError.value = "Failed to load attributes"
            }
            _isLoadingAttributes.value = false
        }
    }


    private val _createItemState = MutableStateFlow<Result<MessageResponse>?>(null)
    val createItemState: StateFlow<Result<MessageResponse>?> = _createItemState.asStateFlow()

    fun createItem(
        vendorId: Int,
        shopId: Int,
        branchId: Int,
        name: RequestBody,
        timesensitive: RequestBody?,
        preparationTime: RequestBody?,
        description: RequestBody?,
        categoryId: RequestBody,
        branchesId: RequestBody,
        variationName: RequestBody?,
        price: RequestBody,
        additionalInfo: RequestBody?,
        picture: MultipartBody.Part?,
        attributesList: List<ItemAttribute>
    ) {
        viewModelScope.launch {
            val attributesMap = mutableMapOf<String, RequestBody>()
            attributesList.forEachIndexed { index, attribute ->
                attributesMap["attributes[$index][key]"] = attribute.key.toRequestBody("text/plain".toMediaTypeOrNull())
                attributesMap["attributes[$index][value]"] = attribute.value.toRequestBody("text/plain".toMediaTypeOrNull())
            }

            _createItemState.value = Result.failure(Exception("Loading...")) // Indicate loading state
            val result = repository.createItem(
                vendorId, shopId, branchId, name, timesensitive,preparationTime,description, categoryId, branchesId,
                variationName, price, additionalInfo, picture, attributesMap
            )
            _createItemState.value = result
        }
    }

    private val _items = mutableStateOf<List<VendorItemResponse>?>(null)
    val items: State<List<VendorItemResponse>?> = _items

    private val _errorMessages = mutableStateOf<String?>(null)
    val errorMessages: State<String?> = _errorMessages

    fun fetchItems(vendorId: Int, shopId: Int, branchId: Int) {
        viewModelScope.launch {
            val result = repository.getVendorItems(vendorId, shopId, branchId)
            result.onSuccess { _items.value = it }
                .onFailure { _errorMessages.value = it.message }
        }
    }
}

