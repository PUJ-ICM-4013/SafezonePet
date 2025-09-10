package com.example.screens.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.screens.footer.AppNavigationBar
import com.example.screens.ui.components.AppTextField
import com.example.screens.ui.theme.InputGreen
import com.example.screens.ui.theme.ScreensTheme

@Composable
fun LoginButton(onLoginClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onLoginClick,
        modifier = modifier.height(50.dp)
    ) {
        Text("Log In", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun FacebookButton(onFacebookClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onFacebookClick,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = InputGreen,
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    ) {
        Text("Facebook", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun TwitterButton(onTwitterClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onTwitterClick,
        modifier = modifier.height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = InputGreen,
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    ) {
        Text("Twitter", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun LoginPage() {
    Scaffold(
        bottomBar = { AppNavigationBar() }
    ) { innerPadding ->
        LoginScreen(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun LoginScreen(modifier: Modifier) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
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
            onValueChange = { username = it },
            label = {Text("username")}
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = {Text("password")}
        )

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

        LoginButton(
            onLoginClick = {
                println("Login clickeado. Usuario: $username, Contraseña: $password")
            },
            modifier = Modifier.fillMaxWidth()
        )

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
            FacebookButton(
                onFacebookClick = { println("Facebook clickeado") },
                modifier = Modifier.weight(1f)
            )
            TwitterButton(
                onTwitterClick = { println("Twitter clickeado") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun LoginScreenPreview() {
    ScreensTheme {
        Surface {
            LoginPage()
        }
    }
}