package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.niftylive.viewmodel.AuthState
import com.example.niftylive.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    // Use the StateFlow name `state` that your AuthViewModel exposes
    val authState by viewModel.state.collectAsState()

    var clientCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("SmartAPI Login", fontSize = 28.sp)

            OutlinedTextField(
                value = clientCode,
                onValueChange = { clientCode = it },
                label = { Text("Client Code") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("Auth Code / OTP") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.login(clientCode, password, apiKey, otp) },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(if (authState is AuthState.Loading) "Logging in..." else "Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (val s = authState) {
                is AuthState.Success -> {
                    Text(
                        text = "Login Successful! Token: ${s.token.takeIf { it.isNotBlank() } ?: "..."}",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Navigate to dashboard after a short delay so user can read the message
                    LaunchedEffect(s.token) {
                        delay(800)
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }

                is AuthState.Error -> {
                    Text(
                        text = "Error: ${s.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                else -> {
                    // Idle - do nothing
                }
            }
        }
    }
}