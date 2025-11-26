package com.example.screens.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.screens.data.GroupMember
import com.example.screens.data.Pet
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.repository.GroupRepository
import com.example.screens.ui.theme.PetSafeGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreenWithNavigation(
    navController: NavController,
    groupId: String,
    groupName: String,
    onBackClick: () -> Unit
) {
    val repo = remember { GroupRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var members by remember { mutableStateOf<List<GroupMember>>(emptyList()) }
    var pets by remember { mutableStateOf<List<Pet>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }

    val petsByOwner = remember(pets) { pets.groupBy { it.ownerId } }

    LaunchedEffect(groupId, refreshKey) {
        loading = true
        try {
            members = repo.getGroupMembers(groupId)
            pets = repo.getGroupPets(groupId)
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar("Error cargando el grupo")
        } finally {
            loading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(groupName) },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = { AppNavigationBar2(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMemberDialog = true },
                containerColor = PetSafeGreen,
                contentColor = Color.Black
            ) { Icon(Icons.Default.Add, "Add Member") }
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
                    text = "Group Members",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${members.size} members",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (loading) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            items(members) { member ->
                GroupMemberWithPetsItem(
                    member = member,
                    pets = petsByOwner[member.uid].orEmpty(),
                    onRemoveClick = {
                        scope.launch {
                            val ok = repo.removeMember(groupId, member.uid)
                            if (!ok) snackbarHostState.showSnackbar("No se pudo remover al miembro")
                            refreshKey++
                        }
                    }
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "All pets in this group",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
            }

            items(pets) { pet ->
                PetItem(pet = pet)
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onConfirm = { email ->
                scope.launch {
                    val ok = repo.addMemberByEmail(groupId, email)
                    if (!ok) snackbarHostState.showSnackbar("No existe un usuario con ese email")
                    showAddMemberDialog = false
                    refreshKey++
                }
            }
        )
    }
}

@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Member") },
        text = {
            Column {
                Text("Enter the email address of the person you want to add:")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(email.trim()) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}


@Composable
private fun GroupMemberWithPetsItem(
    member: GroupMember,
    pets: List<Pet>,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = if (member.name.isNotBlank()) member.name else "Member",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = member.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = onRemoveClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove member",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (pets.isEmpty()) {
                Text("No pets yet", color = Color.Gray)
            } else {
                Text(
                    text = "Pets:",
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                pets.forEach { pet ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Pets, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("${pet.name} • ${pet.breed} • ${pet.age}y")
                    }
                }
            }
        }
    }
}

@Composable
private fun PetItem(pet: Pet) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Pets, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(pet.name, fontWeight = FontWeight.Bold)
                Text("${pet.breed} • ${pet.age} years", color = Color.Gray)
            }
        }
    }
}
