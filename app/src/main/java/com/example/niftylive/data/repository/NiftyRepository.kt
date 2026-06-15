package com.example.niftylive.data.repository

import com.example.niftylive.data.OkHttpWsClient
import com.example.niftylive.data.api.SmartApiService
import com.example.niftylive.data.model.Holding
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.model.LoginRequest
import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.MarketTick
import com.example.niftylive.data.model.OrderRequest
import com.example.niftylive.utils.SecurePrefs
import kotlinx.coroutines.flow.SharedFlow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NiftyRepository @Inject constructor(
    private val apiService: SmartApiService,
    private val wsClient: OkHttpWsClient,
    private val securePrefs: SecurePrefs
) {
    
    // ==========================================
    // 1. LIVE TICKER STREAM
    // ==========================================
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

    // ==========================================
    // 2. CREDENTIAL MANAGEMENT
    // ==========================================
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

    // ==========================================
    // 3. DASHBOARD API CALLS
    // ==========================================
    suspend fun getHoldings(): ApiResult<List<Holding>> {
        // Returning an empty success list to satisfy compiler
        // TODO: Wire up to Shoonya's NorenOMS GetHoldings endpoint
        return ApiResult.Success(emptyList())
    }

    suspend fun getQuotesForList(tokens: List<String>): ApiResult<List<InstrumentQuote>> {
        // Returning an empty success list to satisfy compiler
        // TODO: Wire up to Shoonya's NorenOMS GetQuotes endpoint
        return ApiResult.Success(emptyList())
    }

    suspend fun getFunds(): ApiResult<String> {
        // Returning dummy funds to satisfy compiler
        // TODO: Wire up to Shoonya's NorenOMS GetLimits endpoint
        return ApiResult.Success("0.00")
    }

    suspend fun placeOrder(request: OrderRequest): ApiResult<String> {
        // Returning a dummy success string to satisfy compiler
        // TODO: Wire up to Shoonya's NorenOMS PlaceOrder endpoint
        return ApiResult.Success("SHOONYA_ORDER_PLACEHOLDER")
    }

    fun saveTokens(tokens: Any?) {
        if (tokens == null) {
            // Clears local session if logout is pressed
            securePrefs.saveCredentials("", "", "", "", "", "")
        }
    }
}
