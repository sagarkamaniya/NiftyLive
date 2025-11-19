package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.repository.NiftyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

// ✅ Create a simple data class to hold credentials for the UI
data class SavedCredentials(
    val clientCode: String,
    val password: String,
    val apiKey: String,
    val localIp: String,
    val publicIp: String,
    val macAddress: String
)

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: NiftyRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            if (repository.getAccessToken() == null) return@launch
            _authState.value = AuthState.Loading
            val isValid = repository.validateSession()
            if (isValid) {
                _authState.value = AuthState.Success("Welcome back!")
            } else {
                _authState.value = AuthState.Idle
            }
        }
    }

    fun login(totp: String) {
        if (totp.isBlank() || totp.length < 6) {
            _authState.value = AuthState.Error("A valid 6-digit TOTP is required")
            return
        }

        val clientCode = repository.getClientCode()
        val password = repository.getPassword()
        val apiKey = repository.getApiKey()
        val localIp = repository.getLocalIp()
        val publicIp = repository.getPublicIp()
        val macAddress = repository.getMacAddress()

        if (clientCode == null || password == null || apiKey == null || localIp == null || publicIp == null || macAddress == null) {
            _authState.value = AuthState.Error("Credentials not set. Please set up your credentials in settings.")
            return
        }

        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val response: Response<LoginResponse> = repository.loginWithCredentials(
                    clientCode = clientCode,
                    password = password,
                    apiKey = apiKey,
                    authCode = totp,
                    localIp = localIp,
                    publicIp = publicIp,
                    macAddress = macAddress
                )
                val body = response.body()
                if (response.isSuccessful && body?.data != null) {
                    repository.saveTokens(body)
                    _authState.value = AuthState.Success("Login successful")
                } else {
                    val errorMsg = body?.message ?: "Login failed: Invalid response"
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error: ${e.localizedMessage ?: "Unknown"}")
            }
        }
    }

    fun saveStaticCredentials(
        clientCode: String,
        password: String,
        apiKey: String,
        localIp: String,
        publicIp: String,
        macAddress: String
    ) {
        repository.saveCredentials(clientCode, password, apiKey, localIp, publicIp, macAddress)
    }

    // ✅ NEW FUNCTION: Get credentials to show in Settings
    fun getSavedCredentials(): SavedCredentials {
        return SavedCredentials(
            clientCode = repository.getClientCode() ?: "",
            password = repository.getPassword() ?: "",
            apiKey = repository.getApiKey() ?: "",
            localIp = repository.getLocalIp() ?: "",
            publicIp = repository.getPublicIp() ?: "",
            macAddress = repository.getMacAddress() ?: ""
        )
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
