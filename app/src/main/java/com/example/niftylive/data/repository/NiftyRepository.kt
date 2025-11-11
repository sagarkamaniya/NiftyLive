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
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import com.example.niftylive.data.repository.ApiResult // <-- 1. IMPORT THE NEW CLASS

@Singleton
class NiftyRepository @Inject constructor(
    private val api: SmartApiService,
    private val prefs: SecurePrefs,
    private val moshi: Moshi
) {

    // ... (Your companion object, loginWithCredentials, and all save/get functions are perfect, no changes needed there) ...
    // ...
    // ...
    
    /** Fetch live quote for an instrument token */
    // ✅ THIS IS THE UPDATED FUNCTION
    suspend fun getQuoteForToken(token: String): ApiResult<InstrumentQuote> { // <-- 2. CHANGE RETURN TYPE
        // 1. Get all the required credentials
        val access = getAccessToken() ?: return ApiResult.Error("No access token found.") // <-- 3. RETURN ERROR
        val apiKey = getApiKey() ?: return ApiResult.Error("No apiKey found.")
        val localIp = getLocalIp() ?: return ApiResult.Error("No localIp found.")
        val publicIp = getPublicIp() ?: return ApiResult.Error("No publicIp found.")
        val macAddress = getMacAddress() ?: return ApiResult.Error("No macAddress found.")

        val bearer = "Bearer $access"

        // 2. Create the new request body
        val body = mapOf(
            "mode" to "FULL",
            "exchangeTokens" to mapOf(
                "NSE" to listOf(token)
            )
        )

        try {
            // 3. Call the API
            val resp: Response<QuoteResponse> = api.getQuote(
                auth = bearer,
                apiKey = apiKey,
                localIp = localIp,
                publicIp = publicIp,
                macAddress = macAddress,
                body = body
            )

            // 4. Check the response
            if (resp.isSuccessful) {
                val quote = resp.body()?.data?.fetched?.firstOrNull()
                if (quote != null) {
                    // 5. Return Success
                    return ApiResult.Success(quote)
                } else {
                    // 6. Return Error (if fetched list is empty)
                    return ApiResult.Error("API returned success but 'fetched' list was empty.")
                }
            } else {
                // 7. ✅ THIS IS THE KEY: Return the REAL error message from the server
                val errorBodyString = resp.errorBody()?.string() ?: "Unknown HTTP error"
                Log.e("SmartAPI_QUOTE_ERROR", "Error body: $errorBodyString")
                return ApiResult.Error(errorBodyString)
            }

        } catch (e: Exception) {
            Log.e("SmartAPI_QUOTE_ERR", "Exception: ${e.localizedMessage}", e)
            // 8. Return the exception message
            return ApiResult.Error("Exception: ${e.localizedMessage ?: "Unknown Error"}")
        }
    }
}
