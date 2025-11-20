package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.niftylive.viewmodel.DashboardState
import com.example.niftylive.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusManager.clearFocus()
        keyboardController?.hide()
        viewModel.startDataPolling()
    }

    // ✅ DEFINE STYLES WITH TABULAR NUMERALS (tnum)
    // This ensures '1' takes the same space as '9', preventing the jumping effect.
    val priceStyle = MaterialTheme.typography.displaySmall.copy(fontFeatureSettings = "tnum")
    val changeStyle = MaterialTheme.typography.bodyLarge.copy(fontFeatureSettings = "tnum")

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is DashboardState.Idle -> {
                    Text(
                        text = "Welcome to NiftyLive 📈",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                is DashboardState.Loading -> {
                    CircularProgressIndicator()
                }
                is DashboardState.Error -> {
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is DashboardState.Success -> {
                    val quote = currentState.quote
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = quote.tradingSymbol ?: "NIFTY 50",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // ✅ USE THE FIXED WIDTH STYLE HERE
                        TickerText(
                            text = "₹${String.format("%.2f", quote.ltp ?: 0.0)}", 
                            style = priceStyle, // Using 'tnum' style
                            color = if ((quote.netChange ?: 0.0) >= 0) Color(0xFF00C853) else Color(0xFFD50000)
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // ✅ USE THE FIXED WIDTH STYLE HERE TOO
                        Text(
                            text = "Change: ${quote.netChange} (${quote.percentChange}%)",
                            style = changeStyle // Using 'tnum' style
                        )
                    }
                }
            }
        }
    }
}
