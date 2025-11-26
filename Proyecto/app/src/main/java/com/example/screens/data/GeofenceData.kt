package com.example.screens.data

data class GeofenceData(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val petId: String,
    val petName: String
)
