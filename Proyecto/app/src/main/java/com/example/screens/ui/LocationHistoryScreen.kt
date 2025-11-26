package com.example.screens.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.screens.data.LocationHistoryItem
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.ui.theme.PetSafeGreen
import com.example.screens.ui.theme.ScreensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationHistoryScreenWithNavigation(
    navController: NavController,
    onBackClick: () -> Unit
) {
    val locationHistory = listOf(
        LocationHistoryItem(1, "Buddy", "Central Park, NY", "Today, 10:30 AM", "Inside Safe Zone"),
        LocationHistoryItem(2, "Buddy", "5th Avenue, NY", "Today, 9:15 AM", "Outside Safe Zone"),
        LocationHistoryItem(3, "Max", "Brooklyn Bridge, NY", "Yesterday, 8:45 PM", "Inside Safe Zone"),
        LocationHistoryItem(4, "Charlie", "Times Square, NY", "Yesterday, 6:20 PM", "Inside Safe Zone"),
        LocationHistoryItem(5, "Buddy", "Madison Square, NY", "Yesterday, 2:10 PM", "Outside Safe Zone")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Location History") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            AppNavigationBar2(navController = navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Recent Locations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(locationHistory) { history ->
                LocationHistoryCard(history = history)
            }
        }
    }
}

@Composable
fun LocationHistoryCard(history: LocationHistoryItem) {
    val statusColor = if (history.status.contains("Inside")) PetSafeGreen else MaterialTheme.colorScheme.error

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(alpha = 0.1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = history.petName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = history.location,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = history.dateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusColor.copy(alpha = 0.1f)
            ) {
                Text(
                    text = history.status,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationHistoryScreenPreview() {
    ScreensTheme {
        LocationHistoryScreenWithNavigation(
            navController = rememberNavController(),
            onBackClick = {}
        )
    }
}