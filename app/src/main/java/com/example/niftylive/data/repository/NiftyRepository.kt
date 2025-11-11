package com.example.niftylive.data.repository

import android.util.Log
import com.example.niftylive.data.api.SmartApiService
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteResponse
import com.example.niftylive.utils.SecurePrefs // Make sure this file exists
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
        const val KEY_PASSWORD = "password" // This will be your MPIN
        const val KEY_LOCAL_IP = "local_ip"
        const val KEY_PUBLIC_IP = "public_ip"
        const val KEY_MAC_ADDRESS = "mac_address"
    }

    /**
     * Login with SmartAPI credentials.
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

            // Handle raw or malformed responses
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
        loginData?.data?.jwtToken?.let { prefs.saveString(KEY_ACCESS, it) }
        loginData?.data?.refreshToken?.let { prefs.saveString(KEY_REFRESH, it) }
        loginData?.data?.feedToken?.let { prefs.saveString(KEY_FEED, it) }
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS)
    fun getFeedToken(): String? = prefs.getString(KEY_FEED)

    /** Save all static credentials */
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

    // Getters for all saved credentials
    fun getClientCode(): String? = prefs.getString(KEY_CLIENT)
    fun getApiKey(): String? = prefs.getString(KEY_APIKEY)
    fun getPassword(): String? = prefs.getString(KEY_PASSWORD)
    fun getLocalIp(): String? = prefs.getString(KEY_LOCAL_IP)
    fun getPublicIp(): String? = prefs.getString(KEY_PUBLIC_IP)
    fun getMacAddress(): String? = prefs.getString(KEY_MAC_ADDRESS)

    /** Fetch live quote for an instrument token */
    suspend fun getQuoteForToken(token: String): InstrumentQuote? {
        // 1. Get all the required credentials
        val access = getAccessToken() ?: run {
            Log.w("SmartAPI_QUOTE", "No access token found.")
            return null
        }
        val apiKey = getApiKey() ?: run {
            Log.w("SmartAPI_QUOTE", "No apiKey found.")
            return null
        }
        val localIp = getLocalIp() ?: run {
            Log.w("SmartAPI_QUOTE", "No localIp found.")
            return null
        }
        val publicIp = getPublicIp() ?: run {
            Log.w("SmartAPI_QUOTE", "No publicIp found.")
            return null
        }
        val macAddress = getMacAddress() ?: run {
            Log.w("SmartAPI_QUOTE", "No macAddress found.")
            return null
        }

        val bearer = "Bearer $access"
        val body = mapOf(
            "mode" to "FULL",
            "exchangeTokens" to mapOf(
                "NSE" to listOf(token)
            )
        )

        // Aggressive logging for debug
        Log.d("SmartAPI_QUOTE_REQ", "token=$token, body=$body, access=$access, apiKey=$apiKey, localIp=$localIp, publicIp=$publicIp, macAddress=$macAddress")

        try {
            val resp: Response<QuoteResponse> = api.getQuote(
                auth = bearer,
                apiKey = apiKey,
                localIp = localIp,
                publicIp = publicIp,
                macAddress = macAddress,
                body = body
            )

            // Log network result
            Log.d("SmartAPI_QUOTE_RAW", "Raw: ${resp.raw()}")
            Log.d("SmartAPI_QUOTE_CODE", "HTTP Code: ${resp.code()}")
            Log.d("SmartAPI_QUOTE_HEADERS", "Headers: ${resp.headers()}")

            try {
                val responseBody = resp.body()
                Log.d("SmartAPI_QUOTE_BODY", "Body: $responseBody")
                if (resp.isSuccessful) {
                    val fetched = responseBody?.data?.fetched
                    if (fetched.isNullOrEmpty()) {
                        Log.e("SmartAPI_QUOTE", "Fetched list is empty or null! Body: $responseBody")
                        // Still return null so your ViewModel can handle with error message
                        return null
                    }
                    return fetched.firstOrNull()
                } else {
                    val errorBodyString =
                        try { resp.errorBody()?.string() } catch (e: Exception) { "error reading errorBody: $e" }
                    Log.e("SmartAPI_QUOTE_ERR", "API call FAILED. Code: ${resp.code()} Body: $errorBodyString")
                }
            } catch (jp: Exception) {
                Log.e("SmartAPI_QUOTE_PARSE", "Could not parse response: ${jp.localizedMessage}")
            }

            return null

        } catch (e: Exception) {
            Log.e("SmartAPI_QUOTE_ERR", "Exception thrown: ${e.localizedMessage}", e)
            return null
        }
    }
}