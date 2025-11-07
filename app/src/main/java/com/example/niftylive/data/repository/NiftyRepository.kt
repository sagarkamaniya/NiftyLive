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
     * This method captures the raw response for debugging.
     */
    suspend fun loginWithCredentials(
        clientCode: String,
        password: String,
        apiKey: String,
        authCode: String
    ): Pair<LoginResponse?, String> {
        val body = mapOf(
            "clientcode" to clientCode,
            "password" to password,
            "totp" to authCode
        )

        return try {
            val response = api.login(apiKey, body)

            val rawString = response.errorBody()?.string()
                ?: response.body()?.toString()
                ?: "⚠️ Empty response from SmartAPI"

            Log.e("SMARTAPI_RAW", rawString)

            if (response.isSuccessful && response.body() != null) {
                return Pair(response.body(), "✅ Parsed OK")
            }

            // Try parsing manually in case SmartAPI sent plain text or broken JSON
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(LoginResponse::class.java).lenient()

            return try {
                val parsed = adapter.fromJson(rawString)
                Pair(parsed, "Parsed manually: $rawString")
            } catch (e: Exception) {
                Log.e("SMARTAPI_PARSE", "Error: ${e.localizedMessage}")
                Pair(null, "❌ Failed to parse. Raw: $rawString")
            }
        } catch (e: Exception) {
            Log.e("SMARTAPI_CALL", "Exception: ${e.localizedMessage}")
            Pair(null, "❌ Exception: ${e.localizedMessage}")
        }
    }

    /** Save tokens after successful login */
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

    /** Fetch live quote for a given token */
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