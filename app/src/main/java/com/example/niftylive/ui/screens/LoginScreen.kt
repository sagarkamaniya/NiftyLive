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
    onGoToSettings: () -> Unit = {}
) {
    // This variable IS USED by the OutlinedTextField
    val state = viewModel.authState.collectAsState().value

    // This variable IS USED by the OutlinedTextField
    var totp by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("SmartAPI Login", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(24.dp))

            // ✅ THIS IS THE TOTP FIELD
            OutlinedTextField(
                value = totp,
                onValueChange = { totp = it },
                label = { Text("TOTP (2FA)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ✅ THIS IS THE LOGIN BUTTON
            Button(
                onClick = { viewModel.login(totp) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(Modifier.height(16.dp))

            // ✅ THIS 'when' BLOCK USES THE 'state' VARIABLE
            when (state) {
                is AuthState.Loading -> CircularProgressIndicator()
                is AuthState.Success -> {
                    Text(state.message, color = MaterialTheme.colorScheme.primary)
                    // This uses the 'onLoginSuccess' parameter
                    LaunchedEffect(Unit) {
                        onLoginSuccess()
                    }
                }
                is AuthState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                AuthState.Idle -> {}
            }

            Spacer(Modifier.height(32.dp))

            // ✅ THIS IS THE SETTINGS BUTTON
            TextButton(onClick = onGoToSettings) {
                Text("Setup Credentials (Settings)")
            }
        }
    }
}
