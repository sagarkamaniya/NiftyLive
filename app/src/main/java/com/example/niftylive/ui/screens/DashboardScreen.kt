package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.niftylive.viewmodel.DashboardViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DashboardScreen() {
    val vm: DashboardViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.start() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("NIFTY 50", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Price: ₹${state.price ?: state.lastClose ?: "-"}")
                Text("Change %: ${state.changePercent ?: "-"}")
                Text(if (state.isMarketOpen) "LIVE" else "Market Closed - Showing Last Close")
            }
        }

        state.error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }
    }
}