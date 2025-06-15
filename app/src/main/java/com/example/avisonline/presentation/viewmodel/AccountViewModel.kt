package com.example.avisonline.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avisonline.data.model.UserAccount
import com.example.avisonline.data.repository.AuthRepository
import com.example.avisonline.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AccountState {
    data class Success(val user: UserAccount) : AccountState()
    data class Error(val message: String) : AccountState()
    object Loading : AccountState()
}

class AccountViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _accountState = MutableStateFlow<AccountState>(AccountState.Loading)
    val accountState = _accountState.asStateFlow()

    fun fetchAccountDetails() {
        viewModelScope.launch {
            _accountState.value = AccountState.Loading
            try {
                val token = TokenManager.getAuthHeader()
                val response = repository.getMe(token)
                if (response.isSuccessful && response.body() != null) {
                    _accountState.value = AccountState.Success(response.body()!!)
                } else {
                    _accountState.value = AccountState.Error("Error al cargar tu cuenta.")
                }
            } catch (e: Exception) {
                _accountState.value = AccountState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}