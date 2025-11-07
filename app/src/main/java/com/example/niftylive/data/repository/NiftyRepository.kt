package com.example.niftylive.data.repository

import android.util.Log
import com.example.niftylive.data.api.SmartApiService
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteResponse
import com.example.niftylive.utils.SecurePrefs
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
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

    /**
     * Logs in to SmartAPI using client credentials and TOTP (authCode).
     * Automatically handles plain-text or invalid JSON responses.
     */
    suspend fun loginWithCredentials(
        clientCode: String,
        password: String,
        apiKey: String,
        authCode: String
    ): Pair<Response<LoginResponse>, String> {
        val body = mapOf(
            "clientcode" to clientCode,
            "password" to password,
            "totp" to authCode
        )

        val rawResponse = api.login(apiKey, body)
        val rawText = try {
            rawResponse.errorBody()?.string()
                ?: rawResponse.body().toString()
                ?: "Empty response"
        } catch (e: Exception) {
            "Error parsing raw body: ${e.localizedMessage}"
        }

        Log.d("SmartAPI", "Raw: $rawText")
        return Pair(rawResponse, rawText)
    }

    /** Save tokens from SmartAPI login response */
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

    /** Fetch live quote for an instrument token */
    suspend fun getQuoteForToken(token: String): InstrumentQuote? {
        val access = getAccessToken() ?: return null
        val apiKey = getApiKey() ?: return null
        val bearer = "Bearer $access"

        val body = mapOf(
            "mode" to "FULL",
            "exchangeTokens" to mapOf("NSE" to listOf(token))
        )

        val resp: Response<QuoteResponse> = api.getQuote(bearer, apiKey, body)
        return if (resp.isSuccessful) resp.body()?.data?.values?.firstOrNull() else null
    }
}