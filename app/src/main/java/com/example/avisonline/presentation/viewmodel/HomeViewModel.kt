package com.example.avisonline.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avisonline.data.model.HomeResponse
import com.example.avisonline.data.repository.ProductRepository
import com.example.avisonline.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    data class Success(val data: HomeResponse) : HomeState()
    data class Error(val message: String) : HomeState()
    object Loading : HomeState()
}

class HomeViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState = _homeState.asStateFlow()

    fun fetchHomeData() {
        viewModelScope.launch {
            _homeState.value = HomeState.Loading
            try {
                val token = TokenManager.getAuthHeader()
                val response = repository.getHomeData(token)
                if (response.isSuccessful && response.body() != null) {
                    _homeState.value = HomeState.Success(response.body()!!)
                } else {
                    _homeState.value = HomeState.Error("Error al cargar los datos.")
                }
            } catch (e: Exception) {
                _homeState.value = HomeState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}