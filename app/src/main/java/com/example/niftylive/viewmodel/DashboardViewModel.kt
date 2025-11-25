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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _tradeStatus = Channel<String>()
    val tradeStatus = _tradeStatus.receiveAsFlow()

    private var myStaticHoldings: List<Holding> = emptyList()
    private var latestLiveHoldings: List<Holding> = emptyList()
    private var latestNifty: InstrumentQuote? = null
    private var latestFunds: String = "Loading..."

    fun startDashboard() {
        _state.value = DashboardState.Loading

        // --- LOOP 1: Live Prices ---
        viewModelScope.launch {
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

            while (true) {
                val niftyToken = "99926000"
                val allTokens = mutableListOf(niftyToken)
                myStaticHoldings.forEach { holding -> holding.symbolToken?.let { allTokens.add(it) } }

                when (val result = repository.getQuotesForList(allTokens)) {
                    is ApiResult.Success -> {
                        val liveQuotes = result.data
                        latestNifty = liveQuotes.find { it.symbolToken == niftyToken }

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
                    is ApiResult.Error -> {}
                }
                delay(1000)
            }
        }

        // --- LOOP 2: Funds ---
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

    fun placeTrade(
        symbol: String,
        token: String,
        transactionType: String,
        quantity: String,
        price: String,
        isLimitOrder: Boolean
    ) {
        viewModelScope.launch {
            val orderType = if (isLimitOrder) "LIMIT" else "MARKET"
            val finalPrice = if (isLimitOrder) price else "0"

            val request = OrderRequest(
                tradingSymbol = symbol,
                symbolToken = token,
                transactionType = transactionType,
                exchange = "NSE",
                orderType = orderType,
                productType = "DELIVERY",
                quantity = quantity,
                price = finalPrice
            )

            val result = repository.placeOrder(request)

            if (result is ApiResult.Success) {
                _tradeStatus.send("✅ Order Placed! ID: ${result.data}")
                repository.getFunds() 
            } else if (result is ApiResult.Error) {
                _tradeStatus.send("❌ Failed: ${result.message}")
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
