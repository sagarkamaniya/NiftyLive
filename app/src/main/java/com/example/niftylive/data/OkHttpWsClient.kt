package com.example.niftylive.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import javax.inject.Inject // <-- ADDED THIS IMPORT

class OkHttpWsClient @Inject constructor(
    private val client: OkHttpClient // <-- CHANGED THIS LINE
) {
    // private val client = OkHttpClient.Builder().build() // <-- REMOVED THIS LINE

    private var ws: WebSocket? = null
    private val msgChannel = Channel<String>(Channel.BUFFERED)
    val messages = msgChannel.receiveAsFlow()

    fun connect(wsUrl: String, onOpen: (() -> Unit)? = null) {
        val request = Request.Builder().url(wsUrl).build()
        ws = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                onOpen?.invoke()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                CoroutineScope(Dispatchers.IO).launch { msgChannel.send(text) }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                CoroutineScope(Dispatchers.IO).launch { msgChannel.send(bytes.utf8()) }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                CoroutineScope(Dispatchers.IO).launch { msgChannel.send("{\"error\":\"${t.localizedMessage ?: "unknown"}\"}") }
            }
        })
    }

    fun send(text: String) { ws?.send(text) }
    fun close() { ws?.close(1000, "closing") }
}
