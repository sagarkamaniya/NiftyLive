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
import androidx.compose.ui.text.font.FontFamily
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

    // Use Monospace font to ensure every digit has the exact same width
    val priceStyle = MaterialTheme.typography.displaySmall.copy(
        fontFamily = FontFamily.Monospace,
        fontFeatureSettings = "tnum"
    )
    val changeStyle = MaterialTheme.typography.bodyLarge.copy(
        fontFamily = FontFamily.Monospace,
        fontFeatureSettings = "tnum"
    )

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
                        text = "Welcome to NiftyLive ",
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
                        
                        // Price: Formatted to 2 decimals (e.g., "25432.10")
                        TickerText(
                            text = "${String.format("%.2f", quote.ltp ?: 0.0)}", 
                            style = priceStyle, 
                            color = if ((quote.netChange ?: 0.0) >= 0) Color(0xFF00C853) else Color(0xFFD50000)
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Change & Percent:  NOW FORMATTED TO 2 DECIMALS
                        // Example: "Change: 120.50 (0.45%)"
                        // This prevents the text from jumping when ".5" becomes ".50"
                        Text(
                            text = "Change: ${String.format("%.2f", quote.netChange ?: 0.0)} (${String.format("%.2f", quote.percentChange ?: 0.0)}%)",
                            style = changeStyle
                        )
                    }
                }
            }
        }
    }
}
