package com.example.niftylive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.niftylive.di.ServiceLocator
import com.example.niftylive.ui.screens.LoginScreen
import com.example.niftylive.ui.theme.NiftyLiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Try initializing ServiceLocator safely
        var initError: String? = null
        try {
            ServiceLocator.initialize(applicationContext)
        } catch (t: Throwable) {
            initError = "Failed to initialize app services: ${t.localizedMessage ?: t::class.java.simpleName}"
        }

        setContent {
            NiftyLiveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (initError != null) {
                        InitializationErrorScreen(initError)
                    } else {
                        LoginScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun InitializationErrorScreen(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "⚠️ App Initialization Failed",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { /* optional: could trigger restart or retry */ }) {
            Text("Retry")
        }
    }
}