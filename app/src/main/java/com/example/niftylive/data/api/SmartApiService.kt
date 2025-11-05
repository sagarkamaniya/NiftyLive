package com.example.niftylive.data.api

import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.QuoteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SmartApiService {
    // NOTE: these endpoint paths are placeholders. Replace with exact SmartAPI endpoints if different.

    // Exchange auth_code for access_token
    @POST("rest/secure/angelbroking/user/v1/session")
    suspend fun login(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<LoginResponse>

    // Quote call to fetch instrument / last close
    @POST("rest/secure/angelbroking/market/v1/quote/")
    suspend fun getQuote(
        @Header("Authorization") bearer: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<QuoteResponse>
}
