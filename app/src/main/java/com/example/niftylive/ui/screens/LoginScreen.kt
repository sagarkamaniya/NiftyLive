package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.niftylive.viewmodel.AuthState
import com.example.niftylive.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onGoToSettings: () -> Unit = {} // <-- 1. ADD THIS PARAMETER
) {
    val state = viewModel.authState.collectAsState().value

    var totp by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ... (Title, TOTP field, Login Button, State messages) ...
            
            // ... (Your 'when (state)' block) ...
            
            Spacer(Modifier.height(32.dp)) // Add some space

            // ✅ 2. ADD THIS "SETTINGS" BUTTON
            TextButton(onClick = onGoToSettings) {
                Text("Setup Credentials (Settings)")
            }
        }
    }
}
