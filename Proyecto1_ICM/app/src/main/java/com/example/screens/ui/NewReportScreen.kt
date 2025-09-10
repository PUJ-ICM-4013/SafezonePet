package com.example.screens.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.screens.ui.theme.PetSafeGreen
import com.example.screens.ui.components.AppTextField
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen() {
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Report") },
                navigationIcon = {
                    IconButton(onClick = { /* Acción de regresar */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },

                )
        },
        bottomBar = {
            Button(
                onClick = { /* Acción de enviar */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = PetSafeGreen,
                    contentColor = Color.Black
                )

            ) {
                Text("Submit Report", style = MaterialTheme.typography.labelLarge)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Text("Report Details", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Enter Title") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Details", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Enter Details") },
                singleLine = false,
                modifier = Modifier.height(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Media", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* Subir video */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PetSafeGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Upload Video")
                }
                Button(
                    onClick = { /* Subir foto */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PetSafeGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Upload Photo")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { /* Tomar foto */ },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, PetSafeGreen),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Icon(Icons.Filled.PhotoCamera, contentDescription = "Take Photo")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Take Photo")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNewReportScreen() {
    NewReportScreen()
}