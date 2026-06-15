package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.model.LoginRequest
import com.example.niftylive.data.repository.NiftyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

// ✅ Maintained old names to prevent Compose UI compile errors.
// clientCode = User ID | localIp = Vendor Code | macAddress = IMEI
data class SavedCredentials(
    val clientCode: String,
    val password: String,
    val apiKey: String,
    val localIp: String, 
    val publicIp: String, // Unused by Shoonya
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
            // Adjust validation based on your repository's existing logic
            if (repository.getClientCode() == null) return@launch
            // Assuming your repository handles session cache logic here
            _authState.value = AuthState.Idle 
        }
    }

    fun login(totp: String) {
        if (totp.isBlank() || totp.length < 6) {
            _authState.value = AuthState.Error("A valid TOTP is required")
            return
        }

        // Fetching mapped credentials from local secure preferences
        val userId = repository.getClientCode()
        val password = repository.getPassword()
        val apiKey = repository.getApiKey()
        val vendorCode = repository.getLocalIp()
        val imei = repository.getMacAddress() ?: "niftylive_app"

        if (userId.isNullOrBlank() || password.isNullOrBlank() || apiKey.isNullOrBlank() || vendorCode.isNullOrBlank()) {
            _authState.value = AuthState.Error("Credentials missing. Please update them in settings.")
            return
        }

        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                
                // Shoonya requires the appKey to be a SHA-256 hash of "uid|api_key"
                val hashedAppKey = generateShoonyaAppKey(userId, apiKey)

                val request = LoginRequest(
                    userId = userId,
                    password = password,
                    totp = totp,
                    vendorCode = vendorCode,
                    appKey = hashedAppKey,
                    imei = imei,
                    source = "API"
                )

                val response = repository.login(request)
                val body = response.body()

                if (response.isSuccessful && body?.status == "Ok") {
                    // Success! Store the new Shoonya susertoken if your repository supports it
                    // repository.saveSessionToken(body.sessionToken)
                    _authState.value = AuthState.Success("Login successful")
                } else {
                    val errorMsg = body?.errorMessage ?: "Login failed: Invalid credentials or TOTP"
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network Error: ${e.localizedMessage ?: "Unknown"}")
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

    /**
     * Shoonya API securely expects the appKey parameter to be a strict 
     * SHA256 hashed string combining your User ID and App API Key separated by a pipe.
     */
    private fun generateShoonyaAppKey(userId: String, apiKey: String): String {
        val input = "$userId|$apiKey"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
