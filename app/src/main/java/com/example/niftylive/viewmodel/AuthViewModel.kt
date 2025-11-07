package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ✅ Represents all login states
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
        try {
            val (response, rawText) = repo.loginWithCredentials(clientCode, password, apiKey, authCode)

            if (response.isSuccessful && response.body()?.data?.jwtToken != null) {
                val data = response.body()!!.data!!
                repo.saveTokens(response.body())
                _token.value = data.jwtToken ?: ""
                _authState.value = AuthState.Success("Login Successful! Token saved.")
            } else {
                _authState.value = AuthState.Error("Invalid response:\n$rawText")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Exception: ${e.localizedMessage}")
        }
    }
}
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val response = repo.loginWithCredentials(clientCode, password, apiKey, authCode)

                if (response.isSuccessful && response.body() != null) {
                    repo.saveTokens(response.body())
                    repo.saveCredentials(clientCode, apiKey)

                    val token = response.body()?.data?.jwtToken ?: ""
                    _state.value = AuthState.Success(token)
                } else {
                    val msg = response.errorBody()?.string() ?: "Login failed: ${response.code()}"
                    _state.value = AuthState.Error(msg)
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _state.value = AuthState.Idle
    }
}