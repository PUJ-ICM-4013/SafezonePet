package com.example.screens.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.screens.Data.AuthRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
class AuthViewModel(private val repo: AuthRepository = AuthRepository()): ViewModel(){
    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val error = mutableStateOf<String?>(null)
    val _error = mutableStateOf<String?>( null)


    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repo.signIn(email.trim(), password)
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
            } finally {
                _loading.value = false
            }
        }
    }

    fun signup(email: String, password: String, onSuccess: (String) -> Unit) {
        error.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid.isNullOrBlank()) {
                        error.value = "No se pudo obtener el UID del usuario."
                        return@addOnCompleteListener
                    }
                    // (Opcional) enviar verificación por correo:
                    // auth.currentUser?.sendEmailVerification()

                    onSuccess(uid)
                } else {
                    val ex = task.exception
                    error.value = when (ex) {
                        is FirebaseAuthWeakPasswordException ->
                            "La contraseña es muy débil (mínimo 6 caracteres)."
                        is FirebaseAuthInvalidCredentialsException ->
                            "El correo no es válido."
                        is FirebaseAuthUserCollisionException ->
                            "Ya existe una cuenta registrada con este correo."
                        else -> ex?.localizedMessage ?: "Error creando la cuenta."
                    }
                }
            }
    }

}