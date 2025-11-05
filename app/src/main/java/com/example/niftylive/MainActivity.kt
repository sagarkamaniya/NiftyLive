package com.example.niftylive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.niftylive.di.ServiceLocator
import com.example.niftylive.ui.navigation.NavGraph
import com.example.niftylive.ui.theme.NiftyLiveTheme // ✅ This import is key!

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize app-wide dependencies
        ServiceLocator.initialize(this)

        setContent {
            AppContent()
        }
    }
}

@Composable
fun AppContent() {
    NiftyLiveTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            NavGraph(navController)
        }
    }
}