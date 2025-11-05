package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.niftylive.viewmodel.AuthState
import com.example.niftylive.viewmodel.AuthViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel = viewModel()) {
    var clientCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var authCode by remember { mutableStateOf("") }

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SmartAPI Login", fontSize = 24.sp)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = clientCode,
            onValueChange = { clientCode = it },
            label = { Text("Client Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("API Key") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = authCode,
            onValueChange = { authCode = it },
            label = { Text("Auth Code / OTP") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.login(clientCode, password, apiKey, authCode)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Login")
        }

        Spacer(Modifier.height(12.dp))

        when (val s = state) {
            is AuthState.Loading -> Text("Logging in...", color = Color.Gray)
            is AuthState.Success -> Text(
                text = "Login Successful! Token: ${s.token.take(10)}...",
                color = Color.Green
            )
            is AuthState.Error -> {
                Text(
                    text = "Error: ${s.message}",
                    color = Color.Red,
                    fontSize = 16.sp
                )
                if (!s.rawResponse.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Raw:\n${s.rawResponse}",
                        color = Color.DarkGray,
                        fontSize = 12.sp
                    )
                }
            }
            else -> {}
        }
    }
}