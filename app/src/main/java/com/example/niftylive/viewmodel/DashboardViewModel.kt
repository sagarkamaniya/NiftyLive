package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.repository.NiftyRepository
import dagger.hilt.android.lifecycle.HiltViewModel // <-- 1. IMPORT THIS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject // <-- 2. IMPORT THIS

sealed class DashboardState {
    object Idle : DashboardState()
    object Loading : DashboardState()
    data class Success(val quote: InstrumentQuote) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

@HiltViewModel // <-- 3. ADD THIS ANNOTATION
class DashboardViewModel @Inject constructor( // <-- 4. ADD THIS ANNOTATION
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
                if (quote != null) _state.value = DashboardState.Success(quote)
                else _state.value = DashboardState.Error("⚠️ No quote found")
            } catch (e: Exception) {
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
