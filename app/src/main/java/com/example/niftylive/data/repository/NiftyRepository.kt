package com.example.niftylive.data.repository

import android.util.Log
import com.example.niftylive.data.api.SmartApiService
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteResponse
import com.example.niftylive.utils.SecurePrefs
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject


class NiftyRepository @Inject constructor(
    private val api: SmartApiService,
    private val prefs: SecurePrefs,
    private val moshi: Moshi
) {

    companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_FEED = "feed_token"
        const val KEY_CLIENT = "client_code"
        const val KEY_APIKEY = "api_key"

        // ✅ ADDED NEW KEYS
        const val KEY_PASSWORD = "password" // This will be your MPIN
        const val KEY_LOCAL_IP = "local_ip"
        const val KEY_PUBLIC_IP = "public_ip"
        const val KEY_MAC_ADDRESS = "mac_address"
    }

    /**
     * Login with SmartAPI credentials.
     * ✅ UPDATED
     */
    suspend fun loginWithCredentials(
        clientCode: String,
        password: String, // This is your MPIN
        apiKey: String,
        authCode: String, // This is your TOTP
        localIp: String,
        publicIp: String,
        macAddress: String
    ): Response<LoginResponse> {

        val body = mapOf(
            "clientcode" to clientCode,
            "password" to password, // "password" key maps to your PIN
            "totp" to authCode
        )

        return try {
            val response = api.login(
                apiKey = apiKey,
                localIp = localIp,
                publicIp = publicIp,
                macAddress = macAddress,
                body = body
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Log.d("SmartAPI_LOGIN", "✅ Parsed LoginResponse: $responseBody")
                    return response
                }
            }

            val errorText = response.errorBody()?.string()
                ?: "Empty or malformed SmartAPI response"

            Log.e("SmartAPI_LOGIN_RAW", "Response: $errorText")

            val adapter: JsonAdapter<LoginResponse> =
                moshi.adapter(LoginResponse::class.java).lenient()

            val parsed = adapter.fromJson(errorText)
            if (parsed != null) {
                Log.d("SmartAPI_LOGIN_PARSE", "✅ Lenient parsed: $parsed")
                Response.success(parsed)
            } else {
                Log.e("SmartAPI_LOGIN_PARSE", "❌ Could not parse response")
                Response.error(
                    500,
                    "Invalid or empty response: $errorText".toResponseBody("text/plain".toMediaType())
                )
            }

        } catch (e: Exception) {
            Log.e("SmartAPI_LOGIN_ERR", "❌ Exception during login: ${e.localizedMessage}")
            Response.error(
                500,
                "Exception: ${e.localizedMessage}".toResponseBody("text/plain".toMediaType())
            )
        }
    }

    /** Save tokens securely */
    fun saveTokens(loginData: LoginResponse?) {
        // ✅ UPDATED based on new LoginResponse model
        loginData?.data?.jwtToken?.let { prefs.saveString(KEY_ACCESS, it) }
        loginData?.data?.refreshToken?.let { prefs.saveString(KEY_REFRESH, it) }
        loginData?.data?.feedToken?.let { prefs.saveString(KEY_FEED, it) }
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS)
    fun getFeedToken(): String? = prefs.getString(KEY_FEED)

    // ✅ UPDATED to save all static data
    fun saveCredentials(
        clientCode: String,
        password: String, // Your MPIN
        apiKey: String,
        localIp: String,
        publicIp: String,
        macAddress: String
    ) {
        prefs.saveString(KEY_CLIENT, clientCode)
        prefs.saveString(KEY_PASSWORD, password)
        prefs.saveString(KEY_APIKEY, apiKey)
        prefs.saveString(KEY_LOCAL_IP, localIp)
        prefs.saveString(KEY_PUBLIC_IP, publicIp)
        prefs.saveString(KEY_MAC_ADDRESS, macAddress)
    }

    // ✅ ADDED NEW GETTERS
    fun getClientCode(): String? = prefs.getString(KEY_CLIENT)
    fun getApiKey(): String? = prefs.getString(KEY_APIKEY)
    fun getPassword(): String? = prefs.getString(KEY_PASSWORD)
    fun getLocalIp(): String? = prefs.getString(KEY_LOCAL_IP)
    fun getPublicIp(): String? = prefs.getString(KEY_PUBLIC_IP)
    fun getMacAddress(): String? = prefs.getString(KEY_MAC_ADDRESS)


    /** Fetch live quote for an instrument token */
    suspend fun getQuoteForToken(token: String): InstrumentQuote? {
        val access = getAccessToken() ?: return null
        val apiKey = getApiKey() ?: return null
        val bearer = "Bearer $access"

        val body = mapOf(
            "mode" to "FULL",
            "exchangeTokens" to mapOf("NSE" to listOf(token))
        )
        
        // ✅ TODO: This will fail. You must update this API call
        // based on the new API documentation.
        val resp: Response<QuoteResponse> = api.getQuote(bearer, apiKey, body)
        
        if (resp.isSuccessful) {
            return resp.body()?.data?.values?.firstOrNull()
        }
        Log.e("SmartAPI_QUOTE", "Failed: ${resp.errorBody()?.string()}")
        return null
    }
}
