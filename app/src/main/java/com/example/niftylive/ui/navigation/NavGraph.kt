package com.example.niftylive.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.niftylive.ui.screens.DashboardScreen
import com.example.niftylive.ui.screens.LoginScreen
import com.example.niftylive.viewmodel.AuthViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            // get or create a ViewModel scoped to the composition
            val authVm: AuthViewModel = viewModel()
            LoginScreen(navController = navController, viewModel = authVm)
        }

        composable("dashboard") {
            DashboardScreen()
        }
    }
}