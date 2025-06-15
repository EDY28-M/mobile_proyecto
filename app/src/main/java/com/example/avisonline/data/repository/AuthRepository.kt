package com.example.avisonline.data.repository

import com.example.avisonline.data.model.*
import com.example.avisonline.data.remote.ApiService
import com.example.avisonline.data.remote.RetrofitClient
import retrofit2.Response

class AuthRepository(private val apiService: ApiService = RetrofitClient.instance) {

    suspend fun login(loginRequest: LoginRequest): Response<AuthResponse> {
        return apiService.login(loginRequest)
    }

    suspend fun register(registerRequest: RegisterRequest): Response<AuthResponse> {
        return apiService.register(registerRequest)
    }

    suspend fun loginWithGoogle(googleLoginRequest: GoogleLoginRequest): Response<AuthResponse> {
        return apiService.loginWithGoogle(googleLoginRequest)
    }    

    suspend fun getMe(token: String): Response<UserAccount> {
        return apiService.getMe(token)
    }
}