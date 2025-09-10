// Archivo: app/src/main/java/com/example/screens/ui/Signup.kt

package com.example.screens.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.screens.ui.components.AppTextField
import com.example.screens.ui.theme.ScreensTheme

@Composable
fun SignupButton(
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onSignupClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Sign up", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SignupScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { /* Lógica para volver atrás */ },
                modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "SafeZonePet",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Create your account",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppTextField(value = email,
            onValueChange = { email = it },
            label = {Text("Email")})

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(value = password,
            onValueChange = { password = it },
            label = {Text("Password")})

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = {Text("Confirm password")})

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Forgot password?",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Lógica para recuperar contraseña */ }
        )

        Spacer(modifier = Modifier.height(32.dp))

        SignupButton(onSignupClick = { /* Lógica de registro */ })

        Spacer(modifier = Modifier.weight(1f))

        Text(
            "Already have an account? Sign in",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable { /* Lógica para ir a Sign In */ }
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun SignupScreenPreview() {
    ScreensTheme {
        Surface {
            SignupScreen()
        }
    }
}