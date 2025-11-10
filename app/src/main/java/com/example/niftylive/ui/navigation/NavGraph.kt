package com.example.niftylive.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.niftylive.ui.screens.DashboardScreen
import com.example.niftylive.ui.screens.LoginScreen
import com.example.niftylive.ui.screens.SettingsScreen
import com.example.niftylive.viewmodel.AuthViewModel
import com.example.niftylive.viewmodel.DashboardViewModel

object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val SETTINGS = "settings"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel
) {
    NavHost(
        navController = navController,
        // ✅ Sets the "Settings" screen as the first screen
        startDestination = Routes.SETTINGS
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoToSettings = {
                    // This lets you go from Login *back* to Settings
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen()
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = authViewModel,
                // ✅ This is the updated navigation logic
                onSettingsSaved = {
                    // Go to the Login screen
                    navController.navigate(Routes.LOGIN) {
                        // Remove the Settings screen from history
                        popUpTo(Routes.SETTINGS) { inclusive = true }
                    }
                }
            )
        }
    }
}
