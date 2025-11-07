package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.niftylive.viewmodel.AuthViewModel
import com.example.niftylive.viewmodel.AuthState

@Composable
fun LoginScreen(
    navController: NavController? = null,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // ✅ Input states (fixes unresolved reference errors)
    var clientCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var totp by remember { mutableStateOf("") }

    val state by viewModel.authState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🔐 SmartAPI Login", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(24.dp))

            // ✅ Client Code
            OutlinedTextField(
                value = clientCode,
                onValueChange = { clientCode = it },
                label = { Text("Client Code") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // ✅ Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // ✅ API Key
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // ✅ TOTP / Auth Code
            OutlinedTextField(
                value = totp,
                onValueChange = { totp = it },
                label = { Text("TOTP / Auth Code") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // ✅ Login Button
            Button(
                onClick = {
                    viewModel.login(clientCode, password, apiKey, totp)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is AuthState.Loading
            ) {
                Text("Login")
            }

            Spacer(Modifier.height(16.dp))

            // ✅ State feedback
            when (val s = state) {
                is AuthState.Loading -> {
                    CircularProgressIndicator()
                }
                is AuthState.Error -> {
                    Text(
                        text = "Login failed: ${s.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is AuthState.Success -> {
                    Text("✅ Login success!", color = MaterialTheme.colorScheme.primary)
                    // Optionally navigate to Dashboard
                    // navController?.navigate("dashboard")
                }
                else -> Unit
            }
        }
    }
}