package com.example.screens.footer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.screens.navigation.Screen

@Composable
fun AppNavigationBar2(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        "Map" to Icons.Default.Map to Screen.Map.route,
        "Groups" to Icons.Default.Groups to Screen.Groups.route,
        "Profile" to Icons.Default.AccountCircle to Screen.PetProfile.route,
        "Reports" to Icons.Default.Report to Screen.Community.route
    )

    NavigationBar {
        items.forEach { (titleIcon, route) ->
            val (title, icon) = titleIcon
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = { Text(title) },
                icon = { Icon(imageVector = icon, contentDescription = title) }
            )
        }
    }
}