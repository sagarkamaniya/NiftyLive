package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
// ... (other imports) ...
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.niftylive.viewmodel.DashboardState
import com.example.niftylive.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchQuote()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
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
                    //  UPDATED THIS BLOCK
                    val quote = currentState.quote
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = quote.tradingSymbol ?: "NIFTY 50",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "${quote.ltp}", // Use new field 'ltp'
                            style = MaterialTheme.typography.displaySmall,
                            color = if ((quote.netChange ?: 0.0) >= 0) Color(0xFF00C853) else Color(0xFFD50000)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            // Use new fields 'netChange' and 'percentChange'
                            text = "Change: ${quote.netChange} (${quote.percentChange}%)",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
