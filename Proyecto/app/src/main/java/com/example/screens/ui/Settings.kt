package com.example.screens.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.screens.data.NotificationType
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.notifications.NotificationHelper
import com.example.screens.notifications.NotificationScheduler
import com.example.screens.ui.theme.InputGreen
import com.example.screens.ui.theme.PetSafeGreen
import com.example.screens.ui.theme.ScreensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPageWithNavigation(
    navController: NavController,
    onBackClick: () -> Unit
) {
    ScreensTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Settings",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onBackClick() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            },
            bottomBar = {
                AppNavigationBar2(navController = navController)
            }
        ) { innerPadding ->
            SettingsScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }
    val notificationScheduler = remember { NotificationScheduler(context) }

    var petTracking by remember { mutableStateOf(false) }
    var peopleTracking by remember { mutableStateOf(false) }
    var petZoneAlerts by remember { mutableStateOf(true) }
    var peopleZoneAlerts by remember { mutableStateOf(false) }
    var batteryAlerts by remember { mutableStateOf(true) }
    var periodicMonitoring by remember { mutableStateOf(true) }
    var updateFrequency by remember { mutableFloatStateOf(15f) }
    var safeZoneRadius by remember { mutableFloatStateOf(50f) }

    var showTestDialog by remember { mutableStateOf(false) }

    // Control del monitoreo periódico
    LaunchedEffect(periodicMonitoring) {
        if (periodicMonitoring) {
            notificationScheduler.startPeriodicMonitoring(updateFrequency.toLong())
        } else {
            notificationScheduler.stopPeriodicMonitoring()
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // Sección de Prueba de Notificaciones
        Text(
            "Test Notifications",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PetSafeGreen
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showTestDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PetSafeGreen
            )
        ) {
            Icon(Icons.Default.Notifications, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Probar Notificaciones")
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Monitoreo Automático
        Text(
            "Automatic Monitoring",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        SettingSwitch(
            title = "Periodic Monitoring",
            description = "Automatically check pet status every few minutes",
            checked = periodicMonitoring,
            onCheckedChange = { periodicMonitoring = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Tracking",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        SettingSwitch(
            title = "Pet Tracking",
            description = "Enable real-time tracking for your pet",
            checked = petTracking,
            onCheckedChange = { petTracking = it }
        )

        SettingSwitch(
            title = "People Tracking",
            description = "Enable real-time tracking for people",
            checked = peopleTracking,
            onCheckedChange = { peopleTracking = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Notifications",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        SettingSwitch(
            title = "Pet Safe Zone Alerts",
            description = "Receive alerts when your pet leaves the safe zone",
            checked = petZoneAlerts,
            onCheckedChange = { petZoneAlerts = it }
        )

        SettingSwitch(
            title = "People Safe Zone Alerts",
            description = "Receive alerts when people leave the safe zone",
            checked = peopleZoneAlerts,
            onCheckedChange = { peopleZoneAlerts = it }
        )

        SettingSwitch(
            title = "Low Battery Alerts",
            description = "Receive notifications for low battery on tracking devices",
            checked = batteryAlerts,
            onCheckedChange = { batteryAlerts = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "App Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("Update Frequency", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Text("Adjust the frequency of monitoring checks", style = MaterialTheme.typography.bodySmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Check Interval (minutes)", style = MaterialTheme.typography.bodyMedium)
            Text("${updateFrequency.toInt()} min", style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = updateFrequency,
            onValueChange = { updateFrequency = it },
            valueRange = 5f..60f,
            colors = SliderDefaults.colors(
                thumbColor = InputGreen,
                activeTrackColor = InputGreen
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Safe Zone Radius", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Text("Set the radius for the safe zone", style = MaterialTheme.typography.bodySmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Safe Zone Radius (meters)", style = MaterialTheme.typography.bodyMedium)
            Text("${safeZoneRadius.toInt()}m", style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = safeZoneRadius,
            onValueChange = { safeZoneRadius = it },
            valueRange = 10f..200f,
            colors = SliderDefaults.colors(
                thumbColor = InputGreen,
                activeTrackColor = InputGreen
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Privacy Policy", style = MaterialTheme.typography.bodyLarge)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Go", tint = MaterialTheme.colorScheme.primary)
        }

        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Terms of Service", style = MaterialTheme.typography.bodyLarge)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Go", tint = MaterialTheme.colorScheme.primary)
        }
    }

    // Diálogo de prueba de notificaciones
    if (showTestDialog) {
        TestNotificationsDialog(
            onDismiss = { showTestDialog = false },
            notificationHelper = notificationHelper,
            notificationScheduler = notificationScheduler
        )
    }
}

@Composable
fun TestNotificationsDialog(
    onDismiss: () -> Unit,
    notificationHelper: NotificationHelper,
    notificationScheduler: NotificationScheduler
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Probar Notificaciones") },
        text = {
            Column {
                Text("Selecciona el tipo de notificación que deseas probar:")
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Notificación de salida de zona
                Button(
                    onClick = {
                        notificationHelper.sendGeofenceExitNotification("Buddy")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PetSafeGreen)
                ) {
                    Text("Alerta: Mascota fuera de zona")
                }

                // Notificación de regreso
                Button(
                    onClick = {
                        notificationHelper.sendGeofenceEnterNotification("Max")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PetSafeGreen)
                ) {
                    Text("Mascota de regreso")
                }

                // Notificación de batería baja
                Button(
                    onClick = {
                        notificationHelper.sendLowBatteryNotification("Charlie", 15)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PetSafeGreen)
                ) {
                    Text("Batería baja")
                }

                // Notificación de mascota perdida
                Button(
                    onClick = {
                        notificationHelper.sendLostPetReportNotification("Luna", "Central Park")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PetSafeGreen)
                ) {
                    Text("Mascota perdida reportada")
                }

                // Verificación inmediata
                Button(
                    onClick = {
                        notificationScheduler.runImmediateCheck()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = InputGreen)
                ) {
                    Text("Ejecutar verificación inmediata")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun SettingSwitch(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = InputGreen,
                checkedTrackColor = InputGreen.copy(alpha = 0.6f)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    ScreensTheme {
        SettingsPageWithNavigation(
            navController = rememberNavController(),
            onBackClick = {}
        )
    }
}