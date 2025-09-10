package com.example.screens.footer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AppNavigationBar() {
    var selectedItemIndex by remember { mutableIntStateOf(3) }

    val items = listOf(
        "Home" to Icons.Default.Home,
        "Map" to Icons.Default.LocationOn,
        "Alerts" to Icons.Default.Notifications,
        "Profile" to Icons.Default.AccountCircle
    )

    NavigationBar {
        items.forEachIndexed { index, (title, icon) ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = { selectedItemIndex = index },
                label = { Text(title) },
                icon = { Icon(imageVector = icon, contentDescription = title) }
            )
        }
    }
}