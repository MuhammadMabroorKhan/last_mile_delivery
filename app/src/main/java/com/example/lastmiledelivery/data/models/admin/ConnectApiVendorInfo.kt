package com.example.lastmiledelivery.data.models.admin

//Request
data class ApiVendorRequest(
    val api_key: String,
    val api_base_url: String?,
    val api_auth_method: String?,
    val api_version: String?,
    val vendor_integration_status: String?,
    val response_format: String?,
    val branches_ID: Int
)
//Response
data class ApiVendorResponse(
    val status: Boolean,
    val message: String,
    val data: ApiVendorData?
)

data class ApiVendorData(
    val id: Int,
    val api_key: String,
    val api_base_url: String?,
    val api_auth_method: String?,
    val api_version: String?,
    val vendor_integration_status: String?,
    val response_format: String?,
    val branches_ID: Int
)
/////////////////////////////////////////////
data class GetApiVendorResponse(
    val status: Boolean,
    val message: String,
    val data: GetApiVendorData? = null
)

data class GetApiVendorData(
    val id: Int,
    val api_key: String,
    val api_base_url: String?,
    val api_auth_method: String?,
    val api_version: String?,
    val vendor_integration_status: String?,
    val response_format: String?,
    val branches_ID: Int,
    val created_at: String?,
    val updated_at: String?
)


/////////////////////////////
data class MethodsTemplateResponse(
    val status: Boolean,
    val message: String,
    val data: List<MethodTemplate>
)

data class MethodTemplate(
    val method_name: String,
    val http_method: String
)
data class SaveApiMethodsRequest(
    val methods: List<MethodInputForApiVendor>
)

data class MethodInputForApiVendor(
    val method_name: String,
    val http_method: String,
    val endpoint: String,
    val description: String
)
data class SaveApiMethodResponse(
    val status: Boolean,
    val message: String
)
////////////////////////////
data class VendorMethodResponse(
    val status: Boolean,
    val message: String,
    val methods: List<VendorMethod>
)

data class VendorMethod(
    val id: Int,
    val method_name: String,
    val http_method: String,
    val endpoint: String,
    val description: String,
    val apivendor_ID: Int
)
///////////////////////
//add mapping and get mapping
data class AddMapping(
    val id: Int,
    val api_values: String,
    val variable_ID: Int,
    val apivendor_ID: Int,
    val branch_ID: Int
)

data class AddVariable(
    val id: Int,
    val tags: String
)

data class SaveMappingRequest(
    val branch_ID: Int,
    val apivendor_ID: Int,
    val mappings: List<MappingInput>
)

data class MappingInput(
    val variable_ID: Int,
    val api_values: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
