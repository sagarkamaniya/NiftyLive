package com.example.niftylive.data.repository

import android.util.Log
import com.example.niftylive.data.api.SmartApiService
import com.example.niftylive.data.model.ExchangeTokens
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteRequest
import com.example.niftylive.data.model.QuoteResponse
import com.example.niftylive.utils.SecurePrefs
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
        const val KEY_PASSWORD = "password"
        const val KEY_LOCAL_IP = "local_ip"
        const val KEY_PUBLIC_IP = "public_ip"
        const val KEY_MAC_ADDRESS = "mac_address"
    }

    // --- 1. LOGIN FUNCTION ---
    suspend fun loginWithCredentials(
        clientCode: String,
        password: String,
        apiKey: String,
        authCode: String,
        localIp: String,
        publicIp: String,
        macAddress: String
    ): Response<LoginResponse> {

        val body = mapOf(
            "clientcode" to clientCode,
            "password" to password,
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
            Log.e("SmartAPI_LOGIN_ERR", "❌ Exception during login: ${e.localizedMessage}", e)
            Response.error(
                500,
                "Exception: ${e.localizedMessage}".toResponseBody("text/plain".toMediaType())
            )
        }
    }

    // --- 2. AUTO-LOGIN VALIDATION ---
    // This is the function needed for your Auto-Login feature
    suspend fun validateSession(): Boolean {
        val access = getAccessToken() ?: return false
        val apiKey = getApiKey() ?: return false
        val localIp = getLocalIp() ?: return false
        val publicIp = getPublicIp() ?: return false
        val macAddress = getMacAddress() ?: return false

        val bearer = "Bearer $access"

        return try {
            val response = api.getProfile(
                auth = bearer,
                apiKey = apiKey,
                localIp = localIp,
                publicIp = publicIp,
                macAddress = macAddress
            )
            // If status is true, the token is valid
            response.isSuccessful && (response.body()?.status == true)
        } catch (e: Exception) {
            Log.e("SmartAPI_SESSION", "Session validation failed: ${e.localizedMessage}")
            false
        }
    }

    // --- 3. CREDENTIAL MANAGEMENT ---
    fun saveTokens(loginData: LoginResponse?) {
        loginData?.data?.jwtToken?.let { prefs.saveString(KEY_ACCESS, it) }
        loginData?.data?.refreshToken?.let { prefs.saveString(KEY_REFRESH, it) }
        loginData?.data?.feedToken?.let { prefs.saveString(KEY_FEED, it) }
    }

    fun saveCredentials(
        clientCode: String,
        password: String,
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

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS)
    fun getFeedToken(): String? = prefs.getString(KEY_FEED)
    fun getClientCode(): String? = prefs.getString(KEY_CLIENT)
    fun getApiKey(): String? = prefs.getString(KEY_APIKEY)
    fun getPassword(): String? = prefs.getString(KEY_PASSWORD)
    fun getLocalIp(): String? = prefs.getString(KEY_LOCAL_IP)
    fun getPublicIp(): String? = prefs.getString(KEY_PUBLIC_IP)
    fun getMacAddress(): String? = prefs.getString(KEY_MAC_ADDRESS)


    // --- 4. QUOTE FETCHING ---
    // This uses the fixed QuoteRequest object to prevent crashes
    suspend fun getQuoteForToken(token: String): ApiResult<InstrumentQuote> {
        val access = getAccessToken() ?: return ApiResult.Error("No access token found.")
        val apiKey = getApiKey() ?: return ApiResult.Error("No apiKey found.")
        val localIp = getLocalIp() ?: return ApiResult.Error("No localIp found.")
        val publicIp = getPublicIp() ?: return ApiResult.Error("No publicIp found.")
        val macAddress = getMacAddress() ?: return ApiResult.Error("No macAddress found.")

        val bearer = "Bearer $access"

        // Create the QuoteRequest Object
        val exchangeTokens = ExchangeTokens(nse = listOf(token))
        val body = QuoteRequest(
            mode = "FULL",
            exchangeTokens = exchangeTokens
        )

        try {
            val resp: Response<QuoteResponse> = api.getQuote(
                auth = bearer,
                apiKey = apiKey,
                localIp = localIp,
                publicIp = publicIp,
                macAddress = macAddress,
                body = body
            )

            if (resp.isSuccessful) {
                val quote = resp.body()?.data?.fetched?.firstOrNull()
                if (quote != null) {
                    return ApiResult.Success(quote)
                } else {
                    return ApiResult.Error("API returned success but 'fetched' list was empty.")
                }
            } else {
                val errorBodyString = resp.errorBody()?.string() ?: "Unknown HTTP error"
                Log.e("SmartAPI_QUOTE_ERROR", "Error body: $errorBodyString")
                return ApiResult.Error(errorBodyString)
            }

        } catch (e: Exception) {
            Log.e("SmartAPI_QUOTE_ERR", "Exception: ${e.localizedMessage}", e)
            return ApiResult.Error("Exception: ${e.localizedMessage ?: "Unknown Error"}")
        }
    }
}
