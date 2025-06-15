package com.example.avisonline.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avisonline.data.model.KpiConfigResponse
import com.example.avisonline.data.repository.ProductRepository
import com.example.avisonline.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

sealed class ProductCreationState {
    data class Success(val message: String) : ProductCreationState()
    data class Error(val message: String) : ProductCreationState()
    object Loading : ProductCreationState()
    object Idle : ProductCreationState()
}

class CreateProductViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {

    private val _limitState = MutableStateFlow<KpiConfigResponse?>(null)
    val limitState = _limitState.asStateFlow()

    private val _creationState = MutableStateFlow<ProductCreationState>(ProductCreationState.Idle)
    val creationState = _creationState.asStateFlow()

    fun checkProductLimit() {
        viewModelScope.launch {
            try {
                val token = TokenManager.getAuthHeader()
                val response = repository.getKpiConfig(token)
                if (response.isSuccessful) {
                    _limitState.value = response.body()
                }
            } catch (e: Exception) {
                // Handle error silently or show a small error message
            }
        }
    }

    fun createProduct(context: Context, title: String, summary: String, price: String, imageUri: Uri?) {
        if (imageUri == null) {
            _creationState.value = ProductCreationState.Error("Por favor, selecciona una imagen.")
            return
        }

        viewModelScope.launch {
            _creationState.value = ProductCreationState.Loading
            try {
                val token = TokenManager.getAuthHeader()

                // Create RequestBody for text fields
                val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val summaryPart = summary.toRequestBody("text/plain".toMediaTypeOrNull())
                val pricePart = price.toRequestBody("text/plain".toMediaTypeOrNull())
                
                // Create MultipartBody.Part for the image file
                val file = uriToFile(context, imageUri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("portada", file.name, requestFile)

                val response = repository.createProduct(token, titlePart, summaryPart, pricePart, imagePart)

                if (response.isSuccessful) {
                    _creationState.value = ProductCreationState.Success("¡Producto creado con éxito!")
                } else if (response.code() == 403) {
                     _creationState.value = ProductCreationState.Error("Límite alcanzado. Actualice su plan para crear nuevos productos.")
                } else {
                    _creationState.value = ProductCreationState.Error("Error al crear el producto: ${response.message()}")
                }
            } catch (e: Exception) {
                _creationState.value = ProductCreationState.Error("Error de conexión: ${e.message}")
            }
        }
    }
    
    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image_file")
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input?.copyTo(output)
            }
        }
        return file
    }
    
    fun resetCreationState() {
        _creationState.value = ProductCreationState.Idle
    }
}