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
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun MissingPetCard(
    status: String,
    title: String,
    description: String,
    imageUrl: String,
    placeholderRes: Int,
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
            modifier = Modifier.fillMaxWidth().padding(16.dp),
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

            // Si tienes Coil (ya la usas en otras pantallas), esto te sirve:
            if (imageUrl.isNotBlank()) {
                coil.compose.AsyncImage(
                    model = imageUrl,
                    contentDescription = "Pet photo",
                    modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(id = placeholderRes),
                    contentDescription = "Pet placeholder",
                    modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp)),
                    tint = Color.Unspecified
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPageWithNavigation(
    navController: NavController,
    onBackClick: () -> Unit,
    onReportClick: () -> Unit,
    onCardClick: (String) -> Unit // <- reportId
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
        bottomBar = { AppNavigationBar2(navController = navController) },
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
    onCardClick: (String) -> Unit
) {
    val repo = remember { com.example.screens.repository.CommunityReportRepository() }

    var reports by remember { mutableStateOf(emptyList<com.example.screens.data.CommunityReport>()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        loading = true
        error = null
        try {
            reports = repo.getRecentReports()
        } catch (e: Exception) {
            e.printStackTrace()
            error = "No se pudieron cargar reportes"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
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

        when {
            loading -> CircularProgressIndicator()
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            reports.isEmpty() -> Text("No hay reportes aún. Crea uno con el botón +", color = Color.Gray)
            else -> {
                reports.forEach { r ->
                    val isLost = r.status.uppercase() == "LOST"
                    MissingPetCard(
                        status = if (isLost) "Lost" else "Found",
                        title = r.title,
                        description = r.description,
                        imageUrl = r.imageUrl,
                        placeholderRes = R.drawable.perro,
                        isLost = isLost,
                        onClick = { onCardClick(r.id) }
                    )
                }
            }
        }

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
