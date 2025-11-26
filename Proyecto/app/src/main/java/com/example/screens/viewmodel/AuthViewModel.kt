package com.example.screens.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.screens.data.UserProfile
import com.example.screens.data.UserType
import com.example.screens.repository.AuthRepository
import com.example.screens.repository.UserRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class AuthViewModel(
    application: Application
): AndroidViewModel(application) {
    private val authRepo: AuthRepository = AuthRepository()
    private val userRepo: UserRepository = UserRepository(application.applicationContext)
    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _currentUserProfile = MutableStateFlow<UserProfile?>(null)
    val currentUserProfile: StateFlow<UserProfile?> = _currentUserProfile.asStateFlow()


    fun login(email: String, password: String, onSuccess: (UserProfile?) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val firebaseUser = authRepo.signIn(email.trim(), password)
                if (firebaseUser != null) {
                    // Cargar perfil del usuario desde el repositorio
                    val userProfile = userRepo.getUserProfile(firebaseUser.uid)
                    _currentUserProfile.value = userProfile
                    onSuccess(userProfile)
                } else {
                    _error.value = "No se pudo obtener información del usuario"
                    onSuccess(null)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
                onSuccess(null)
            } finally {
                _loading.value = false
            }
        }
    }

    fun signup(
        email: String,
        password: String,
        name: String,
        userType: UserType,
        homeLocation: LatLng? = null,
        homeAddress: String = "",
        phoneNumber: String = "",
        onSuccess: (UserProfile?) -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                // Crear usuario en Firebase Auth
                val firebaseUser = authRepo.signUp(email.trim(), password)

                if (firebaseUser != null) {
                    // Crear perfil de usuario
                    val userProfile = UserProfile(
                        userId = firebaseUser.uid,
                        email = email.trim(),
                        name = name,
                        userType = userType,
                        homeLatitude = homeLocation?.latitude,
                        homeLongitude = homeLocation?.longitude,
                        homeAddress = homeAddress,
                        phoneNumber = phoneNumber,
                        role = if (userType == UserType.OWNER) UserType.OWNER else UserType.WALKER,
                        dogs = emptyList(),
                        profileImageUrl = TODO(),
                        createdAt = TODO(),
                        phone = phoneNumber,
                        address = homeAddress
                    )

                    // Guardar perfil en el repositorio
                    val saved = userRepo.saveUserProfile(userProfile)
                    if (saved) {
                        _currentUserProfile.value = userProfile
                        onSuccess(userProfile)
                    } else {
                        _error.value = "Error al guardar el perfil del usuario"
                        onSuccess(null)
                    }
                } else {
                    _error.value = "Error al crear usuario"
                    onSuccess(null)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Signup failed"
                onSuccess(null)
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout() {
        authRepo.signOut()
        userRepo.clearCurrentUserProfile()
        _currentUserProfile.value = null
    }

    fun updateHomeLocation(location: LatLng, address: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = _currentUserProfile.value?.userId
            if (userId != null) {
                val success = userRepo.updateHomeLocation(userId, location, address)
                if (success) {
                    // Actualizar el perfil local
                    _currentUserProfile.value = _currentUserProfile.value?.copy(
                        homeLatitude = location.latitude,
                        homeLongitude = location.longitude,
                        homeAddress = address
                    )
                }
                onComplete(success)
            } else {
                onComplete(false)
            }
        }
    }

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val profile = userRepo.getUserProfile(userId)
                _currentUserProfile.value = profile
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar perfil"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}