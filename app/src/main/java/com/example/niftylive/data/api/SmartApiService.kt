package com.example.niftylive.data.api

import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteRequest // <-- 1. ADD THIS IMPORT
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

    
    // ✅ THIS IS THE UPDATED getQuote FUNCTION
    @Headers(
        "Accept: application/json",
        "X-UserType: USER",
        "X-SourceID: WEB",
        "Content-Type: application/json"
    )
    @POST("/rest/secure/angelbroking/market/v1/quote/")
    suspend fun getQuote(
        @Header("Authorization") auth: String,
        @Header("X-PrivateKey") apiKey: String,
        @Header("X-ClientLocalIP") localIp: String,
        @Header("X-ClientPublicIP") publicIp: String,
        @Header("X-MACAddress") macAddress: String,
        
        @Body body: QuoteRequest // <-- 2. THIS IS THE FIX
    ): Response<QuoteResponse>
}
