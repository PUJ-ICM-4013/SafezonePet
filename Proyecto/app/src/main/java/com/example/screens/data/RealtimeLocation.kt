package com.example.screens.data

data class RealtimeLocation(
    val petId: String = "",
    val petName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val isInSafeZone: Boolean = true
)