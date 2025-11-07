package com.example.niftylive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.niftylive.ui.navigation.NavGraph
import com.example.niftylive.ui.theme.NiftyLiveTheme
import com.example.niftylive.viewmodel.AuthViewModel
import com.example.niftylive.viewmodel.DashboardViewModel
import com.example.niftylive.di.ServiceLocator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.initialize(applicationContext)

        setContent {
            NiftyLiveTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    // Instantiate ViewModels manually (no Hilt)
                    val authViewModel = remember {
                        AuthViewModel(ServiceLocator.niftyRepository)
                    }
                    val dashboardViewModel = remember {
                        DashboardViewModel(ServiceLocator.niftyRepository)
                    }

                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        dashboardViewModel = dashboardViewModel,
                        onLogout = {
                            dashboardViewModel.logout()
                            navController.navigate("login") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}