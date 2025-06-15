package com.example.avisonline.data.model

data class RegisterRequest(val name: String, val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)

data class AuthResponse(
    val access_token: String,
    val user: UserInfo
)

data class UserInfo(
    val full_name: String,
    val email: String
)