package com.example.niftylive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.niftylive.di.ServiceLocator
import com.example.niftylive.ui.navigation.NavGraph
import com.example.niftylive.ui.theme.NiftyLiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize app-wide singletons that need Context
        ServiceLocator.initialize(this)

        setContent {
            NiftyLiveTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Create NavController and pass it to NavGraph
                    val navController = rememberNavController()
                    NavGraph(navController)
                }
            }
        }
    }
}