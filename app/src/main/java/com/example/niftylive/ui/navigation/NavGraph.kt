package com.example.niftylive.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.niftylive.ui.screens.LoginScreen
import com.example.niftylive.ui.screens.DashboardScreen
import com.example.niftylive.viewmodel.AuthViewModel
import com.example.niftylive.viewmodel.DashboardViewModel
import com.example.niftylive.data.repository.NiftyRepository

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    onLogout: () -> Unit
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { navController.navigate("dashboard") }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onLogout = {
                    onLogout()
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
    }
}