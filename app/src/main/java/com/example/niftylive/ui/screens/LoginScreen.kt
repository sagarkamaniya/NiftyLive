package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.niftylive.viewmodel.AuthViewModel
import com.example.niftylive.viewmodel.AuthState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.niftylive.ui.navigation.Screen

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    var clientCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var totp by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("🔐 SmartAPI Login", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = clientCode,
                onValueChange = { clientCode = it },
                label = { Text("Client Code") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = totp,
                onValueChange = { totp = it },
                label = { Text("TOTP Code") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.login(clientCode, password, apiKey, totp)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is AuthState.Loading
            ) {
                Text(if (state is AuthState.Loading) "Logging in..." else "Login")
            }

            when (val s = state) {
                is AuthState.Error -> Text(
                    text = s.message,
                    color = MaterialTheme.colorScheme.error
                )
                is AuthState.Success -> {
                    // ✅ Navigate to Dashboard once logged in
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}