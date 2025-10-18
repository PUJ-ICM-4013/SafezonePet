package com.example.screens.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.ui.theme.PetSafeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectTrackerPageWithNavigation(
    navController: NavController,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onConnectClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "SafeZonePet",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onSettingsClick() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        bottomBar = {
            AppNavigationBar2(navController = navController)
        }
    ) { innerPadding ->
        ConnectTrackerScreen(
            modifier = Modifier.padding(innerPadding),
            onConnectClick = onConnectClick
        )
    }
}

@Composable
fun ConnectTrackerScreen(
    modifier: Modifier = Modifier,
    onConnectClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Connect your tracker",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Press the button below to start the connection process with your GPS tracker.",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { onConnectClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = PetSafeGreen,
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp)
        ) {
            Text(
                text = "Connect Tracker",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewConnectTrackerScreen() {
    ConnectTrackerPageWithNavigation(
        navController = rememberNavController(),
        onBackClick = {},
        onSettingsClick = {},
        onConnectClick = {}
    )
}