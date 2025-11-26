package com.example.screens.repository

import android.util.Log
import com.google.firebase.database.*
import com.example.screens.data.RealtimeLocation

class RealtimeLocationRepository {

    private val db = FirebaseDatabase.getInstance()
    private val ref = db.getReference("pet_locations")
    private var locationListener: ValueEventListener? = null

    /**
     * Actualiza la ubicación de una mascota en Firebase Realtime Database
     */
    fun updatePetLocation(
        location: RealtimeLocation,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        ref.child(location.petId).setValue(location)
            .addOnSuccessListener {
                Log.d("RealtimeLocationRepo", "Ubicación actualizada para petId: ${location.petId}")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("RealtimeLocationRepo", "Error actualizando ubicación: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }

    /**
     * Escucha cambios en tiempo real de las ubicaciones de las mascotas
     * @return ValueEventListener que puede ser usado para remover el listener después
     */
    fun listenToLocations(
        callback: (List<RealtimeLocation>) -> Unit,
        onError: (String) -> Unit = {}
    ): ValueEventListener {
        locationListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val list = snapshot.children.mapNotNull {
                        it.getValue(RealtimeLocation::class.java)
                    }
                    Log.d("RealtimeLocationRepo", "Ubicaciones recibidas: ${list.size}")
                    callback(list)
                } catch (e: Exception) {
                    Log.e("RealtimeLocationRepo", "Error parseando datos: ${e.message}")
                    onError(e.message ?: "Error parseando datos")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RealtimeLocationRepo", "Error en listener: ${error.message}")
                onError(error.message)
            }
        }

        ref.addValueEventListener(locationListener!!)
        return locationListener!!
    }

    /**
     * Remueve el listener activo para evitar memory leaks
     */
    fun removeListener() {
        locationListener?.let {
            ref.removeEventListener(it)
            locationListener = null
            Log.d("RealtimeLocationRepo", "Listener removido")
        }
    }

    /**
     * Obtiene la ubicación actual de una mascota específica (una sola vez)
     */
    fun getPetLocation(
        petId: String,
        callback: (RealtimeLocation?) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        ref.child(petId).get()
            .addOnSuccessListener { snapshot ->
                val location = snapshot.getValue(RealtimeLocation::class.java)
                callback(location)
            }
            .addOnFailureListener { exception ->
                Log.e("RealtimeLocationRepo", "Error obteniendo ubicación: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }
}
