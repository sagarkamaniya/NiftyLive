package com.example.niftylive.viewmodel

import android.util.Log 
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.niftylive.data.model.InstrumentQuote
import com.example.niftylive.data.repository.NiftyRepository
import kotlinx.coroutines.delay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.niftylive.data.repository.ApiResult // <-- 1. IMPORT THE NEW CLASS

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

    // ✅ THIS IS THE UPDATED FUNCTION
    fun startDataPolling(token: String = "99926000") { // Correct NIFTY 50 index token
        viewModelScope.launch {
        	while(true) {
            _state.value = DashboardState.Loading
            
            // 2. Check the result from the repository
            when (val result = repository.getQuoteForToken(token)) {
                is ApiResult.Success -> {
                    // We got the data
                    _state.value = DashboardState.Success(result.data)
                }
                is ApiResult.Error -> {
                    // 3. We got an error, pass the REAL message to the UI
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
