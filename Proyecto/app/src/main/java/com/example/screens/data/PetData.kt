package com.example.screens.data

/**
 * Modelo de mascota para Firebase Realtime Database
 */
data class PetData(
    val petId: String = "",              // ID único de la mascota
    val ownerId: String = "",            // ID del dueño (usuario)
    val name: String = "",               // Nombre de la mascota
    val breed: String = "",              // Raza
    val age: String = "",                // Edad en años
    val imageUrl: String = "",           // URL de la imagen (puede ser local por ahora)
    val veterinarian: String = "",       // Nombre del veterinario
    val vetAddress: String = "",         // Dirección del veterinario
    val createdAt: Long = System.currentTimeMillis()  // Timestamp de creación
)
