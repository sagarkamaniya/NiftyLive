package com.example.niftylive.data.repository

import android.util.Log
import com.example.niftylive.data.api.SmartApiService
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteResponse
import com.example.niftylive.utils.SecurePrefs
import okhttp3.ResponseBody
import retrofit2.Response
import com.squareup.moshi.Moshi

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
    ): Response<LoginResponse> {
        val body = mapOf(
            "clientcode" to clientCode,
            "password" to password,
            "totp" to authCode
        )

        val rawResponse = api.login(apiKey, body)

        // ✅ If response is JSON, just return as-is
        if (rawResponse.isSuccessful && rawResponse.body() != null) {
            return rawResponse
        }

        // ✅ If SmartAPI sent text instead of JSON, handle manually
        val rawText = rawResponse.errorBody()?.string()
            ?: rawResponse.body()?.toString()
            ?: "Empty or malformed response from SmartAPI"

        Log.e("SmartAPI_RAW", rawText)

        // Try parsing manually with lenient Moshi (to avoid crash)
        return try {
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(LoginResponse::class.java).lenient()
            val parsed = adapter.fromJson(rawText)
            if (parsed != null) {
                Response.success(parsed)
            } else {
                Response.error(500, ResponseBody.create(null, rawText))
            }
        } catch (e: Exception) {
            Log.e("SmartAPI_PARSE", "Invalid JSON: ${e.localizedMessage}")
            Response.error(500, ResponseBody.create(null, "Invalid response: $rawText"))
        }
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
        if (resp.isSuccessful) {
            return resp.body()?.data?.values?.firstOrNull()
        }
        return null
    }
}