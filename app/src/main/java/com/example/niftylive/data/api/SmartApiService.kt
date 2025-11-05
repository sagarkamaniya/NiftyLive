package com.example.niftylive.data.api

import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SmartApiService {

    // ✅ LOGIN: Exchange clientcode/password/totp for access token
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "X-UserType: USER",
        "X-SourceID: WEB",
        "X-ClientLocalIP: 127.0.0.1",
        "X-ClientPublicIP: 127.0.0.1",
        "X-MACAddress: 00:00:00:00:00:00"
    )
    @POST("rest/secure/angelbroking/user/v1/session")
    suspend fun login(
        @Header("X-PrivateKey") apiKey: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<LoginResponse>


    // ✅ QUOTE: Fetch market quote data
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "X-UserType: USER",
        "X-SourceID: WEB",
        "X-ClientLocalIP: 127.0.0.1",
        "X-ClientPublicIP: 127.0.0.1",
        "X-MACAddress: 00:00:00:00:00:00"
    )
    @POST("rest/secure/angelbroking/market/v1/quote/")
    suspend fun getQuote(
        @Header("Authorization") bearer: String,
        @Header("X-PrivateKey") apiKey: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<QuoteResponse>
}