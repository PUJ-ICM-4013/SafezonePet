package com.example.screens.Data

import com.google.android.gms.maps.model.LatLng

data class PetLocation(
    val pet: Pet,
    val location: LatLng,
    val isInSafeZone: Boolean = true,
    val lastUpdate: String = "Ahora"
)
