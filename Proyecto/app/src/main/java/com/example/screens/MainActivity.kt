package com.example.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.screens.navigation.AppNavigation
import com.example.screens.notifications.NotificationHelper
import com.example.screens.notifications.NotificationScheduler
import com.example.screens.ui.theme.ScreensTheme

class MainActivity : ComponentActivity() {

    private lateinit var notificationHelper: NotificationHelper
    private lateinit var notificationScheduler: NotificationScheduler

    // Launcher para solicitar permisos de notificación en Android 13+
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

            startNotificationMonitoring()
        } else {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar helpers de notificaciones
        notificationHelper = NotificationHelper(this)
        notificationScheduler = NotificationScheduler(this)

        // Solicitar permisos de notificación
        checkAndRequestNotificationPermission()

        enableEdgeToEdge()
        setContent {
            ScreensTheme {
                val navController = rememberNavController()

                // Manejar navegación desde notificaciones
                handleNotificationIntent(intent)

                AppNavigation(navController = navController)
            }
        }
    }


    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {

                    startNotificationMonitoring()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {

                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {

                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {

            startNotificationMonitoring()
        }
    }

    /**
     * Inicia el sistema de monitoreo de notificaciones
     */
    private fun startNotificationMonitoring() {

        notificationScheduler.startPeriodicMonitoring(intervalMinutes = 15)


        notificationScheduler.runImmediateCheck()


    }


    private fun handleNotificationIntent(intent: android.content.Intent?) {
        intent?.let {
            val navigateTo = it.getStringExtra("navigate_to")
            val petName = it.getStringExtra("pet_name")


            navigateTo?.let { route ->

                android.util.Log.d("MainActivity", "Navegando a: $route, mascota: $petName")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}