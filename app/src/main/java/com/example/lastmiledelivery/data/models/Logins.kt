package com.example.lastmiledelivery.data.models

data class LoginRequest(
    val email: String,
    val password: String
)



data class LoginResponse(
    val message: String,
    val user: User
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)


//SHop Categories
data class ShopCategoryResponse(
    val id: Int,
    val name: String
)


//CIties
data class Cities(
    val id:Int,
    val name:String
)
