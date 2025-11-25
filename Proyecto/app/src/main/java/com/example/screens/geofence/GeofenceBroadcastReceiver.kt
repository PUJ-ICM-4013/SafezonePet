package com.example.screens.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.screens.notifications.NotificationHelper
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "GeofenceReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null) {
            Log.e(TAG, "GeofencingEvent is null")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, "Geofencing error: $errorMessage")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences

        if (triggeringGeofences.isNullOrEmpty()) {
            Log.w(TAG, "No triggering geofences found")
            return
        }

        val notificationHelper = NotificationHelper(context)

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                triggeringGeofences.forEach { geofence ->
                    val petName = geofence.requestId

                    // Enviar notificación usando el NotificationHelper
                    notificationHelper.sendGeofenceExitNotification(petName)

                    // Guardar evento en historial (usando coroutine para operaciones asíncronas)
                    CoroutineScope(Dispatchers.IO).launch {
                        saveLocationEvent(context, petName, isInSafeZone = false)
                    }

                    Log.d(TAG, "$petName salió de la zona segura")
                }
            }

            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                triggeringGeofences.forEach { geofence ->
                    val petName = geofence.requestId

                    // Enviar notificación de regreso
                    notificationHelper.sendGeofenceEnterNotification(petName)

                    // Guardar evento en historial
                    CoroutineScope(Dispatchers.IO).launch {
                        saveLocationEvent(context, petName, isInSafeZone = true)
                    }

                    Log.d(TAG, "$petName entró a la zona segura")
                }
            }

            else -> {
                Log.w(TAG, "Transición de geofence desconocida: $geofenceTransition")
            }
        }
    }

    /**
     * Guarda el evento de ubicación en el historial
     */
    private suspend fun saveLocationEvent(context: Context, petName: String, isInSafeZone: Boolean) {
        try {
            val locationRepository = com.example.screens.repository.LocationRepository()
            val locationHistory = com.example.screens.data.LocationHistory(
                petId = petName.hashCode().toString(),
                petName = petName,
                latitude = 0.0, // Se actualizaría con la ubicación real del GPS
                longitude = 0.0,
                timestamp = System.currentTimeMillis(),
                isInSafeZone = isInSafeZone,
                address = if (isInSafeZone) "Zona Segura" else "Fuera de zona segura"
            )

            locationRepository.saveLocation(locationHistory)
            Log.d(TAG, "Evento de ubicación guardado para $petName")
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar evento de ubicación: ${e.message}", e)
        }
    }
}