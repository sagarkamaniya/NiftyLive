package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.repository.NiftyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import com.example.niftylive.data.model.LoginResponse

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: NiftyRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(clientCode: String, password: String, apiKey: String, totp: String) {
        // Input validation
        if (clientCode.isBlank() || password.isBlank() || apiKey.isBlank() || totp.isBlank()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val response: Response<LoginResponse> = repository.loginWithCredentials(
                    clientCode = clientCode,
                    password = password,
                    apiKey = apiKey,
                    authCode = totp
                )

                val body = response.body()

                if (response.isSuccessful && body?.data?.access_token != null) {
                    repository.saveTokens(body)
                    repository.saveCredentials(clientCode, apiKey)
                    _authState.value = AuthState.Success("Login successful")
                } else {
                    _authState.value = AuthState.Error("Login failed: Invalid response")
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error: ${e.localizedMessage ?: "Unknown"}")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}