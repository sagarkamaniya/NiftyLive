package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.repository.NiftyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repo: NiftyRepository // if using Hilt, annotate constructor and provide repo
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    /**
     * Validates inputs before calling repository.
     * Returns immediately with an Error state if required fields are missing.
     *
     * clientCode/password/apiKey/totp must be non-empty.
     */
    fun login(clientCode: String, password: String, apiKey: String, totp: String) {
        if (clientCode.isBlank() || password.isBlank() || apiKey.isBlank() || totp.isBlank()) {
            _state.value = AuthState.Error("Please fill all fields")
            return
        }

        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val resp = repo.loginWithCredentials(clientCode, password, apiKey, totp)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    // adapt to your model's field names; many SmartAPI variants use jwtToken or access_token
                    val token = body?.data?.access_token ?: body?.data?.jwtToken ?: ""
                    if (!token.isNullOrBlank()) {
                        repo.saveTokens(body)
                        repo.saveCredentials(clientCode, apiKey)
                        _state.value = AuthState.Success(token)
                    } else {
                        _state.value = AuthState.Error("Login succeeded but no token found")
                    }
                } else {
                    val msg = resp.errorBody()?.string() ?: "Login failed: ${resp.code()}"
                    _state.value = AuthState.Error(msg)
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun reset() {
        _state.value = AuthState.Idle
    }
}