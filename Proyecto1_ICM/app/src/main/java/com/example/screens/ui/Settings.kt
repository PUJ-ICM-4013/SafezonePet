package com.example.proyecto1_icm.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.ui.theme.ScreensTheme   // 👈 Importar el tema
import com.example.screens.ui.theme.InputGreen   // 👈 Importar colores si los necesitas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage() {
    ScreensTheme {   // 👈 Envolvemos todo en el tema
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Settings",
                            style = MaterialTheme.typography.titleLarge, // 👈 Fuente del tema
                            color = MaterialTheme.colorScheme.primary    // 👈 Color del tema
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* Acción volver */ }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface // 👈 Color coherente
                            )
                        }
                    }
                )
            },
            bottomBar = {
                AppNavigationBar2()
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
    // Estados de los switches
    var petTracking by remember { mutableStateOf(false) }
    var peopleTracking by remember { mutableStateOf(false) }
    var petZoneAlerts by remember { mutableStateOf(false) }
    var peopleZoneAlerts by remember { mutableStateOf(false) }
    var batteryAlerts by remember { mutableStateOf(false) }

    // Estados de sliders
    var updateFrequency by remember { mutableStateOf(10f) }
    var safeZoneRadius by remember { mutableStateOf(50f) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // ---------- Tracking ----------
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

        // ---------- Notifications ----------
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

        // ---------- App Settings ----------
        Text(
            "App Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Update Frequency
        Text("Update Frequency", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Text("Adjust the frequency of location updates", style = MaterialTheme.typography.bodySmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Update Frequency (seconds)", style = MaterialTheme.typography.bodyMedium)
            Text("${updateFrequency.toInt()}s", style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = updateFrequency,
            onValueChange = { updateFrequency = it },
            valueRange = 5f..60f,
            colors = SliderDefaults.colors(
                thumbColor = InputGreen, // 👈 Usando color personalizado
                activeTrackColor = InputGreen
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Safe Zone Radius
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

        // ---------- Links ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Acción Privacy Policy */ }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Privacy Policy", style = MaterialTheme.typography.bodyLarge)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Go", tint = MaterialTheme.colorScheme.primary)
        }

        Divider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Acción Terms of Service */ }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Terms of Service", style = MaterialTheme.typography.bodyLarge)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Go", tint = MaterialTheme.colorScheme.primary)
        }
    }
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
                checkedThumbColor = InputGreen,   // 👈 Colores del tema
                checkedTrackColor = InputGreen.copy(alpha = 0.6f)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    ScreensTheme {   // 👈 Importante para que el preview herede los colores/fuentes
        SettingsPage()
    }
}
