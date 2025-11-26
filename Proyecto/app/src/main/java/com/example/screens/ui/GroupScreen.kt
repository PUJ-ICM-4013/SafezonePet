package com.example.screens.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.screens.data.Group
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.repository.GroupRepository
import com.example.screens.ui.theme.InputGreen
import com.example.screens.ui.theme.PetSafeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreenWithNavigation(
    navController: NavController,
    currentUserId: String,
    onBackClick: () -> Unit,
    onCreateGroupClick: () -> Unit = {},
    onGroupClick: (String) -> Unit = {} // ahora recibe groupId
) {
    val repo = remember { GroupRepository() }

    var groups by remember { mutableStateOf<List<Group>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUserId) {
        loading = true
        error = null
        try {
            groups = repo.getMyGroups(currentUserId)
        } catch (e: Exception) {
            e.printStackTrace()
            error = "No se pudieron cargar los grupos"
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Groups") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        bottomBar = { AppNavigationBar2(navController = navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Create Group") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Create Group") },
                onClick = { onCreateGroupClick() },
                containerColor = PetSafeGreen,
                contentColor = Color.Black
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "My Groups",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                when {
                    loading -> {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator()
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                    error != null -> {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    groups.isEmpty() -> {
                        Text(
                            text = "Aún no tienes grupos. Crea uno con el botón +",
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            items(groups) { group ->
                GroupItem(
                    group = group,
                    onClick = { onGroupClick(group.id) }
                )
            }
        }
    }
}

@Composable
fun GroupItem(
    group: Group,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = InputGreen)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = group.description,
                style = MaterialTheme.typography.bodyMedium,
                color = PetSafeGreen
            )
        }
    }
}
