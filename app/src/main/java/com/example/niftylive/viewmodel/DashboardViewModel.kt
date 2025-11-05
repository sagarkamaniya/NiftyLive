package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.OkHttpWsClient
import com.example.niftylive.di.ServiceLocator
import com.example.niftylive.utils.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

data class NiftyUiState(
    val price: Double? = null,
    val changePercent: Double? = null,
    val lastClose: Double? = null,
    val isMarketOpen: Boolean = false,
    val error: String? = null
)

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NiftyUiState(isMarketOpen = TimeUtils.isMarketOpen()))
    val uiState: StateFlow<NiftyUiState> = _uiState

    private val ws = OkHttpWsClient()
    private val repo = ServiceLocator.niftyRepository

    private val WS_URL = "wss://smartapisocket.angelbroking.com/smart-stream" // usually correct
    private val NIFTY_TOKEN = "26000" // confirm token with SmartAPI docs / support

    fun start() {
        _uiState.value = _uiState.value.copy(isMarketOpen = TimeUtils.isMarketOpen())
        if (TimeUtils.isMarketOpen()) startWebSocket() else fetchLastClose()
    }

    private fun startWebSocket() {
        ws.connect(WS_URL) {
            // onOpen: construct subscribe message. Format may vary; adjust if SmartAPI requires auth in subscription:
            val sub = JSONObject()
            sub.put("correlationID", "nifty_1")
            sub.put("action", 1) // subscribe
            val params = JSONObject()
            params.put("mode", 1)
            // tokenlist: SmartAPI expects list-of-objects; adjust if different
            val tokenList = JSONObject()
            tokenList.put("exchangeType", 1)
            tokenList.put("tokens", listOf(NIFTY_TOKEN))
            params.put("tokenList", listOf(tokenList))
            sub.put("params", params)
            ws.send(sub.toString())
        }

        viewModelScope.launch {
            ws.messages.collect { raw ->
                try {
                    val j = JSONObject(raw)
                    val ltp = j.optDouble("ltp", Double.NaN)
                    val pct = j.optDouble("percentChange", Double.NaN)
                    if (!ltp.isNaN()) {
                        _uiState.value = _uiState.value.copy(price = ltp, changePercent = if (!pct.isNaN()) pct else _uiState.value.changePercent)
                    } else {
                        // handle feed messages that wrap data differently
                        val payload = j.optJSONArray("data")
                        if (payload != null && payload.length() > 0) {
                            val first = payload.getJSONObject(0)
                            val price = first.optDouble("ltp", Double.NaN)
                            val perc = first.optDouble("pctChg", Double.NaN)
                            if (!price.isNaN()) _uiState.value = _uiState.value.copy(price = price, changePercent = if (!perc.isNaN()) perc else _uiState.value.changePercent)
                        }
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun fetchLastClose() {
        viewModelScope.launch {
            try {
                val q = repo.getQuoteForToken(NIFTY_TOKEN)
                _uiState.value = _uiState.value.copy(
                    lastClose = q?.closePrice ?: q?.lastPrice,
                    price = q?.lastPrice,
                    changePercent = q?.percentChange
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun stop() {
        ws.close()
    }
}
