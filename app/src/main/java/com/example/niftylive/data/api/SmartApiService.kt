package com.example.niftylive.data.api

import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SmartApiService {

    // Your login function (this one is correct)
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "X-UserType: USER",
        "X-SourceID: WEB"
    )
    @POST("/rest/auth/angelbroking/user/v1/loginByPassword")
    suspend fun login(
        @Header("X-PrivateKey") apiKey: String,
        @Header("X-ClientLocalIP") localIp: String,
        @Header("X-ClientPublicIP") publicIp: String,
        @Header("X-MACAddress") macAddress: String,
        @Body body: Map<String, String>
    ): Response<LoginResponse>

    
    // ✅ REPLACED THE OLD getQuote FUNCTION WITH THIS
    @Headers(
        "Accept: application/json",
        "X-UserType: USER",
        "X-SourceID: WEB",
        "Content-Type: application/json"
    )
    @POST("/rest/secure/angelbroking/market/v1/quote/") // New Endpoint
    suspend fun getQuote(
        @Header("Authorization") auth: String, // Bearer token
        @Header("X-PrivateKey") apiKey: String,
        @Header("X-ClientLocalIP") localIp: String,
        @Header("X-ClientPublicIP") publicIp: String,
        @Header("X-MACAddress") macAddress: String,
        
        @Body body: Map<String, Any> // The new request body
    ): Response<QuoteResponse>
}
