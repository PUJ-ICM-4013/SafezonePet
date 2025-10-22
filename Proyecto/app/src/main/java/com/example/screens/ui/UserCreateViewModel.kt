package com.example.screens.ui
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.screens.Data.UserRepository
import com.example.screens.Data.remote.model.CreateUserRequest
import kotlinx.coroutines.launch

class UserCreateViewModel(
    private val repo: UserRepository = UserRepository()
) : ViewModel() {
    val loading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val createdId = mutableStateOf<String?>(null)

    fun create(body: CreateUserRequest) {
        viewModelScope.launch {
            loading.value = true
            error.value = null
            try {
                val res = repo.createUser(body)
                createdId.value = res.id
            } catch (e: Exception) {
                error.value = e.message ?: "Error creando usuario"
            } finally {
                loading.value = false
            }
        }
    }
}
