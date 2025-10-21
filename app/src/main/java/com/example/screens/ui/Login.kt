package com.example.screens.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.screens.ui.components.AppTextField
import com.example.screens.ui.theme.InputGreen
import com.example.screens.ui.theme.ScreensTheme
import com.example.screens.ui.auth.AuthViewModel

@Composable
fun LoginScreenWithNavigation(
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SafeZonePet",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(48.dp))

        AppTextField(
            value = username,
            onValueChange = {
                username = it
                showError = false
            },
            label = { Text("username") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = password,
            onValueChange = {
                password = it
                showError = false
            },
            label = { Text("password") }
        )

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Forgot password?",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { /* Lógica para recuperar contraseña */ }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                when {
                    username.isBlank() -> {
                        showError = true
                        errorMessage = "Please enter a username"
                    }
                    password.isBlank() -> {
                        showError = true
                        errorMessage = "Please enter a password"
                    }
                    password.length < 6 -> {
                        showError = true
                        errorMessage = "Password must be at least 6 characters"
                    }
                    else -> {
                        showError = false
                        viewModel.login(username.trim(), password){
                            onLoginSuccess()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Log In", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Or sign with",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onLoginSuccess() },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = InputGreen,
                    contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            ) {
                Text("Facebook", style = MaterialTheme.typography.labelLarge)
            }
            Button(
                onClick = { onLoginSuccess() },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = InputGreen,
                    contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            ) {
                Text("Twitter", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Don't have an account? Sign up",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onSignupClick() }
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun LoginScreenPreview() {
    ScreensTheme {
        Surface {
            LoginScreenWithNavigation(
                onLoginSuccess = {},
                onSignupClick = {}
            )
        }
    }
}