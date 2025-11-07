package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.repository.NiftyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DashboardState {
    object Idle : DashboardState()
    object Loading : DashboardState()
    data class Success(val quote: InstrumentQuote) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class DashboardViewModel(
    private val repo: NiftyRepository
) : ViewModel() {

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val dashboardState = _dashboardState.asStateFlow()

    private val _clientCode = MutableStateFlow(repo.getClientCode() ?: "")
    val clientCode = _clientCode.asStateFlow()

    private val _accessToken = MutableStateFlow(repo.getAccessToken() ?: "No Token Found...")
    val accessToken = _accessToken.asStateFlow()

    /**
     * Fetch live quote for the provided token (e.g. NIFTY index)
     */
    fun fetchQuote(token: String = "26000") {
        viewModelScope.launch {
            _dashboardState.value = DashboardState.Loading
            try {
                val quote = repo.getQuoteForToken(token)
                if (quote != null) {
                    _dashboardState.value = DashboardState.Success(quote)
                } else {
                    _dashboardState.value = DashboardState.Error("⚠️ Failed to fetch quote")
                }
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error("Exception: ${e.localizedMessage}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.saveTokens(null)
            _accessToken.value = ""
            _clientCode.value = ""
            _dashboardState.value = DashboardState.Idle
        }
    }
}