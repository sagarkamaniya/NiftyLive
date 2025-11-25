package com.example.niftylive.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.niftylive.data.model.Holding
import com.example.niftylive.viewmodel.DashboardState
import com.example.niftylive.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dialog States
    var showDialog by remember { mutableStateOf(false) }
    var selectedStock by remember { mutableStateOf<Holding?>(null) }
    var transactionType by remember { mutableStateOf("BUY") }

    LaunchedEffect(Unit) {
        focusManager.clearFocus()
        keyboardController?.hide()
        viewModel.startDashboard()
        viewModel.tradeStatus.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val priceStyle = MaterialTheme.typography.displaySmall.copy(
        fontFamily = FontFamily.Monospace,
        fontFeatureSettings = "tnum"
    )
    val bodyStyle = MaterialTheme.typography.bodyLarge.copy(
        fontFamily = FontFamily.Monospace,
        fontFeatureSettings = "tnum"
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            when (val currentState = state) {
                is DashboardState.Idle -> Text("Initializing...", style = MaterialTheme.typography.headlineSmall)
                is DashboardState.Loading -> CircularProgressIndicator()
                is DashboardState.Error -> Text(currentState.message, color = MaterialTheme.colorScheme.error)
                
                is DashboardState.Success -> {
                    val nifty = currentState.niftyQuote
                    val holdings = currentState.holdings
                    val funds = currentState.funds

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // NIFTY CARD
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = nifty?.tradingSymbol ?: "NIFTY 50", style = MaterialTheme.typography.headlineMedium)
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

                        // FUNDS
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Available Funds:", style = MaterialTheme.typography.titleMedium)
                                    Text("₹$funds", style = MaterialTheme.typography.titleMedium, color = Color(0xFF00C853))
                                }
                            }
                        }

                        // PORTFOLIO LIST
                        item { Text("Your Portfolio (${holdings.size})", style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth()) }

                        if (holdings.isEmpty()) {
                            item { Text("No holdings found") }
                        } else {
                            items(holdings) { stock ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(text = stock.tradingSymbol ?: "UNKNOWN", style = MaterialTheme.typography.titleMedium)
                                            TickerText(text = "₹${String.format("%.2f", stock.ltp ?: 0.0)}", style = bodyStyle)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Qty: ${stock.quantity}")
                                            Text(
                                                text = "P&L: ${String.format("%.2f", stock.pnl ?: 0.0)}",
                                                style = bodyStyle,
                                                color = if ((stock.pnl ?: 0.0) >= 0) Color(0xFF00C853) else Color(0xFFD50000)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { selectedStock = stock; transactionType = "BUY"; showDialog = true },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                                                modifier = Modifier.weight(1f)
                                            ) { Text("BUY") }
                                            
                                            Button(
                                                onClick = { selectedStock = stock; transactionType = "SELL"; showDialog = true },
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

        if (showDialog && selectedStock != null) {
            TradeDialog(
                stock = selectedStock!!,
                type = transactionType,
                onDismiss = { showDialog = false },
                onConfirm = { qty, price, isLimit ->
                    viewModel.placeTrade(
                        symbol = selectedStock!!.tradingSymbol ?: "",
                        token = selectedStock!!.symbolToken ?: "",
                        transactionType = transactionType,
                        quantity = qty,
                        price = price,
                        isLimitOrder = isLimit
                    )
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun TradeDialog(
    stock: Holding,
    type: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Boolean) -> Unit
) {
    var quantity by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf(stock.ltp?.toString() ?: "0") }
    var isLimitOrder by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "$type ${stock.tradingSymbol}", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column {
                Text("Current Price: ₹${stock.ltp}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { if(it.all { char -> char.isDigit() }) quantity = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isLimitOrder, onCheckedChange = { isLimitOrder = it })
                    Text("Limit Order (Set Custom Price)")
                }
                if (isLimitOrder) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(quantity, price, isLimitOrder) },
                colors = ButtonDefaults.buttonColors(containerColor = if (type == "BUY") Color(0xFF00C853) else Color(0xFFD50000))
            ) { Text("CONFIRM $type") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
