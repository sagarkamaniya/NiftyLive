package com.example.niftylive.data.api

import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SmartApiService {

    // ✅ Correct SmartAPI endpoint for login
    @POST("rest/auth/angelone/v1/loginByPassword")
    suspend fun login(
        @Header("X-UserType") userType: String = "USER",
        @Header("X-SourceID") source: String = "WEB",
        @Header("X-ClientLocalIP") localIP: String = "127.0.0.1",
        @Header("X-ClientPublicIP") publicIP: String = "127.0.0.1",
        @Header("X-MACAddress") mac: String = "00:00:00:00:00:00",
        @Header("X-PrivateKey") apiKey: String,
        @Header("Accept") accept: String = "application/json",
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<LoginResponse>

    // ✅ Quote API endpoint (after login)
    @POST("rest/secure/angelbroking/market/v1/quote/")
    suspend fun getQuote(
        @Header("Authorization") bearer: String,
        @Header("X-PrivateKey") apiKey: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<QuoteResponse>
}