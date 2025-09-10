
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AppNavigationBar2() {
    var selectedItemIndex by remember { mutableIntStateOf(3) }

    val items = listOf(

        "Map" to Icons.Default.Map,
        "Groups" to Icons.Default.Groups,
        "Profile" to Icons.Default.AccountCircle,
        "Reports" to Icons.Default.Report,
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
