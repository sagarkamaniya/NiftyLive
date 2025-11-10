package com.example.niftylive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.niftylive.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AuthViewModel = viewModel(),
    onSettingsSaved: () -> Unit // This comes from the NavGraph
) {
    var clientCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") } // Your MPIN
    var apiKey by remember { mutableStateOf("") }
    var localIp by remember { mutableStateOf("") }
    var publicIp by remember { mutableStateOf("") }
    var macAddress by remember { mutableStateOf("") }

    var saveMessage by remember { mutableStateOf<String?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Enter Credentials", style = MaterialTheme.typography.headlineMedium)
            Text("This data will be saved securely one time.")

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = clientCode,
                onValueChange = { clientCode = it },
                label = { Text("Client Code") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (Your MPIN)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key (X-PrivateKey)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = localIp,
                onValueChange = { localIp = it },
                label = { Text("Local IP") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = publicIp,
                onValueChange = { publicIp = it },
                label = { Text("Public IP") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = macAddress,
                onValueChange = { macAddress = it },
                label = { Text("MAC Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    // 1. Save the data
                    viewModel.saveStaticCredentials(
                        clientCode = clientCode,
                        password = password,
                        apiKey = apiKey,
                        localIp = localIp,
                        publicIp = publicIp,
                        macAddress = macAddress
                    )
                    
                    // 2. Show a message
                    saveMessage = "Credentials Saved!"
                    
                    // 3. ✅ This is the line that triggers navigation
                    onSettingsSaved()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Credentials")
            }

            if (saveMessage != null) {
                Spacer(Modifier.height(16.dp))
                Text(saveMessage!!, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
