package com.example.niftylive.viewmodel

import android.util.Log // <-- ADD THIS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.repository.NiftyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DashboardState {
    object Idle : DashboardState()
    object Loading : DashboardState()
    data class Success(val quote: InstrumentQuote) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: NiftyRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val state = _state.asStateFlow()

    val clientCode = MutableStateFlow(repository.getClientCode() ?: "")
    val accessToken = MutableStateFlow(repository.getAccessToken() ?: "")

    fun fetchQuote(token: String = "26000") { // NIFTY 50 index token
        viewModelScope.launch {
            _state.value = DashboardState.Loading
            try {
                val quote = repository.getQuoteForToken(token)
                Log.d("DashboardViewModel", "Quote response for token $token: $quote")
                if (quote != null) {
                    _state.value = DashboardState.Success(quote)
                } else {
                    Log.w("DashboardViewModel", "No quote found for token $token!")
                    _state.value = DashboardState.Error("⚠️ No quote found")
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Exception during API request: ${e.localizedMessage}", e)
                _state.value = DashboardState.Error("Exception: ${e.localizedMessage}")
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