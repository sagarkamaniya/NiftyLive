package com.example.niftylive.data.repository

import com.example.niftylive.data.api.SmartApiService
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteResponse
import com.example.niftylive.utils.SecurePrefs
import retrofit2.Response

class NiftyRepository(
    private val api: SmartApiService,
    private val prefs: SecurePrefs
) {
    companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_FEED = "feed_token"
        const val KEY_CLIENT = "client_code"
        const val KEY_APIKEY = "api_key"
    }

    suspend fun loginWithCredentials(clientCode: String, password: String, apiKey: String, authCode: String): Response<LoginResponse> {
        // TODO: adapt body according to SmartAPI login endpoint you use
        val body = mapOf(
            "client_code" to clientCode,
            "password" to password,
            "api_key" to apiKey,
            "auth_code" to authCode
        )
        return api.login(body)
    }

    fun saveTokens(loginData: LoginResponse?) {
        loginData?.data?.access_token?.let { prefs.saveString(KEY_ACCESS, it) }
        loginData?.data?.refresh_token?.let { prefs.saveString(KEY_REFRESH, it) }
        loginData?.data?.feed_token?.let { prefs.saveString(KEY_FEED, it) }
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS)
    fun getFeedToken(): String? = prefs.getString(KEY_FEED)
    fun saveCredentials(clientCode: String, apiKey: String) {
        prefs.saveString(KEY_CLIENT, clientCode)
        prefs.saveString(KEY_APIKEY, apiKey)
    }
    fun getClientCode(): String? = prefs.getString(KEY_CLIENT)
    fun getApiKey(): String? = prefs.getString(KEY_APIKEY)

    suspend fun getQuoteForToken(token: String): InstrumentQuote? {
        val access = getAccessToken() ?: return null
        val bearer = "Bearer $access"
        // body structure assumed by SmartAPI - adjust if needed
        val body: Map<String, Any> = mapOf(
            "mode" to "FULL",
            "exchangeTokens" to mapOf("NSE" to listOf(token))
        )
        val resp: Response<QuoteResponse> = api.getQuote(bearer, body)
        if (resp.isSuccessful) {
            val q = resp.body()?.data?.values?.firstOrNull()
            return q
        }
        return null
    }
}