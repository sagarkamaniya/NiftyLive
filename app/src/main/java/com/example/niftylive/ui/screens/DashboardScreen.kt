package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.niftylive.viewmodel.DashboardViewModel
import com.example.niftylive.viewmodel.DashboardState

@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val clientCode by viewModel.clientCode.collectAsState()
    val accessToken by viewModel.accessToken.collectAsState()
    val state by viewModel.dashboardState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchQuote()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📊 Welcome to NiftyLive Dashboard", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Client Code: $clientCode")
            Text("Access Token: $accessToken")

            Spacer(modifier = Modifier.height(16.dp))

            when (state) {
                is DashboardState.Loading -> Text("Fetching latest quote…")
                is DashboardState.Success -> {
                    val quote = (state as DashboardState.Success).quote
                    Text("Token: ${quote.token}")
                    Text("Last Traded Price: ₹${quote.last_traded_price}")
                }
                is DashboardState.Error -> Text((state as DashboardState.Error).message)
                else -> Text("Idle")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                viewModel.logout()
                onLogout()
            }) {
                Text("Logout")
            }
        }
    }
}