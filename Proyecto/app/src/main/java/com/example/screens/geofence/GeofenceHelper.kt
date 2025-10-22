package com.example.screens.geofence

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.screens.Data.GeofenceData
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceHelper(private val context: Context) {

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    companion object {
        private const val TAG = "GeofenceHelper"
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    fun addGeofence(geofenceData: GeofenceData, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val geofence = Geofence.Builder()
            .setRequestId(geofenceData.petName)
            .setCircularRegion(
                geofenceData.latitude,
                geofenceData.longitude,
                geofenceData.radius
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onFailure("Permisos de ubicación no otorgados")
            return
        }

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d(TAG, "Geofence agregado para ${geofenceData.petName}")
                onSuccess()
            }
            addOnFailureListener { exception ->
                Log.e(TAG, "Error al agregar geofence: ${exception.message}")
                onFailure(exception.message ?: "Error desconocido")
            }
        }
    }

    fun removeGeofence(petName: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        geofencingClient.removeGeofences(listOf(petName)).run {
            addOnSuccessListener {
                Log.d(TAG, "Geofence removido para $petName")
                onSuccess()
            }
            addOnFailureListener { exception ->
                Log.e(TAG, "Error al remover geofence: ${exception.message}")
                onFailure(exception.message ?: "Error desconocido")
            }
        }
    }

    fun removeAllGeofences(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d(TAG, "Todos los geofences removidos")
                onSuccess()
            }
            addOnFailureListener { exception ->
                Log.e(TAG, "Error al remover geofences: ${exception.message}")
                onFailure(exception.message ?: "Error desconocido")
            }
        }
    }
}