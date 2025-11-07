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

class DashboardViewModel(private val repo: NiftyRepository) : ViewModel() {

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val state = _state.asStateFlow()

    val clientCode = MutableStateFlow(repo.getClientCode() ?: "")
    val accessToken = MutableStateFlow(repo.getAccessToken() ?: "")

    fun fetchQuote(token: String = "26000") {
        viewModelScope.launch {
            _state.value = DashboardState.Loading
            try {
                val quote = repo.getQuoteForToken(token)
                if (quote != null) _state.value = DashboardState.Success(quote)
                else _state.value = DashboardState.Error("⚠️ No quote found")
            } catch (e: Exception) {
                _state.value = DashboardState.Error("Exception: ${e.localizedMessage}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.saveTokens(null)
            _state.value = DashboardState.Idle
        }
    }
}