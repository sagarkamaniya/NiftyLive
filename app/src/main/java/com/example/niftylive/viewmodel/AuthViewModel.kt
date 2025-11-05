package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repo = ServiceLocator.niftyRepository

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun login(clientCode: String, password: String, apiKey: String, authCode: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val resp = repo.loginWithCredentials(clientCode, password, apiKey, authCode)
                if (resp.isSuccessful) {
                    repo.saveTokens(resp.body())
                    repo.saveCredentials(clientCode, apiKey)
                    _state.value = AuthState.Success(resp.body()?.data?.access_token ?: "")
                } else {
                    val msg = resp.errorBody()?.string() ?: "Login failed: ${resp.code()}"
                    _state.value = AuthState.Error(msg)
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }
}
