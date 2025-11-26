package com.example.screens.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.screens.repository.GroupRepository
import com.example.screens.ui.components.AppTextField
import com.example.screens.ui.theme.PetSafeGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreenWithNavigation(
    currentUserId: String,
    onBackClick: () -> Unit,
    onCreated: (String) -> Unit // groupId
) {
    val repo = remember { GroupRepository() }
    val scope = rememberCoroutineScope()

    var groupName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var creating by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Group") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create a new group to share pet tracking with family and friends",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            AppTextField(
                value = groupName,
                onValueChange = { groupName = it; showError = false; errorText = null },
                label = { Text("Group Name") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = description,
                onValueChange = { description = it; showError = false; errorText = null },
                label = { Text("Description") },
                singleLine = false,
                modifier = Modifier.height(120.dp)
            )

            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please enter a group name",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (errorText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorText!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (groupName.isBlank()) {
                        showError = true
                        return@Button
                    }
                    scope.launch {
                        creating = true
                        errorText = null
                        try {
                            val group = repo.createGroup(
                                ownerId = currentUserId,
                                name = groupName,
                                description = description
                            )
                            onCreated(group.id) // navega a detalle
                        } catch (e: Exception) {
                            e.printStackTrace()
                            errorText = "No se pudo crear el grupo"
                        } finally {
                            creating = false
                        }
                    }
                },
                enabled = !creating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PetSafeGreen,
                    contentColor = Color.Black
                )
            ) {
                if (creating) CircularProgressIndicator(modifier = Modifier.size(18.dp))
                else Text("Create Group", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
