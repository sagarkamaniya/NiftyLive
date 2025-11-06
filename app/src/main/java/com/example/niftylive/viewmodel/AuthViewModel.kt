package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.repository.NiftyRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log

class AuthViewModel(
    private val repository: NiftyRepository
) : ViewModel() {

    private val _loginStatus = MutableStateFlow<String?>(null)
    val loginStatus: StateFlow<String?> = _loginStatus

    /**
     * Handle SmartAPI login
     */
    fun login(clientCode: String, password: String, apiKey: String, authCode: String) {
        viewModelScope.launch {
            //  Validate all fields
            if (clientCode.isBlank() || password.isBlank() || apiKey.isBlank() || authCode.isBlank()) {
                _loginStatus.value = "Please fill all fields"
                return@launch
            }

            try {
                //  Attempt login
                val response = repository.loginWithCredentials(
                    clientCode.trim(),
                    password.trim(),
                    apiKey.trim(),
                    authCode.trim()
                )

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    //  Success case
                    if (loginResponse?.status.equals("success", ignoreCase = true)
                        || loginResponse?.data?.access_token != null
                    ) {
                        repository.saveTokens(loginResponse)
                        _loginStatus.value =
                            "Login Successful! Token: ${loginResponse?.data?.access_token ?: "Unknown"}"
                        Log.i("SMARTAPI_LOGIN", "Login successful for $clientCode")
                    } else {
                        //  SmartAPI returned 200 but status = false
                        val errorMsg =
                            response.errorBody()?.string()
                                ?: "SmartAPI returned failure response"
                        Log.e("SMARTAPI_LOGIN_FAIL", errorMsg)
                        _loginStatus.value =
                            "Error: ${loginResponse?.status ?: "Invalid credentials"}"
                    }
                } else {
                    //  Non-200 error (HTTP 4xx/5xx)
                    val errorMsg = response.errorBody()?.string() ?: "Unknown server error"
                    Log.e("SMARTAPI_HTTP_FAIL", "HTTP ${response.code()} - $errorMsg")
                    _loginStatus.value = "Error: Login failed (${response.code()})"
                }
            } catch (e: Exception) {
                //  Catch any network or parsing exceptions
                Log.e("SMARTAPI_EXCEPTION", e.localizedMessage ?: "Unknown exception")
                _loginStatus.value =
                    "Error: ${e.localizedMessage ?: "Unexpected exception occurred"}"
            }
        }
    }

    fun clearStatus() {
        _loginStatus.value = null
    }
}