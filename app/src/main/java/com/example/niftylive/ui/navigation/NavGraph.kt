package com.example.niftylive.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.niftylive.ui.screens.LoginScreen
import com.example.niftylive.ui.screens.DashboardScreen
import com.example.niftylive.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(viewModel = authViewModel)
        }

        composable("dashboard") {
            DashboardScreen()
        }
    }
}