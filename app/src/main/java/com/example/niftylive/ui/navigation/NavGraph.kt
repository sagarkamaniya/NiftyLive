package com.example.niftylive.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.niftylive.ui.screens.LoginScreen
import com.example.niftylive.ui.screens.DashboardScreen
import com.example.niftylive.ui.screens.SettingsScreen // <-- 1. ADD THIS IMPORT
import com.example.niftylive.viewmodel.AuthViewModel
import com.example.niftylive.viewmodel.DashboardViewModel

object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val SETTINGS = "settings" // <-- 2. ADD THIS NEW ROUTE
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                // ✅ 3. ADD THIS NAVIGATION TO THE LOGIN SCREEN
                onGoToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(viewModel = dashboardViewModel)
        }

        // ✅ 4. ADD THIS ENTIRE NEW BLOCK FOR THE SETTINGS SCREEN
        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = authViewModel,
                onSettingsSaved = {
                    // When saved, just go back to the previous screen (Login)
                    navController.navigate(Routes.LOGIN) {
                        // This removes the SettingsScreen from the back stack,
                        // so you can't go "back" to it from the LoginScreen.
                        popUpTo(Routes.SETTINGS) { inclusive = true }
                    }

                }
            )
        }
    }
}
