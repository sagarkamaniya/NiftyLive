package com.example.niftylive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun startDataPolling(token: String = "99926000") {
        viewModelScope.launch {
            
            // ✅ FIX: Loading state is set HERE, before the loop starts.
            // It will only show the spinner the very first time.
            _state.value = DashboardState.Loading

            while(true) {
                // Inside the loop, we ONLY update the data.
                // We NEVER set 'Loading' again, so the screen never flashes.
                
                when (val result = repository.getQuoteForToken(token)) {
                    is ApiResult.Success -> {
                        _state.value = DashboardState.Success(result.data)
                    }
                    is ApiResult.Error -> {
                        _state.value = DashboardState.Error(result.message)
                    }
                }
                
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
