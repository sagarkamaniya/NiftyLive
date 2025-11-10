package com.example.niftylive.data.api

import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SmartApiService {

    // ✅ UPDATED LOGIN FUNCTION
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "X-UserType: USER",
        "X-SourceID: WEB" // Note: You may need to change WEB to APP
    )
    @POST("/rest/auth/angelbroking/user/v1/loginByPassword") // The new endpoint
    suspend fun login(
        @Header("X-PrivateKey") apiKey: String, // This is your API Key
        @Header("X-ClientLocalIP") localIp: String,
        @Header("X-ClientPublicIP") publicIp: String,
        @Header("X-MACAddress") macAddress: String,
        @Body body: Map<String, String>
    ): Response<LoginResponse>

    // ✅ TODO: You will need to update this quote endpoint as well.
    // It is probably wrong.
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("smartapi/v1/quote")
    suspend fun getQuote(
        @Header("Authorization") auth: String,
        @Header("X-Api-Key") apiKey: String,
        @Body body: Map<String, Any>
    ): Response<QuoteResponse>
}
