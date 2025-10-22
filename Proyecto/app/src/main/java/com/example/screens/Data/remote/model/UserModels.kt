package com.example.screens.Data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class CreateUserRequest(
    @SerialName("firebase_uid") val firebaseUid: String,
    val email: String,
    val name: String,
    val phone: String,
    val address: String,
    val role: String = "owner",
    val dogs: List<JsonObject> = emptyList(),
    @SerialName("password_hash") val passwordHash: String? = null
)

@Serializable
data class CreateUserResponse(
    // por si tu backend devuelve "uid" o "_id"
    @SerialName("uid") val uid: String? = null,
    @SerialName("_id") val mongoId: String? = null
) {
    val id: String get() = uid ?: mongoId ?: ""
}
