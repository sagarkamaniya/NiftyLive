package com.example.niftylive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.niftylive.ui.navigation.NavGraph
import com.example.niftylive.ui.theme.NiftyLiveTheme
import com.example.niftylive.viewmodel.AuthViewModel
import com.example.niftylive.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NiftyLiveTheme {
                Surface {
                    val navController = rememberNavController()
                    // get viewmodels (Hilt) — if you don't use Hilt, provide them from ServiceLocator
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val dashboardViewModel: DashboardViewModel = hiltViewModel()

                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        dashboardViewModel = dashboardViewModel,
                        onLogout = {
                            // navigate back to login and clear backstack
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}