package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// UI States for login
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

    /**
     * Handles SmartAPI Login process.
     * Performs field validation before making network call.
     */
    fun login(clientCode: String, password: String, apiKey: String, authCode: String) {
        viewModelScope.launch {
            // 🛑 Step 1: Validate input fields
            if (clientCode.isBlank() || password.isBlank() || apiKey.isBlank() || authCode.isBlank()) {
                _state.value = AuthState.Error("All fields are required.")
                return@launch
            }

            _state.value = AuthState.Loading

            try {
                // 🧠 Step 2: Call repository to make API request
                val resp = repo.loginWithCredentials(clientCode, password, apiKey, authCode)

                // 🧩 Step 3: Check for valid SmartAPI response
                val responseBody = resp.body()
                if (resp.isSuccessful && responseBody?.data?.access_token != null) {
                    repo.saveTokens(responseBody)
                    repo.saveCredentials(clientCode, apiKey)
                    _state.value = AuthState.Success(responseBody.data.access_token!!)
                } else {
                    val errorMsg = resp.errorBody()?.string()
                        ?: responseBody?.message
                        ?: "Login failed (${resp.code()})"
                    _state.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                // 🧱 Step 4: Catch and display network or parsing errors
                _state.value = AuthState.Error(e.localizedMessage ?: "Network error occurred.")
            }
        }
    }

    /**
     * Resets login state (useful when navigating back to login screen)
     */
    fun resetState() {
        _state.value = AuthState.Idle
    }
}