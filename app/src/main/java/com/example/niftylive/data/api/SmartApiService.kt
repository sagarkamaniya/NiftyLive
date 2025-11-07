package com.example.niftylive.data.api

import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SmartApiService {

    // ✅ SmartAPI login endpoint
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("smartapi/v1/login")
    suspend fun login(
        @Header("X-Api-Key") apiKey: String,
        @Body body: Map<String, String>
    ): Response<LoginResponse>

    // ✅ Quote endpoint
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("smartapi/v1/quote")
    suspend fun getQuote(
        @Header("Authorization") auth: String,
        @Header("X-Api-Key") apiKey: String,
        @Body body: Map<String, Any>
    ): Response<QuoteResponse>
}