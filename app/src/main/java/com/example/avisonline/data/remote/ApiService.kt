package com.example.avisonline.data.remote

import com.example.avisonline.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>

    @POST("auth/login_ecommerce")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>
    
    @POST("auth/login_google")
    suspend fun loginWithGoogle(@Body googleLoginRequest: GoogleLoginRequest): Response<AuthResponse>

    @GET("ecommerce/home")
    suspend fun getHomeData(@Header("Authorization") token: String): Response<HomeResponse>

    @GET("admin/kpi/config")
    suspend fun getKpiConfig(@Header("Authorization") token: String): Response<KpiConfigResponse>

    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<UserAccount>

    @Multipart
    @POST("admin/products")
    suspend fun createProduct(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("resumen") summary: RequestBody,
        @Part("price_pen") price: RequestBody,
        @Part portada: MultipartBody.Part
    ): Response<CreateProductResponse>
}