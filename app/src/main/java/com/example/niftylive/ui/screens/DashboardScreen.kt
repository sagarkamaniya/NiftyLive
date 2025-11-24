package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
        // Call the combined start function
        viewModel.startDashboard()
    }

    // Styles for Monospace numbers
    val priceStyle = MaterialTheme.typography.displaySmall.copy(
        fontFamily = FontFamily.Monospace,
        fontFeatureSettings = "tnum"
    )
    val bodyStyle = MaterialTheme.typography.bodyLarge.copy(
        fontFamily = FontFamily.Monospace,
        fontFeatureSettings = "tnum"
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is DashboardState.Idle -> Text("Initializing...", style = MaterialTheme.typography.headlineSmall)
                is DashboardState.Loading -> CircularProgressIndicator()
                is DashboardState.Error -> Text(currentState.message, color = MaterialTheme.colorScheme.error)
                
                is DashboardState.Success -> {
                    val nifty = currentState.niftyQuote
                    val holdings = currentState.holdings
                    val funds = currentState.funds // ✅ Get Funds from state

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // --- 1. NIFTY 50 CARD ---
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = nifty?.tradingSymbol ?: "NIFTY 50",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    
                                    TickerText(
                                        text = "₹${String.format("%.2f", nifty?.ltp ?: 0.0)}",
                                        style = priceStyle,
                                        color = if ((nifty?.netChange ?: 0.0) >= 0) Color(0xFF00C853) else Color(0xFFD50000)
                                    )
                                    
                                    Spacer(Modifier.height(8.dp))
                                    
                                    Text(
                                        text = "Change: ${String.format("%.2f", nifty?.netChange ?: 0.0)} (${String.format("%.2f", nifty?.percentChange ?: 0.0)}%)",
                                        style = bodyStyle
                                    )
                                }
                            }
                        }

                        // --- 2. FUNDS DISPLAY (NEW) ---
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                // Light blue background for funds
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Available Funds:", style = MaterialTheme.typography.titleMedium)
                                    // Show funds in Green
                                    Text(
                                        text = "₹$funds", 
                                        style = MaterialTheme.typography.titleMedium, 
                                        color = Color(0xFF00C853)
                                    )
                                }
                            }
                        }

                        // --- 3. SECTION TITLE ---
                        item {
                            Text(
                                text = "Your Portfolio (${holdings.size})",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // --- 4. PORTFOLIO ITEMS ---
                        if (holdings.isEmpty()) {
                            item { Text("No holdings found") }
                        } else {
                            items(holdings) { stock ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        // Top Row: Name & Price
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = stock.tradingSymbol ?: "UNKNOWN",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            TickerText(
                                                text = "₹${String.format("%.2f", stock.ltp ?: 0.0)}",
                                                style = bodyStyle
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        // Middle Row: Qty & P&L
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Qty: ${stock.quantity}")
                                            Text(
                                                text = "P&L: ${String.format("%.2f", stock.pnl ?: 0.0)}",
                                                style = bodyStyle,
                                                color = if ((stock.pnl ?: 0.0) >= 0) Color(0xFF00C853) else Color(0xFFD50000)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // ✅ NEW: BUY / SELL BUTTONS
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            // BUY BUTTON
                                            Button(
                                                onClick = { 
                                                    viewModel.placeTrade(
                                                        symbol = stock.tradingSymbol ?: "", 
                                                        token = stock.symbolToken ?: "", 
                                                        type = "BUY", 
                                                        qty = "1" // Default qty 1 for testing
                                                    ) 
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                                                modifier = Modifier.weight(1f)
                                            ) { Text("BUY") }
                                            
                                            // SELL BUTTON
                                            Button(
                                                onClick = { 
                                                    viewModel.placeTrade(
                                                        symbol = stock.tradingSymbol ?: "", 
                                                        token = stock.symbolToken ?: "", 
                                                        type = "SELL", 
                                                        qty = "1"
                                                    ) 
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD50000)),
                                                modifier = Modifier.weight(1f)
                                            ) { Text("SELL") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
