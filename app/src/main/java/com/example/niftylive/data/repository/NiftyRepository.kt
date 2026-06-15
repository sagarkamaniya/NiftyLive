package com.example.niftylive.data.repository

import com.example.niftylive.data.OkHttpWsClient
import com.example.niftylive.data.api.SmartApiService
import com.example.niftylive.data.model.LoginRequest
import com.example.niftylive.data.model.LoginResponse
import com.example.niftylive.data.model.MarketTick
import kotlinx.coroutines.flow.SharedFlow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NiftyRepository @Inject constructor(
    private val apiService: SmartApiService,
    private val wsClient: OkHttpWsClient
) {
    val liveTicks: SharedFlow<MarketTick> = wsClient.ticks

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }

    fun startLiveFeed(userId: String, sessionToken: String, scrips: List<String>) {
        wsClient.connect(userId, sessionToken)
        wsClient.subscribe(scrips)
    }

    fun stopFeed() {
        wsClient.disconnect()
    }
}
