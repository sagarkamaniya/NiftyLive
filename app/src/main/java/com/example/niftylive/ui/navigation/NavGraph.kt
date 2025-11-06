package com.example.niftylive.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.niftylive.ui.screens.LoginScreen
import com.example.niftylive.ui.screens.DashboardScreen

/**
 * Handles all in-app navigation using Jetpack Compose Navigation.
 * Start destination is LoginScreen → navigates to DashboardScreen after success.
 */
@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // 🟢 LOGIN SCREEN
        composable("login") {
            LoginScreen(navController = navController)
        }

        // 🟣 DASHBOARD SCREEN
        composable("dashboard") {
            DashboardScreen()
        }
    }
}