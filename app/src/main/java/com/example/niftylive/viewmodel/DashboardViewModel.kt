package com.example.niftylive.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.model.Holding
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.model.OrderRequest
import com.example.niftylive.data.repository.ApiResult
import com.example.niftylive.data.repository.NiftyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DashboardState {
    object Idle : DashboardState()
    object Loading : DashboardState()
    data class Success(
        val niftyQuote: InstrumentQuote?,
        val holdings: List<Holding>,
        val funds: String
    ) : DashboardState()

    data class Error(val message: String) : DashboardState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: NiftyRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val state = _state.asStateFlow()

    // Internal cache
    private var myStaticHoldings: List<Holding> = emptyList()
    private var latestLiveHoldings: List<Holding> = emptyList()
    private var latestNifty: InstrumentQuote? = null
    private var latestFunds: String = "Loading..."

    fun startDashboard() {
        _state.value = DashboardState.Loading

        // --- LOOP 1: Live Prices ---
        viewModelScope.launch {
            // 1. Fetch Portfolio ONCE
            when (val result = repository.getHoldings()) {
                is ApiResult.Success -> {
                    myStaticHoldings = result.data
                    latestLiveHoldings = result.data
                }
                is ApiResult.Error -> {
                    myStaticHoldings = emptyList()
                    latestLiveHoldings = emptyList()
                }
            }

            // 2. Start the FAST Loop (1 Second)
            while (true) {
                val niftyToken = "99926000"
                val allTokens = mutableListOf(niftyToken)

                myStaticHoldings.forEach { holding ->
                    holding.symbolToken?.let { allTokens.add(it) }
                }

                when (val result = repository.getQuotesForList(allTokens)) {
                    is ApiResult.Success -> {
                        val liveQuotes = result.data

                        // A. Update Nifty
                        latestNifty = liveQuotes.find { it.symbolToken == niftyToken }

                        // B. Update Portfolio with Live Data
                        latestLiveHoldings = myStaticHoldings.map { staticHolding ->
                            val liveData = liveQuotes.find { it.symbolToken == staticHolding.symbolToken }

                            if (liveData != null) {
                                val currentLtp = liveData.ltp ?: 0.0
                                val avgPrice = staticHolding.averagePrice ?: 0.0
                                val qty = staticHolding.quantity ?: 0

                                val livePnl = (currentLtp - avgPrice) * qty

                                staticHolding.copy(ltp = currentLtp, pnl = livePnl)
                            } else {
                                staticHolding
                            }
                        }
                        emitState()
                    }
                    is ApiResult.Error -> {
                        // Log error
                    }
                }
                delay(1000)
            }
        }

        // --- LOOP 2: Funds (Every 5 seconds) ---
        viewModelScope.launch {
            while (true) {
                when (val result = repository.getFunds()) {
                    is ApiResult.Success -> {
                        latestFunds = result.data
                        emitState()
                    }
                    is ApiResult.Error -> { }
                }
                delay(5000)
            }
        }
    }

    private fun emitState() {
        _state.value = DashboardState.Success(
            niftyQuote = latestNifty,
            holdings = latestLiveHoldings,
            funds = latestFunds
        )
    }

    fun placeTrade(symbol: String, token: String, type: String, qty: String) {
        viewModelScope.launch {
            val request = OrderRequest(
                // ✅ FIXED: Use 'tradingSymbol' (camelCase) not 'tradingsymbol'
                tradingSymbol = symbol,
                // ✅ FIXED: Use 'symbolToken' (camelCase) not 'symboltoken'
                symbolToken = token,
                transactionType = type,
                exchange = "NSE",
                orderType = "MARKET",
                productType = "DELIVERY",
                quantity = qty
            )
            val result = repository.placeOrder(request)

            if (result is ApiResult.Success) {
                Log.d("TRADE", "Order Placed: ${result.data}")
            } else if (result is ApiResult.Error) {
                Log.e("TRADE", "Failed: ${result.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.saveTokens(null)
            _state.value = DashboardState.Idle
        }
    }
}
