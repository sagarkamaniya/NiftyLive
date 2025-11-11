package com.example.niftylive.ui.screens

// ✅ ALL THE MISSING IMPORTS ARE HERE
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.niftylive.viewmodel.DashboardState
import com.example.niftylive.viewmodel.DashboardViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    
    // 1. Get the UI state from the ViewModel
    val state by viewModel.state.collectAsState()

    // 2. This block runs once when the screen first appears
    LaunchedEffect(Unit) {
        // 3. Tell the ViewModel to fetch the quote
        viewModel.fetchQuote()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // 4. Show UI based on the current state
            when (val currentState = state) {
                is DashboardState.Idle -> {
                    // The initial state
                    Text(
                        text = "Welcome to NiftyLive 📈",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                is DashboardState.Loading -> {
                    // Show a loading spinner
                    CircularProgressIndicator()
                }
                is DashboardState.Error -> {
                    // Show the error message
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is DashboardState.Success -> {
                    // ✅ SUCCESS! Show the quote data
                    val quote = currentState.quote
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = quote.tradingSymbol ?: "NIFTY 50",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "₹${quote.ltp}", // Use new field 'ltp'
                            style = MaterialTheme.typography.displaySmall,
                            // Show green for positive, red for negative
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
