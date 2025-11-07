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

class AuthViewModel(
    private val repo: NiftyRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _token = MutableStateFlow("")
    val token = _token.asStateFlow()

    /**
     * Attempts SmartAPI login and shows full raw response if something fails
     */
    fun login(clientCode: String, password: String, apiKey: String, authCode: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                val (response, rawText) = repo.loginWithCredentials(
                    clientCode = clientCode,
                    password = password,
                    apiKey = apiKey,
                    authCode = authCode
                )

                if (response.isSuccessful && response.body()?.data?.jwtToken != null) {
                    val data = response.body()!!.data!!
                    repo.saveTokens(response.body())
                    _token.value = data.jwtToken ?: ""
                    _authState.value = AuthState.Success("✅ Login Successful! Token saved.")
                } else {
                    _authState.value = AuthState.Error("Invalid response:\n$rawText")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Exception: ${e.localizedMessage}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.saveTokens(null)
            _token.value = ""
            _authState.value = AuthState.Idle
        }
    }
}