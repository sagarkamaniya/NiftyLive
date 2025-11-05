package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.niftylive.viewmodel.AuthViewModel

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val vm: AuthViewModel = viewModel()
    val state by vm.state.collectAsState()

    var client by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var authCode by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("SmartAPI Login", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = client, onValueChange = { client = it }, label = { Text("Client Code") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = apiKey, onValueChange = { apiKey = it }, label = { Text("API Key") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = authCode, onValueChange = { authCode = it }, label = { Text("Auth Code / OTP") })
        Spacer(Modifier.height(16.dp))
        Button(onClick = { vm.login(client, password, apiKey, authCode) }, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }

        when (val state = viewModel.state.collectAsState().value) {
    is AuthState.Error -> {
        Column {
            Text(
                text = "Error: ${state.message}",
                color = Color.Red,
                fontSize = 16.sp
            )
            if (!state.rawResponse.isNullOrBlank()) {
                Text(
                    text = "Raw:\n${state.rawResponse}",
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
            }
        }
    }
    else -> {}
}
    }
}
