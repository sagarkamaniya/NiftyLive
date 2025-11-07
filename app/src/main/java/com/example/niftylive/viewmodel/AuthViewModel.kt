package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.repository.NiftyRepository
import com.example.niftylive.data.model.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val response: LoginResponse?) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: NiftyRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(clientCode: String, password: String, apiKey: String, totp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                // ✅ Get API response
                val response = repository.loginWithCredentials(clientCode, password, apiKey, totp)

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse?.data?.access_token != null) {
                        repository.saveTokens(loginResponse)
                        repository.saveCredentials(clientCode, apiKey)
                        _authState.value = AuthState.Success(loginResponse)
                        Log.d("AUTH", "✅ Login success: ${loginResponse.data.access_token}")
                    } else {
                        _authState.value = AuthState.Error(
                            "Login failed: ${loginResponse?.message ?: "Empty login response"}"
                        )
                        Log.e("AUTH", "⚠️ Login failed with empty or null token")
                    }
                } else {
                    val errorText = response.errorBody()?.string() ?: "Unknown error"
                    _authState.value = AuthState.Error("Login failed: $errorText")
                    Log.e("AUTH", "❌ Login failed: $errorText")
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error("Exception: ${e.localizedMessage}")
                Log.e("AUTH", "❌ Exception: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}