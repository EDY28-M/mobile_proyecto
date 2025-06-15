package com.example.avisonline.data.repository

import com.example.avisonline.data.model.CreateProductResponse
import com.example.avisonline.data.model.HomeResponse
import com.example.avisonline.data.model.KpiConfigResponse
import com.example.avisonline.data.remote.ApiService
import com.example.avisonline.data.remote.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ProductRepository(private val apiService: ApiService = RetrofitClient.instance) {

    suspend fun getHomeData(token: String): Response<HomeResponse> {
        return apiService.getHomeData(token)
    }

    suspend fun getKpiConfig(token: String): Response<KpiConfigResponse> {
        return apiService.getKpiConfig(token)
    }

    suspend fun createProduct(
        token: String,
        title: RequestBody,
        summary: RequestBody,
        price: RequestBody,
        image: MultipartBody.Part
    ): Response<CreateProductResponse> {
        return apiService.createProduct(token, title, summary, price, image)
    }
}