package com.example.screens.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.screens.ui.components.AppTextField
import com.example.screens.ui.theme.PetSafeGreen
import com.example.screens.ui.theme.ScreensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreenWithNavigation(
    onBackClick: () -> Unit,
    onCreateClick: (String, String) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

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
                onValueChange = {
                    groupName = it
                    showError = false
                },
                label = { Text("Group Name") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = description,
                onValueChange = {
                    description = it
                    showError = false
                },
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

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (groupName.isNotBlank()) {
                        onCreateClick(groupName, description)
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PetSafeGreen,
                    contentColor = Color.Black
                )
            ) {
                Text("Create Group", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateGroupScreenPreview() {
    ScreensTheme {
        CreateGroupScreenWithNavigation(
            onBackClick = {},
            onCreateClick = { _, _ -> }
        )
    }
}