package com.example.niftylive.data.api

import com.example.niftylive.data.model.HoldingResponse
import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.ProfileResponse
import com.example.niftylive.data.model.QuoteRequest
import com.example.niftylive.data.model.QuoteResponse
import com.example.niftylive.data.model.RMSResponse // ✅ FIXED: Added 'import'
import com.example.niftylive.data.model.OrderRequest
import com.example.niftylive.data.model.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SmartApiService {

    // --- Login ---
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

    
    // --- Get Quote ---
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
        @Body body: QuoteRequest
    ): Response<QuoteResponse>


    // --- Get Profile (Auto-Login) ---
    @Headers(
        "Accept: application/json",
        "X-UserType: USER",
        "X-SourceID: WEB"
    )
    @GET("/rest/secure/angelbroking/user/v1/getProfile")
    suspend fun getProfile(
        @Header("Authorization") auth: String,
        @Header("X-PrivateKey") apiKey: String,
        @Header("X-ClientLocalIP") localIp: String,
        @Header("X-ClientPublicIP") publicIp: String,
        @Header("X-MACAddress") macAddress: String
    ): Response<ProfileResponse>


    // --- Get Portfolio Holdings ---
    @Headers(
        "Accept: application/json",
        "X-UserType: USER",
        "X-SourceID: WEB"
    )
    @GET("/rest/secure/angelbroking/portfolio/v1/getAllHolding")
    suspend fun getHoldings(
        @Header("Authorization") auth: String,
        @Header("X-PrivateKey") apiKey: String,
        @Header("X-ClientLocalIP") localIp: String,
        @Header("X-ClientPublicIP") publicIp: String,
        @Header("X-MACAddress") macAddress: String
    ): Response<HoldingResponse>

    // ✅ NEW: Get Funds (RMS)
    @Headers(
        "Accept: application/json",
        "X-UserType: USER",
        "X-SourceID: WEB"
    )
    @GET("/rest/secure/angelbroking/user/v1/getRMS")
    suspend fun getRMS(
        @Header("Authorization") auth: String,
        @Header("X-PrivateKey") apiKey: String,
        @Header("X-ClientLocalIP") localIp: String,
        @Header("X-ClientPublicIP") publicIp: String,
        @Header("X-MACAddress") macAddress: String
    ): Response<RMSResponse>

    // ✅ NEW: Place Order
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "X-UserType: USER",
        "X-SourceID: WEB"
    )
    @POST("/rest/secure/angelbroking/order/v1/placeOrder")
    suspend fun placeOrder(
        @Header("Authorization") auth: String,
        @Header("X-PrivateKey") apiKey: String,
        @Header("X-ClientLocalIP") localIp: String,
        @Header("X-ClientPublicIP") publicIp: String,
        @Header("X-MACAddress") macAddress: String,
        @Body body: OrderRequest
    ): Response<OrderResponse>
}
