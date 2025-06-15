package com.example.avisonline.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avisonline.data.model.AuthResponse
import com.example.avisonline.data.model.LoginRequest
import com.example.avisonline.data.model.RegisterRequest
import com.example.avisonline.data.model.GoogleLoginRequest // <-- Nueva importación
import com.example.avisonline.data.repository.AuthRepository
import com.example.avisonline.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthResult {
    data class Success(val welcomeMessage: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
    object Idle : AuthResult()
}

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            try {
                val response = repository.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    TokenManager.token = authResponse.access_token
                    _authState.value = AuthResult.Success("¡Login exitoso!")
                } else {
                    _authState.value = AuthResult.Error("Error: Credenciales incorrectas.")
                }
            } catch (e: Exception) {
                _authState.value = AuthResult.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            try {
                val response = repository.register(RegisterRequest(name, email, password))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    TokenManager.token = authResponse.access_token
                    _authState.value = AuthResult.Success("¡Bienvenido a Avisonline!")
                } else {
                    _authState.value = AuthResult.Error("Error en el registro.")
                }
            } catch (e: Exception) {
                _authState.value = AuthResult.Error("Error de conexión: ${e.message}")
            }
        }
    }

    // NUEVO MÉTODO
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            try {
                // Aquí deberías tener una función en tu repositorio que maneje esto
                // Por simplicidad, la llamaremos directamente aquí, pero debería ir en el repo.
                val response = repository.loginWithGoogle(GoogleLoginRequest(idToken))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    TokenManager.token = authResponse.access_token
                    _authState.value = AuthResult.Success("¡Login con Google exitoso!")
                } else {
                    _authState.value = AuthResult.Error("Error: No se pudo verificar el token de Google.")
                }
            } catch (e: Exception) {
                _authState.value = AuthResult.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthResult.Idle
    }
}
