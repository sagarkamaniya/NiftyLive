package com.example.niftylive.data.api

import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SmartApiService {

    /** SmartAPI Login Endpoint **/
    @POST("smartapi/v1.0/login")
    suspend fun login(
        @Header("X-PrivateKey") apiKey: String,
        @Body body: Map<String, String>
    ): Response<LoginResponse>

    /** Get live market quote **/
    @POST("smartapi/v1.0/quote")
    suspend fun getQuote(
        @Header("Authorization") bearer: String,
        @Header("X-PrivateKey") apiKey: String,
        @Body body: Map<String, Any>
    ): Response<QuoteResponse>
}