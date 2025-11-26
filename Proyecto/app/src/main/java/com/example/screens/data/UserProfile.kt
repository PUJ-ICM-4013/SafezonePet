package com.example.screens.data

import com.google.android.gms.maps.model.LatLng

data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val userType: UserType = UserType.OWNER,
    val homeLatitude: Double? = null,
    val homeLongitude: Double? = null,
    val homeAddress: String = "", // Dirección legible
    val phoneNumber: String = "",
    val profileImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val phone: String = "",
    val address: String = "",
    val dogs: List<kotlinx.serialization.json.JsonObject> = emptyList(),
    val role: UserType
) {

    @delegate:Transient
    val homeLocation: LatLng? by lazy {
        if (homeLatitude != null && homeLongitude != null) {
            LatLng(homeLatitude, homeLongitude)
        } else null
    }
    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "email" to email,
            "name" to name,
            "userType" to userType.name,
            "homeLatitude" to homeLatitude,
            "homeLongitude" to homeLongitude,
            "homeAddress" to homeAddress,
            "phoneNumber" to phoneNumber,
            "profileImageUrl" to profileImageUrl,
            "createdAt" to createdAt
        )
    }

    companion object {
        fun fromFirestoreMap(map: Map<String, Any?>): UserProfile {
            return UserProfile(
                userId = map["userId"] as? String ?: "",
                email = map["email"] as? String ?: "",
                name = map["name"] as? String ?: "",
                userType = UserType.valueOf(map["userType"] as? String ?: "OWNER"),
                homeLatitude = map["homeLatitude"] as? Double,
                homeLongitude = map["homeLongitude"] as? Double,
                homeAddress = map["homeAddress"] as? String ?: "",
                phoneNumber = map["phoneNumber"] as? String ?: "",
                profileImageUrl = map["profileImageUrl"] as? String ?: "",
                createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis(),
                phone = map["phone"] as? String ?: "",
                address = map["address"] as? String ?: "",
                dogs = map["dogs"] as? List<kotlinx.serialization.json.JsonObject> ?: emptyList(),
                role = UserType.valueOf(map["userType"] as? String ?: "OWNER")
            )
        }
    }
}

enum class UserType {
    OWNER,      // Dueño
    WALKER;     // Paseador

    fun getDisplayName(): String {
        return when (this) {
            OWNER -> "Dueño de Mascota"
            WALKER -> "Paseador/Entrenador"
        }
    }

    fun getDescription(): String {
        return when (this) {
            OWNER -> "Monitorea tus mascotas desde casa con zona segura fija"
            WALKER -> "Zona segura móvil que sigue tu ubicación mientras paseas"
        }
    }
}
