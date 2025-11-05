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

    /**
     * Logs in to SmartAPI using client credentials and TOTP (authCode).
     * SmartAPI requires JSON body with `clientcode`, `password`, `totp`.
     * The API key must be sent as X-PrivateKey header.
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

    // Call SmartAPI login
    val rawResponse = api.login(apiKey, body)

    if (rawResponse.isSuccessful) {
        val bodyString = rawResponse.body() ?: return Response.error(
            400,
            okhttp3.ResponseBody.create(null, "Empty")
        )

        return try {
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            val adapter = moshi.adapter(LoginResponse::class.java)
            val parsed = adapter.fromJson(bodyString)
            Response.success(parsed)
        } catch (e: Exception) {
            Response.error(
                500,
                okhttp3.ResponseBody.create(null, "Invalid JSON: ${e.localizedMessage}")
            )
        }
    } else {
        return Response.error(rawResponse.code(), rawResponse.errorBody()!!)
    }
}

    /** Save tokens from SmartAPI login response */
fun saveTokens(loginData: LoginResponse?) {
    loginData?.data?.jwtToken?.let { prefs.saveString(KEY_ACCESS, it) }
    loginData?.data?.refreshToken?.let { prefs.saveString(KEY_REFRESH, it) }
    loginData?.data?.feedToken?.let { prefs.saveString(KEY_FEED, it) }
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