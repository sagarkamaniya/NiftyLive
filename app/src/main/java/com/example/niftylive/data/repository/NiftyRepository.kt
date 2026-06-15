package com.example.niftylive.data.repository

import com.example.niftylive.data.OkHttpWsClient
import com.example.niftylive.data.api.SmartApiService
import com.example.niftylive.data.model.LoginRequest
import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.MarketTick
import com.example.niftylive.utils.SecurePrefs
import kotlinx.coroutines.flow.SharedFlow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NiftyRepository @Inject constructor(
    private val apiService: SmartApiService,
    private val wsClient: OkHttpWsClient,
    private val securePrefs: SecurePrefs // Injects your existing SecurePrefs helper
) {
    val liveTicks: SharedFlow<MarketTick> = wsClient.ticks

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }

    fun startLiveFeed(userId: String, sessionToken: String, scrips: List<String>) {
        wsClient.connect(userId, sessionToken)
        wsClient.subscribe(scrips)
    }

    fun stopFeed() {
        wsClient.disconnect()
    }

    // --- Restored Credential Management for AuthViewModel ---
    fun getClientCode(): String? = securePrefs.getClientCode()
    fun getPassword(): String? = securePrefs.getPassword()
    fun getApiKey(): String? = securePrefs.getApiKey()
    fun getLocalIp(): String? = securePrefs.getLocalIp()
    fun getPublicIp(): String? = securePrefs.getPublicIp()
    fun getMacAddress(): String? = securePrefs.getMacAddress()

    fun saveCredentials(
        clientCode: String,
        password: String,
        apiKey: String,
        localIp: String,
        publicIp: String,
        macAddress: String
    ) {
        securePrefs.saveCredentials(clientCode, password, apiKey, localIp, publicIp, macAddress)
    }
}
