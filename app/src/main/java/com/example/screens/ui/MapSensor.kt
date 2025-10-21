package com.example.screens.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.screens.Data.Pet
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.ui.theme.ScreensTheme
import com.example.screens.ui.theme.PetSafeGreen
import com.example.screens.ui.theme.TextWhite

@Composable
fun PetListItem(pet: Pet, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = pet.imageRes),
            contentDescription = "Foto de ${pet.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = pet.name,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Search pets or locations...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun MapPlaceholder(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.mapa),
        contentDescription = "Vista del mapa",
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPageWithNavigation(
    navController: NavController,
    onSettingsClick: () -> Unit,
    onConnectClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    onLocationHistoryClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SafeZone") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onConnectClick() },
                containerColor = PetSafeGreen,
                contentColor = TextWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        MapScreenContent(
            modifier = Modifier.padding(innerPadding),
            onConnectClick = onConnectClick
        )
    }
}

@Composable
fun MapScreenContent(
    modifier: Modifier = Modifier,
    onConnectClick: () -> Unit
) {
    val registeredPets = listOf(
        Pet("Buddy", R.drawable.buddy),
        Pet("Max", R.drawable.max),
        Pet("Charlie", R.drawable.charlie),
        Pet("Cooper", R.drawable.cooper),
        Pet("Rocky", R.drawable.rocky)
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        SearchBar()

        Spacer(modifier = Modifier.height(16.dp))

        MapPlaceholder()

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onConnectClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Connect sensor")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Registered Pets",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        registeredPets.forEach { pet ->
            PetListItem(pet = pet)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun MapPagePreview() {
    ScreensTheme {
        MapPageWithNavigation(
            navController = rememberNavController(),
            onSettingsClick = {},
            onConnectClick = {}
        )
    }
}