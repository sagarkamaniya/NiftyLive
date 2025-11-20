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
    // ✅ CHANGED: Success now holds BOTH the Quote AND the Holdings
    data class Success(
        val niftyQuote: InstrumentQuote?, 
        val holdings: List<Holding>
    ) : DashboardState()
    
    data class Error(val message: String) : DashboardState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: NiftyRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val state = _state.asStateFlow()

    // Internal cache to hold the latest data from both loops
    private var cachedQuote: InstrumentQuote? = null
    private var cachedHoldings: List<Holding> = emptyList()

    // ✅ NEW FUNCTION: Starts both polling loops
    fun startDashboard() {
        _state.value = DashboardState.Loading
        
        // Start Loop 1: Nifty 50 (Fast Update)
        viewModelScope.launch {
            while (true) {
                when (val result = repository.getQuoteForToken("99926000")) {
                    is ApiResult.Success -> {
                        cachedQuote = result.data
                        emitSuccessState()
                    }
                    is ApiResult.Error -> {
                        // Optional: Log error but don't crash UI if one fails
                    }
                }
                delay(250) // 1 Second delay for Quote
            }
        }

        // Start Loop 2: Portfolio (Slow Update)
        viewModelScope.launch {
            while (true) {
                when (val result = repository.getHoldings()) {
                    is ApiResult.Success -> {
                        cachedHoldings = result.data
                        emitSuccessState()
                    }
                    is ApiResult.Error -> {
                        // Optional: Log error
                    }
                }
                delay(1000) // 2 Second delay for Portfolio (Rate limit safety)
            }
        }
    }

    private fun emitSuccessState() {
        _state.value = DashboardState.Success(cachedQuote, cachedHoldings)
    }

    fun logout() {
        viewModelScope.launch {
            repository.saveTokens(null)
            _state.value = DashboardState.Idle
        }
    }
}
