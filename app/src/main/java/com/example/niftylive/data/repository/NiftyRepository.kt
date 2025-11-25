package com.example.niftylive.data.repository

import android.util.Log
import com.example.niftylive.data.api.SmartApiService
import com.example.niftylive.data.model.ExchangeTokens
import com.example.niftylive.data.model.Holding
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.OrderRequest
import com.example.niftylive.data.model.OrderResponse
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

    // --- 1. LOGIN (Fixed variable names) ---
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
            val response = api.login(apiKey, localIp, publicIp, macAddress, body)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Log.d("SmartAPI_LOGIN", "✅ Success")
                    return response
                }
            }

            val errorText = response.errorBody()?.string() ?: "Unknown Error"
            Log.e("SmartAPI_LOGIN_RAW", "Response: $errorText")

            val adapter = moshi.adapter(LoginResponse::class.java).lenient()
            val parsed = adapter.fromJson(errorText)
            
            if (parsed != null) {
                Response.success(parsed)
            } else {
                Response.error(500, errorText.toResponseBody("text/plain".toMediaType()))
            }
        } catch (e: Exception) {
            Response.error(500, "Exception: ${e.localizedMessage}".toResponseBody("text/plain".toMediaType()))
        }
    }

    // --- 2. AUTO-LOGIN ---
    suspend fun validateSession(): Boolean {
        val access = getAccessToken() ?: return false
        val apiKey = getApiKey() ?: return false
        val localIp = getLocalIp() ?: return false
        val publicIp = getPublicIp() ?: return false
        val macAddress = getMacAddress() ?: return false

        return try {
            val response = api.getProfile("Bearer $access", apiKey, localIp, publicIp, macAddress)
            response.isSuccessful && (response.body()?.status == true)
        } catch (e: Exception) {
            false
        }
    }

    // --- 3. CREDENTIALS ---
    fun saveTokens(loginData: LoginResponse?) {
        loginData?.data?.jwtToken?.let { prefs.saveString(KEY_ACCESS, it) }
        loginData?.data?.refreshToken?.let { prefs.saveString(KEY_REFRESH, it) }
        loginData?.data?.feedToken?.let { prefs.saveString(KEY_FEED, it) }
    }

    // Fixed variable names here too
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

    // --- 4. FETCH HOLDINGS ---
    suspend fun getHoldings(): ApiResult<List<Holding>> {
        val access = getAccessToken() ?: return ApiResult.Error("No token")
        val apiKey = getApiKey() ?: return ApiResult.Error("No key")
        val localIp = getLocalIp() ?: return ApiResult.Error("No localIp")
        val publicIp = getPublicIp() ?: return ApiResult.Error("No publicIp")
        val macAddress = getMacAddress() ?: return ApiResult.Error("No macAddress")

        try {
            val resp = api.getHoldings("Bearer $access", apiKey, localIp, publicIp, macAddress)
            if (resp.isSuccessful) {
                return ApiResult.Success(resp.body()?.data?.holdings ?: emptyList())
            } else {
                return ApiResult.Error(resp.errorBody()?.string() ?: "Error")
            }
        } catch (e: Exception) {
            return ApiResult.Error("Exception: ${e.localizedMessage}")
        }
    }

    // --- 5. FETCH LIVE QUOTES ---
    suspend fun getQuotesForList(tokens: List<String>): ApiResult<List<InstrumentQuote>> {
        val access = getAccessToken() ?: return ApiResult.Error("No token")
        val apiKey = getApiKey() ?: return ApiResult.Error("No key")
        val localIp = getLocalIp() ?: return ApiResult.Error("No localIp")
        val publicIp = getPublicIp() ?: return ApiResult.Error("No publicIp")
        val macAddress = getMacAddress() ?: return ApiResult.Error("No macAddress")

        val body = QuoteRequest("FULL", ExchangeTokens(tokens))
        try {
            val resp = api.getQuote("Bearer $access", apiKey, localIp, publicIp, macAddress, body)
            if (resp.isSuccessful) {
                return ApiResult.Success(resp.body()?.data?.fetched ?: emptyList())
            } else {
                return ApiResult.Error(resp.errorBody()?.string() ?: "API Error")
            }
        } catch (e: Exception) {
            return ApiResult.Error("Exception: ${e.localizedMessage}")
        }
    }

    // --- 6. GET FUNDS ---
    suspend fun getFunds(): ApiResult<String> {
        val access = getAccessToken() ?: return ApiResult.Error("No token")
        val apiKey = getApiKey() ?: return ApiResult.Error("No key")
        val localIp = getLocalIp() ?: ""
        val publicIp = getPublicIp() ?: ""
        val macAddress = getMacAddress() ?: ""

        try {
            val resp = api.getRMS("Bearer $access", apiKey, localIp, publicIp, macAddress)
            if (resp.isSuccessful && resp.body()?.status == true) {
                return ApiResult.Success(resp.body()?.data?.net ?: "0.00")
            }
            return ApiResult.Error(resp.errorBody()?.string() ?: resp.message())
        } catch (e: Exception) {
            return ApiResult.Error(e.localizedMessage ?: "Error")
        }
    }

    // --- 7. PLACE ORDER ---
    suspend fun placeOrder(request: OrderRequest): ApiResult<String> {
        val access = getAccessToken() ?: return ApiResult.Error("No token")
        val apiKey = getApiKey() ?: return ApiResult.Error("No key")
        val localIp = getLocalIp() ?: ""
        val publicIp = getPublicIp() ?: ""
        val macAddress = getMacAddress() ?: ""

        try {
            val resp = api.placeOrder("Bearer $access", apiKey, localIp, publicIp, macAddress, request)
            if (resp.isSuccessful && resp.body()?.status == true) {
                return ApiResult.Success(resp.body()?.data?.orderId ?: "Success")
            }
            return ApiResult.Error(resp.body()?.message ?: "Order Failed")
        } catch (e: Exception) {
            return ApiResult.Error(e.localizedMessage ?: "Error")
        }
    }
}
