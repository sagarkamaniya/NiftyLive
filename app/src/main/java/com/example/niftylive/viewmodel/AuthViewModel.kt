package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.repository.NiftyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repo: NiftyRepository) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state = _state.asStateFlow()

    val clientCode = MutableStateFlow(repo.getClientCode() ?: "")
    val password = MutableStateFlow("")
    val apiKey = MutableStateFlow(repo.getApiKey() ?: "")
    val totp = MutableStateFlow("")

    fun login() {
        val code = clientCode.value.trim()
        val pass = password.value.trim()
        val key = apiKey.value.trim()
        val otp = totp.value.trim()

        if (code.isEmpty() || pass.isEmpty() || key.isEmpty() || otp.isEmpty()) {
            _state.value = AuthState.Error("⚠️ Please fill in all fields.")
            return
        }

        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val (response, rawText) = repo.loginWithCredentials(code, pass, key, otp)
                if (response.isSuccessful && response.body()?.data?.access_token != null) {
                    repo.saveTokens(response.body())
                    repo.saveCredentials(code, key)
                    _state.value = AuthState.Success("✅ Login successful")
                } else {
                    _state.value = AuthState.Error("Login failed: $rawText")
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error("Exception: ${e.localizedMessage}")
            }
        }
    }
}