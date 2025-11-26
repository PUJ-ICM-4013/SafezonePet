package com.example.screens.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.screens.data.UserProfile
import com.example.screens.data.UserType
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
// import com.google.firebase.firestore.FirebaseFirestore // Descomentar cuando haya Firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Para activar Firestore:
 * 1. Descomentar las líneas marcadas con // FIRESTORE
 * 2. Comentar las secciones marcadas con // MOCK DATA
 * 3. La estructura de datos ya está preparada para Firestore
 */
class UserRepository(
    private val context: Context? = null, // Para SharedPreferences
    // private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance() // FIRESTORE
) {
    // MOCK DATA - Almacenamiento temporal con SharedPreferences para persistencia
    private val _currentUserProfile = MutableStateFlow<UserProfile?>(null)
    val currentUserProfile: Flow<UserProfile?> = _currentUserProfile.asStateFlow()

    private val sharedPrefs: SharedPreferences? = context?.getSharedPreferences("user_profiles", Context.MODE_PRIVATE)
    private val gson = Gson()
    // FIN MOCK DATA

    suspend fun saveUserProfile(userProfile: UserProfile): Boolean {
        return try {
            // MOCK DATA - Guardado persistente con SharedPreferences
            delay(500) // Simula latencia de red
            sharedPrefs?.edit()?.apply {
                putString(userProfile.userId, gson.toJson(userProfile))
                apply()
            }
            _currentUserProfile.value = userProfile
            true
            // FIN MOCK DATA

            /* FIRESTORE - Descomentar para usar Firestore
            firestore.collection("users")
                .document(userProfile.userId)
                .set(userProfile.toFirestoreMap())
                .await()
            _currentUserProfile.value = userProfile
            true
            */
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            // MOCK DATA - Buscar en SharedPreferences
            delay(300) // Simula latencia de red
            val profileJson = sharedPrefs?.getString(userId, null)
            val profile = if (profileJson != null) {
                gson.fromJson(profileJson, UserProfile::class.java)
            } else null

            if (profile != null) {
                _currentUserProfile.value = profile
            }
            profile
            // FIN MOCK DATA

            /* FIRESTORE - Descomentar para usar Firestore
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val profile = UserProfile.fromFirestoreMap(document.data ?: emptyMap())
                _currentUserProfile.value = profile
                profile
            } else {
                null
            }
            */
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateHomeLocation(
        userId: String,
        location: LatLng,
        address: String
    ): Boolean {
        return try {
            // MOCK DATA - SharedPreferences
            delay(300)
            val currentProfile = getUserProfile(userId)
            if (currentProfile != null) {
                val updatedProfile = currentProfile.copy(
                    homeLatitude = location.latitude,
                    homeLongitude = location.longitude,
                    homeAddress = address
                )
                saveUserProfile(updatedProfile)
                true
            } else {
                false
            }
            // FIN MOCK DATA

            /* FIRESTORE - Descomentar para usar Firestore
            firestore.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "homeLatitude" to location.latitude,
                        "homeLongitude" to location.longitude,
                        "homeAddress" to address
                    )
                )
                .await()

            // Actualizar el perfil local
            val currentProfile = _currentUserProfile.value
            if (currentProfile != null) {
                _currentUserProfile.value = currentProfile.copy(
                    homeLocation = location,
                    homeAddress = address
                )
            }
            true
            */
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getAllWalkers(): List<UserProfile> {
        return try {
            // MOCK DATA - SharedPreferences
            delay(400)
            val allKeys = sharedPrefs?.all?.keys ?: emptySet()
            allKeys.mapNotNull { userId ->
                getUserProfile(userId)
            }.filter { it.userType == UserType.WALKER }
            // FIN MOCK DATA

            /* FIRESTORE - Descomentar para usar Firestore
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("userType", UserType.WALKER.name)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                UserProfile.fromFirestoreMap(document.data ?: emptyMap())
            }
            */
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun updateUserType(userId: String, userType: UserType): Boolean {
        return try {
            // MOCK DATA - SharedPreferences
            delay(300)
            val currentProfile = getUserProfile(userId)
            if (currentProfile != null) {
                val updatedProfile = currentProfile.copy(userType = userType)
                saveUserProfile(updatedProfile)
                true
            } else {
                false
            }
            // FIN MOCK DATA

            /* FIRESTORE - Descomentar para usar Firestore
            firestore.collection("users")
                .document(userId)
                .update("userType", userType.name)
                .await()

            val currentProfile = _currentUserProfile.value
            if (currentProfile != null) {
                _currentUserProfile.value = currentProfile.copy(userType = userType)
            }
            true
            */
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun clearCurrentUserProfile() {
        _currentUserProfile.value = null
    }


}
