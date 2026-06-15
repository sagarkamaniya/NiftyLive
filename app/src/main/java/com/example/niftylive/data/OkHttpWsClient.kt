package com.example.niftylive.data

import com.example.niftylive.data.model.MarketTick
import com.example.niftylive.data.model.WsInitMessage
import com.example.niftylive.data.model.WsSubMessage
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OkHttpWsClient @Inject constructor(
    private val client: OkHttpClient
) : WebSocketListener() {

    private var webSocket: WebSocket? = null
    private val gson = Gson()
    
    private val _ticks = MutableSharedFlow<MarketTick>(extraBufferCapacity = 100)
    val ticks: SharedFlow<MarketTick> = _ticks

    fun connect(userId: String, token: String) {
        val request = Request.Builder()
            .url("wss://api.shoonya.com/NorenWSTP/")
            .build()
            
        webSocket = client.newWebSocket(request, this)

        // Shoonya requires handshake validation immediately upon opening the socket
        val authMsg = WsInitMessage(userId = userId, accountId = userId, token = token)
        webSocket?.send(gson.toJson(authMsg))
    }

    fun subscribe(tokens: List<String>) {
        // Combines tokens using a hash separator (e.g., "NSE|26000#BSE|12345")
        val scripKeys = tokens.joinToString("#")
        val subMsg = WsSubMessage(keys = scripKeys)
        webSocket?.send(gson.toJson(subMsg))
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            val tick = gson.fromJson(text, MarketTick::class.java)
            // 'tk' stands for standard tick, 'tf' stands for full touchline data
            if (tick.type == "tk" || tick.type == "tf") {
                _ticks.tryEmit(tick)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnected by user")
        webSocket = null
    }
}
