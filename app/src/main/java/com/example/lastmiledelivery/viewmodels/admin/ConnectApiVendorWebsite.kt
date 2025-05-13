package com.example.lastmiledelivery.viewmodels.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.admin.AddMapping
import com.example.lastmiledelivery.data.models.admin.AddVariable
import com.example.lastmiledelivery.data.models.admin.ApiMethodRequest
import com.example.lastmiledelivery.data.models.admin.ApiVendorRegisterWebsite
import com.example.lastmiledelivery.data.models.admin.ApiVendorRequest
import com.example.lastmiledelivery.data.models.admin.ApiVendorResponse
import com.example.lastmiledelivery.data.models.admin.GetApiVendorData
import com.example.lastmiledelivery.data.models.admin.IntegrationResponse
import com.example.lastmiledelivery.data.models.admin.MappingInput
import com.example.lastmiledelivery.data.models.admin.MethodInputForApiVendor
import com.example.lastmiledelivery.data.models.admin.MethodTemplate
import com.example.lastmiledelivery.data.models.admin.SaveApiMethodsRequest
import com.example.lastmiledelivery.data.models.admin.SaveMappingRequest
import com.example.lastmiledelivery.data.models.admin.UpdateApiMethodRequest
import com.example.lastmiledelivery.data.models.admin.VendorMethod
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

    var integrationState by mutableStateOf<IntegrationResponse?>(null)
        private set

    var isLoadingIntegration by mutableStateOf(false)
    var errorMessageIntegration by mutableStateOf<String?>(null)

    fun loadIntegrationDetails(branchId: Int) {
        viewModelScope.launch {
            isLoadingIntegration = true
            try {
                val response = repository.fetchIntegrationDetails(branchId)
                integrationState = response
            } catch (e: Exception) {
                errorMessageIntegration = e.message
                // Force fallback response to avoid null
                integrationState = IntegrationResponse(
                    status = false,
                    data = null,
                    message = "No Integration Found"
                )
            } finally {
                isLoadingIntegration = false
            }
        }
    }

    var integrationResponse by mutableStateOf<ApiVendorResponse?>(null)
    var loadingApiVendor by mutableStateOf(false)
    var errorMessageApiVendor by mutableStateOf<String?>(null)

    fun addApiVendor(request: ApiVendorRequest) {
        viewModelScope.launch {
            loadingApiVendor = true
            errorMessageApiVendor = null
            val result = repository.addApiVendor(request)
            result.onSuccess {
                integrationResponse = it
            }.onFailure {
                errorMessageApiVendor = it.localizedMessage ?: "Something went wrong"
            }
            loadingApiVendor = false
        }
    }

    fun clearIntegrationResponse() {
        integrationResponse = null
    }


    var isUpdatingIntegration by mutableStateOf(false)
    var updateIntegrationMessage by mutableStateOf<String?>(null)

    fun updateApiVendor(id: Int, request: ApiVendorRequest) {
        viewModelScope.launch {
            isUpdatingIntegration = true
            updateIntegrationMessage = null
            try {
                val response = repository.updateApiVendor(id, request)
                updateIntegrationMessage = response.message
                getVendorIntegration(request.branches_ID) // refresh updated data
            } catch (e: Exception) {
                updateIntegrationMessage = e.message
            } finally {
                isUpdatingIntegration = false
            }
        }
    }



    var vendorIntegrationDetails by mutableStateOf<GetApiVendorData?>(null)
        private set

    var vendorIntegrationMessage by mutableStateOf<String?>(null)
        private set

    var isLoadingGetApiVendor by mutableStateOf(false)
        private set

    fun getVendorIntegration(branchId: Int) {
        viewModelScope.launch {
            isLoadingGetApiVendor = true
            val result = repository.getApiVendor(branchId)
            vendorIntegrationDetails = result?.data
            vendorIntegrationMessage = result?.message
            isLoadingGetApiVendor = false
        }
    }

    var methodTemplates by mutableStateOf<List<MethodTemplate>>(emptyList())
        private set

    var isLoadingMethod by mutableStateOf(false)
    var saveSuccess by mutableStateOf<String?>(null)

    fun loadMethodTemplates() {
        viewModelScope.launch {
            isLoadingMethod = true
            val response = repository.getStandardApiMethods()
            if (response.isSuccessful) {
                methodTemplates = response.body()?.data ?: emptyList()
            }
            isLoadingMethod = false
        }
    }

    fun saveApiMethods(apiVendorId: Int, methods: List<MethodInputForApiVendor>) {
        viewModelScope.launch {
            isLoadingMethod = true
            val request = SaveApiMethodsRequest(methods)
            val response = repository.saveApiMethods(apiVendorId, request)
            if (response.isSuccessful) {
                saveSuccess = response.body()?.message
            }
            isLoadingMethod = false
        }
    }

    var updateStatus by mutableStateOf<String?>(null)
    var isUpdatingMethod by mutableStateOf(false)

    fun updateApiMethodById(methodId: Int, request: UpdateApiMethodRequest, apiVendorId: Int) {
        viewModelScope.launch {
            isUpdatingMethod = true
            try {
                val response = repository.updateApiMethod(methodId, request)
                updateStatus = response.message
                // Refresh after update
                loadSavedMethods(apiVendorId)
            } catch (e: Exception) {
                updateStatus = "Update failed: ${e.message}"
            } finally {
                isUpdatingMethod = false
            }
        }
    }





    var savedVendorMethods by mutableStateOf<List<VendorMethod>>(emptyList())
    var isLoadingSavedMethods by mutableStateOf(false)
    var methodLoadError by mutableStateOf<String?>(null)


    fun loadSavedMethods(apiVendorId: Int) {
        viewModelScope.launch {
            isLoadingSavedMethods = true
            try {
                val response = repository.getMethodsByVendor(apiVendorId)
                if (response != null) {
                    if (response.status) {
                        savedVendorMethods = response.methods
                        methodLoadError = null // no error
                    } else {
                        // Check if it's just "no methods"
                        if (response.methods.isEmpty()) {
                            savedVendorMethods = emptyList()
                            methodLoadError = null // no error, just empty
                        } else {
                            methodLoadError = response.message
                        }
                    }
                }
            } catch (e: Exception) {
                methodLoadError = "Something went wrong"
            } finally {
                isLoadingSavedMethods = false
            }
        }
    }


    var mappings by mutableStateOf<List<AddMapping>>(emptyList())
    var variables by mutableStateOf<List<AddVariable>>(emptyList())
    var isMappingsLoaded by mutableStateOf(false)
    var errorMessageMapping by mutableStateOf<String?>(null)
    var isVariablesLoaded by mutableStateOf(false)


    fun fetchMappings(branchId: Int, vendorId: Int) {
        isMappingsLoaded = false
        isVariablesLoaded = false
        errorMessageMapping = null
        mappings = emptyList()
        variables = emptyList()

        viewModelScope.launch {
            val result = repository.getMappings(branchId, vendorId)
            if (result.success) {
                val mappingList = result.data ?: emptyList()
                mappings = mappingList
                isMappingsLoaded = true

                if (mappingList.isEmpty()) {
                    fetchVariables() // Show input form
                }
            } else {
                isMappingsLoaded = true
                errorMessageMapping = result.message
                fetchVariables()
            }
        }
    }




    fun fetchVariables() {
        viewModelScope.launch {
            val result = repository.getVariables()
            if (result.success) {
                variables = result.data ?: emptyList()
            } else {
                errorMessageMapping = result.message
            }
            isVariablesLoaded = true
        }
    }





    fun saveMappings(
        branchId: Int,
        vendorId: Int,
        inputs: List<MappingInput>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val request = SaveMappingRequest(branchId, vendorId, inputs)
            val result = repository.saveMappings(request)
            if (result.success) {
                onSuccess()
            } else {
                errorMessageMapping = result.message
            }
        }
    }



    fun updateMapping(id: Int, newValue: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.updateMapping(id, newValue)
                if (response.success) {
                    onSuccess()
                } else {
                    errorMessageMapping = response.message
                }
            } catch (e: Exception) {
                errorMessageMapping = e.message
            }
        }
    }


    //Add New Variable By Admin
    var showAddVariableDialog by mutableStateOf(false)
    var newVariableTag by mutableStateOf("")
    var addVariableMessage by mutableStateOf<String?>(null)

    fun addVariable(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.addVariable(newVariableTag)
                if (response.isSuccessful) {
                    addVariableMessage = response.body()?.message
                    newVariableTag = ""
                    showAddVariableDialog = false
                    onSuccess()
                } else {
                    addVariableMessage = "Failed: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                addVariableMessage = "Error: ${e.message}"
            }
        }
    }


    //Add New Methods
    var isDialogVisible by mutableStateOf(false)
    var methodName by mutableStateOf("")
    var httpMethod by mutableStateOf("GET")
    var endpoint by mutableStateOf("")
    var description by mutableStateOf("")
    var saveResult by mutableStateOf<String?>(null)
    var apiVendorIdInput by mutableStateOf("")

    fun saveNewApiMethod() {
        viewModelScope.launch {
            try {
                val vendorId = apiVendorIdInput.toIntOrNull() ?: 0

                val method = ApiMethodRequest(
                    method_name = methodName,
                    http_method = httpMethod,
                    endpoint = if (endpoint.isNotBlank()) endpoint else null,
                    description = if (description.isNotBlank()) description else null,
                    apivendor_ID = vendorId
                )
                val response = repository.saveNewApiMethods(vendorId, listOf(method))
                saveResult = response.message
                isDialogVisible = false
                clearForm()
            } catch (e: Exception) {
                saveResult = e.localizedMessage
            }
        }
    }


    private fun clearForm() {
        methodName = ""
        httpMethod = "GET"
        endpoint = ""
        description = ""
    }
}
