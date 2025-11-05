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

        when (state) {
            is com.example.niftylive.viewmodel.AuthState.Loading -> {
                Spacer(Modifier.height(12.dp))
                CircularProgressIndicator()
            }
            is com.example.niftylive.viewmodel.AuthState.Success -> {
                LaunchedEffect(Unit) { onLoginSuccess() }
            }
            is com.example.niftylive.viewmodel.AuthState.Error -> {
                Spacer(Modifier.height(12.dp))
                Text("Error: ${(state as com.example.niftylive.viewmodel.AuthState.Error).message}", color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}
