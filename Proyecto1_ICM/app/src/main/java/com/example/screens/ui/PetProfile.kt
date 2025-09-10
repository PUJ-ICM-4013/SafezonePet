package com.example.screens.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.screens.R
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.ui.components.AppTextField
import com.example.screens.ui.theme.ScreensTheme

@Composable
fun ProfileHeader(name: String, breed: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.buddy), // 👈 imagen local en drawable
            contentDescription = "Pet profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(
                    width = 4.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (name.isNotBlank()) name else "Pet's Name",
            style = MaterialTheme.typography.displaySmall
        )
        Text(
            text = if (breed.isNotBlank()) breed else "Pet's Breed",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetProfilePage() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pet Profile") },
                navigationIcon = {
                    IconButton(onClick = { /* Volver atrás */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = { AppNavigationBar2() }
    ) { innerPadding ->
        PetProfileScreen(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun PetProfileScreen(modifier: Modifier = Modifier) {
    var petName by remember { mutableStateOf("Buddy") }
    var breed by remember { mutableStateOf("Chiguauga") }
    var age by remember { mutableStateOf("2") }
    var vet by remember { mutableStateOf("Dr. Smith") }
    var address by remember { mutableStateOf("123 Vet Street") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileHeader(name = petName, breed = breed)

        Spacer(modifier = Modifier.height(32.dp))

        AppTextField(value = petName,
            onValueChange = { petName = it },
            label = { Text("Pet's Name") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(value = breed,
            onValueChange = { breed = it },
            label = { Text("Breed") })

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(value = age,
            onValueChange = { age = it },
            label = { Text("Age (years)") })

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(value = vet,
            onValueChange = { vet = it },
            label = { Text("Regular Veterinarian") })

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(value = address,
            onValueChange = { address = it },
            label = { Text("Veterinarian's Address") })

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { println("Profile Saved!") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Save Changes", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetProfileScreenPreview() {
    ScreensTheme {
        Surface {
            PetProfilePage()
        }
    }
}
