package com.example.screens.ui
import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.screens.repository.UserRepository
import com.example.screens.data.UserProfile
import kotlinx.coroutines.launch

class UserCreateViewModel(
    private val repo: UserRepository = UserRepository()
) : ViewModel() {
    val loading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val createdId = mutableStateOf<String?>(null)

    fun create(@SuppressLint("RestrictedApi") body: UserProfile) {
        viewModelScope.launch {
            loading.value = true
            error.value = null
            try {
                val res = repo.saveUserProfile(body)
                when (res) {
                    is String -> createdId.value = res
                    is com.google.firebase.firestore.DocumentReference -> createdId.value = res.id
                    else -> createdId.value = res?.toString()
                }
            } catch (e: Exception) {
                error.value = e.message ?: "Error creando usuario"
            } finally {
                loading.value = false
            }
        }
    }
}
