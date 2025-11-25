package com.example.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.screens.R
import com.example.screens.repository.RealtimeLocationRepository
import com.example.screens.data.RealtimeLocation
import com.example.screens.data.Pet
import com.example.screens.data.PetLocation
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RealtimeLocationViewModel : ViewModel() {

    private val repo = RealtimeLocationRepository()


    private val _firebaseLocations = MutableStateFlow<List<RealtimeLocation>>(emptyList())
    val firebaseLocations = _firebaseLocations.asStateFlow()


    private val _petLocations = MutableStateFlow<List<PetLocation>>(emptyList())
    val petLocations = _petLocations.asStateFlow()

    // Mascotas registradas en el sistema (mock o cargadas desde Firestore)
    private val registeredPets = listOf(
        Pet("Buddy", R.drawable.buddy),
        Pet("Max", R.drawable.max),
        Pet("Charlie", R.drawable.charlie)
    )

    init {
        viewModelScope.launch {
            repo.listenToLocations { list ->
                _firebaseLocations.value = list
                transformToPetLocations(list)
            }
        }
    }

    /* Convierte RealtimeLocation -> PetLocation para la UI*/
    private fun transformToPetLocations(list: List<RealtimeLocation>) {
        val mapped = list.mapNotNull { remote ->

            val pet = registeredPets.find { it.name == remote.petId } ?: return@mapNotNull null

            PetLocation(
                pet = pet,
                location = LatLng(remote.latitude, remote.longitude),
                isInSafeZone = true,
                lastUpdate = formatLastUpdate(remote.timestamp)
            )
        }

        _petLocations.value = mapped
    }

    /* Envía la ubicación de una mascota al servidor*/
    fun sendPetLocation(
        petId: String,
        lat: Double,
        lon: Double
    ) {
        repo.updatePetLocation(
            RealtimeLocation(
                petId = petId,
                latitude = lat,
                longitude = lon,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    /*Convierte timestamp a texto amigable (Ahora, hace 5 min, etc)*/

    private fun formatLastUpdate(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val minutes = diff / 60000

        return when {
            minutes < 1 -> "Ahora"
            minutes == 1L -> "Hace 1 min"
            minutes < 60 -> "Hace $minutes min"
            else -> "${minutes / 60} h"
        }
    }
}
