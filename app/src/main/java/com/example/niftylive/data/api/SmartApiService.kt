package com.example.niftylive.data.api

import com.example.niftylive.data.model.LoginRequest
import com.example.niftylive.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SmartApiService {
    @POST("QuickAuth")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
