package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.model.Holding
import com.example.niftylive.data.model.InstrumentQuote
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
        val holdings: List<Holding> // These holdings will have LIVE prices
    ) : DashboardState()
    
    data class Error(val message: String) : DashboardState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: NiftyRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val state = _state.asStateFlow()

    // Store your holdings (Quantity, Buy Price) here
    private var myHoldings: List<Holding> = emptyList()

    fun startDashboard() {
        viewModelScope.launch {
            _state.value = DashboardState.Loading

            // 1. Fetch Portfolio ONCE to get the list of stocks you own
            when (val result = repository.getHoldings()) {
                is ApiResult.Success -> {
                    myHoldings = result.data
                }
                is ApiResult.Error -> {
                    // If portfolio fails, we continue with empty list (just to show Nifty)
                    myHoldings = emptyList()
                }
            }

            // 2. Start the FAST Loop (1 Second)
            while (true) {
                // Create a list of ALL tokens we need (Nifty + Your Stocks)
                val niftyToken = "99926000"
                val allTokens = mutableListOf(niftyToken)
                
                // Add all portfolio tokens to the list
                myHoldings.forEach { holding ->
                    holding.symbolToken?.let { allTokens.add(it) }
                }

                // 3. Fetch LIVE prices for EVERYTHING in one request
                when (val result = repository.getQuotesForList(allTokens)) {
                    is ApiResult.Success -> {
                        val liveQuotes = result.data
                        
                        // A. Extract Nifty Quote
                        val niftyQuote = liveQuotes.find { it.symbolToken == niftyToken }

                        // B. Update Holdings with Live Data (Calculate P&L locally)
                        val liveHoldings = myHoldings.map { staticHolding ->
                            // Find the live price for this stock
                            val liveData = liveQuotes.find { it.symbolToken == staticHolding.symbolToken }
                            
                            if (liveData != null) {
                                val currentLtp = liveData.ltp ?: 0.0
                                val avgPrice = staticHolding.averagePrice ?: 0.0
                                val qty = staticHolding.quantity ?: 0
                                
                                // Calculate Live P&L: (LTP - Avg) * Qty
                                val livePnl = (currentLtp - avgPrice) * qty
                                
                                // Return updated holding with new LTP and P&L
                                staticHolding.copy(
                                    ltp = currentLtp,
                                    pnl = livePnl
                                )
                            } else {
                                staticHolding // No live data found, keep old data
                            }
                        }

                        _state.value = DashboardState.Success(niftyQuote, liveHoldings)
                    }
                    is ApiResult.Error -> {
                        // If API fails, keep showing old data or show error
                        // We choose to show error here
                        // _state.value = DashboardState.Error(result.message)
                    }
                }
                
                // 4. Wait 1 second
                delay(1000)
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
