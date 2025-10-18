package com.example.screens.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.screens.R
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.ui.theme.PetSafeGreen
import com.example.screens.ui.theme.ScreensTheme

@Composable
fun MissingPetCard(
    status: String,
    title: String,
    description: String,
    imageRes: Int,
    isLost: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isLost) MaterialTheme.colorScheme.error else PetSafeGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = imageRes),
                contentDescription = "Foto de la mascota",
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp)),
                tint = Color.Unspecified
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPageWithNavigation(
    navController: NavController,
    onBackClick: () -> Unit,
    onReportClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Community") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        bottomBar = {
            AppNavigationBar2(navController = navController)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Report Lost Pet") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Report Lost Pet") },
                onClick = { onReportClick() },
                containerColor = PetSafeGreen,
                contentColor = Color.Black
            )
        }
    ) { innerPadding ->
        CommunityScreen(
            modifier = Modifier.padding(innerPadding),
            onCardClick = onCardClick
        )
    }
}

@Composable
fun CommunityScreen(
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit
) {
    val scrollState = androidx.compose.foundation.rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lost & Found",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        MissingPetCard(
            status = "Lost",
            title = "Missing: Pancracio, Minecraft-addicted dog (Creole)",
            description = "Last seen playing Minecraft, NY. Please contact if found.",
            imageRes = R.drawable.community1,
            isLost = true,
            onClick = { onCardClick() }
        )

        MissingPetCard(
            status = "Found",
            title = "Found: 'Godzilla', a very friendly smiling dog (Chihuahua)",
            description = "Found near the library, seems lost. Contact for details.",
            imageRes = R.drawable.community2,
            isLost = false,
            onClick = { onCardClick() }
        )

        MissingPetCard(
            status = "Lost",
            title = "Missing: PonchoZuleta, little dog with a blue jacket (Chiguagua)",
            description = "Disappeared from home in Brooklyn. Very Friendly, answers to 'PonchoZuleta'.",
            imageRes = R.drawable.perro,
            isLost = true,
            onClick = { onCardClick() }
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityScreenPreview() {
    ScreensTheme {
        CommunityPageWithNavigation(
            navController = rememberNavController(),
            onBackClick = {},
            onReportClick = {},
            onCardClick = {}
        )
    }
}