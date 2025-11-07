package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.niftylive.viewmodel.AuthState
import com.example.niftylive.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val clientCode by viewModel.clientCode.collectAsState()
    val password by viewModel.password.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val totp by viewModel.totp.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            onLoginSuccess()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🔐 SmartAPI Login", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = clientCode,
                onValueChange = { viewModel.clientCode.value = it },
                label = { Text("Client Code") },
                singleLine = true
            )
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Password") },
                singleLine = true
            )
            OutlinedTextField(
                value = apiKey,
                onValueChange = { viewModel.apiKey.value = it },
                label = { Text("API Key") },
                singleLine = true
            )
            OutlinedTextField(
                value = totp,
                onValueChange = { viewModel.totp.value = it },
                label = { Text("TOTP / Auth Code") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.login() },
                enabled = state !is AuthState.Loading
            ) {
                Text(if (state is AuthState.Loading) "Logging in..." else "Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (state) {
                is AuthState.Error -> Text(
                    text = (state as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                is AuthState.Success -> Text("✅ Login Successful")
                else -> {}
            }
        }
    }
}