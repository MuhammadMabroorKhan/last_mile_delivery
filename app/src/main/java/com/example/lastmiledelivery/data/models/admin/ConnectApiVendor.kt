package com.example.lastmiledelivery.data.models.admin

data class ApiVendorRegisterWebsite(
    val vendor_ID: Int,
    val vendor_type: String,
    val approval_status: String,
    val lmd_users_ID: Int,
    val name: String,
    val email: String,
    val phone_no: String,
    val cnic: String,
    val profile_picture: String?,
    val account_creation_date: String
)


// fetch ApI VENDOR WEBSITE INTEGRATION
data class IntegrationResponse(
    val status: Boolean,
    val message: String,
    val data: IntegrationData?
)

data class IntegrationData(
    val apivendor: ApiVendorIntegration?,
    val apimethods: List<ApiMethodIntegration>,
    val variables: List<Variable>,
    val mappings: List<Mapping>
)

data class ApiVendorIntegration(
    val id: Int,
    val api_key: String,
    val api_base_url: String,
    val api_auth_method: String,
    val api_version: String,
    val vendor_integration_status: String,
    val response_format: String,
    val branches_ID: Int
)

data class ApiMethodIntegration(
    val id: Int,
    val method_name: String,
    val http_method: String,
    val endpoint: String,
    val description: String?,
    val apivendor_ID: Int
)

data class Variable(
    val id: Int,
    val tags: String
)

data class Mapping(
    val id: Int,
    val api_values: String,
    val variable_ID: Int,
    val apivendor_ID: Int,
    val branch_ID: Int
)
