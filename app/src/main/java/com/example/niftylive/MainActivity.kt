package com.example.niftylive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.niftylive.ui.navigation.NavGraph
import com.example.niftylive.viewmodel.AuthViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NiftyLiveApp(authViewModel)
        }
    }
}

@Composable
fun NiftyLiveApp(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()

    Surface(color = MaterialTheme.colorScheme.background) {
        NavGraph(
            navController = navController,
            authViewModel = authViewModel
        )
    }
}