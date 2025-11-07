package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.niftylive.di.ServiceLocator
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(navController: NavHostController) {
    val repo = ServiceLocator.niftyRepository
    val scope = rememberCoroutineScope()

    var accessToken by remember { mutableStateOf("") }
    var clientCode by remember { mutableStateOf("") }
    var quoteText by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        accessToken = repo.getAccessToken() ?: "No Token Found"
        clientCode = repo.getClientCode() ?: "Unknown"
        quoteText = "Fetching NIFTY quote..."
        isLoading = true

        scope.launch {
            try {
                val nifty = repo.getQuoteForToken("26000") // NIFTY 50
                quoteText = if (nifty != null) {
                    "📊 NIFTY 50: ₹${nifty.lastPrice ?: 0.0}  (${nifty.percentChange ?: 0.0}%)"
                } else {
                    "⚠️ Failed to fetch quote"
                }
            } catch (e: Exception) {
                quoteText = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
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
            Text("Welcome to NiftyLive Dashboard", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Client Code: $clientCode")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Access Token: ${accessToken.take(20)}...")

            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading)
                CircularProgressIndicator()
            else
                Text(text = quoteText)

            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = {
                navController.navigate("login") {
                    popUpTo("dashboard") { inclusive = true }
                }
            }) {
                Text("Logout")
            }
        }
    }
}