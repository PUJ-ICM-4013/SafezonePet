package com.example.screens.ui
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.screens.Data.remote.model.CreateUserRequest
import kotlinx.serialization.json.JsonObject
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserCreateScreenWithNavigation(
    uid: String,
    email: String,
    onBackClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSuccess: (createdId: String) -> Unit,
    vm: UserCreateViewModel = viewModel()
) {
    val loading by vm.loading
    val error by vm.error
    val createdId by vm.createdId

    // UI reusada (tu pantalla anterior)
    UserCreateScreen(
        uid = uid,
        emailPrefill = email,
        onBack = onBackClick,
        onSignInClick = onSignInClick,
        onSubmit = { extra ->
            // Construimos el cuerpo EXACTO que pide el backend
            val body = CreateUserRequest(
                firebaseUid = uid,
                email = extra.email ?: email,
                name = extra.name,
                phone = extra.phone,
                address = extra.address,
                role = "owner",
                dogs = emptyList<JsonObject>(),
                passwordHash = null // opcional
            )
            vm.create(body)
        }
    )

    // Loading / error mínimos (opcional: muéstralos dentro de la pantalla si prefieres)
    if (loading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    error?.let { msg ->
        //SnackbarHost(hostState = remember { SnackbarHostState() }).showSnackbar // opcional
        Text(
            text = msg,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
    }
    createdId?.let { id ->
        LaunchedEffect(id) { onSuccess(id) }
    }
}
