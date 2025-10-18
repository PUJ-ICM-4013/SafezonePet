package com.example.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.screens.navigation.AppNavigation
import com.example.screens.ui.theme.ScreensTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScreensTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}