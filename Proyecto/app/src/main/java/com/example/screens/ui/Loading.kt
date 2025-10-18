package com.example.screens.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.screens.R
import com.example.screens.ui.theme.ScreensTheme
import kotlinx.coroutines.delay

@Composable
fun PetPlaceholder(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.loading),
        contentDescription = "Imagen de mascota",
        modifier = modifier
            .width(200.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingPageWithNavigation(
    navController: NavController,
    onLoadingComplete: () -> Unit,
    onSettingsClick: () -> Unit
) {
    // Simular carga automática
    LaunchedEffect(Unit) {
        delay(3000) // 3 segundos de carga
        onLoadingComplete()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SafeZonePet") },
                actions = {
                    IconButton(onClick = { onSettingsClick() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LoadingScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Loading...",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        PetPlaceholder()
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    ScreensTheme {
        LoadingPageWithNavigation(
            navController = rememberNavController(),
            onLoadingComplete = {},
            onSettingsClick = {}
        )
    }
}