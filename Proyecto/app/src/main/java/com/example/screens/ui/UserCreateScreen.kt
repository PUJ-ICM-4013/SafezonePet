package com.example.screens.ui

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class UserExtraData(
    val uid: String? = null,
    val name: String,
    val phone: String,
    val address: String,
    val email: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCreateScreen(
    modifier: Modifier = Modifier,
    uid: String? = null,                 // opcional: para asociar en backend
    emailPrefill: String? = null,        // opcional: mostrar email (solo lectura)
    onBack: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onSubmit: (UserExtraData) -> Unit = {}
) {
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }

    val inputContainer = Color(0xFFEFF6F1)
    val placeholderColor = Color(0xFF446B58)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        text = "SafeZonePet",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) { inner ->
        Column(
            modifier = modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                // puedes dejar "Create your account" si quieres calcar el diseño
                "Create your account",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
            Spacer(Modifier.height(16.dp))

            // ---- (Opcional) Email read-only, si lo envías por args ----
            if (!emailPrefill.isNullOrBlank()) {
                TextField(
                    value = emailPrefill,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = false,
                    shape = RoundedCornerShape(20.dp),
                    label = { Text("Email") },
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = inputContainer,
                        disabledTextColor = Color.Unspecified,
                        disabledIndicatorColor = Color.Transparent,
                        disabledLabelColor = Color.Unspecified,
                        focusedContainerColor = inputContainer,
                        unfocusedContainerColor = inputContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(Modifier.height(12.dp))
            }

            // ---- Name ----
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text("Name", color = placeholderColor) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = inputContainer,
                    unfocusedContainerColor = inputContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(Modifier.height(12.dp))

            // ---- Phone ----
            TextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text("Phone Number", color = placeholderColor) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = inputContainer,
                    unfocusedContainerColor = inputContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(12.dp))

            // ---- Address ----
            TextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text("Address", color = placeholderColor) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = inputContainer,
                    unfocusedContainerColor = inputContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(Modifier.height(24.dp))

            val primaryGreen = Color(0xFF2EEA6A)
            Button(
                onClick = {
                    if (canSubmitExtra(name, phone, address)) {
                        onSubmit(
                            UserExtraData(
                                uid = uid,
                                name = name.trim(),
                                phone = phone.trim(),
                                address = address.trim(),
                                email = emailPrefill
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                enabled = canSubmitExtra(name, phone, address)
            ) {
                Text(
                    "Continue",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
            }

            Spacer(Modifier.height(28.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val link = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(SpanStyle(color = placeholderColor, fontWeight = FontWeight.SemiBold)) {
                        append("Sign in")
                    }
                }
                Text(
                    text = link,
                    modifier = Modifier.clickable { onSignInClick() },
                    style = MaterialTheme.typography.bodyLarge,
                    color = placeholderColor
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun canSubmitExtra(name: String, phone: String, address: String): Boolean {
    val phoneOk = phone.isNotBlank() && Patterns.PHONE.matcher(phone).matches()
    return name.isNotBlank() && phoneOk && address.isNotBlank()
}

@Preview(showBackground = true)
@Composable
private fun UserCreatePreview() {
    MaterialTheme {
        UserCreateScreen()
    }
}
