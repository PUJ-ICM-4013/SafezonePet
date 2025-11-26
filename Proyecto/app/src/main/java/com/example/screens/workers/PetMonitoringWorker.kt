package com.example.screens.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.screens.notifications.NotificationHelper
import com.example.screens.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay

/**
 * Worker que se ejecuta periódicamente para monitorear el estado de las mascotas
 * y enviar notificaciones cuando sea necesario
 */
class PetMonitoringWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "PetMonitoringWorker"
        const val WORK_NAME = "pet_monitoring_work"
    }

    private val notificationHelper = NotificationHelper(applicationContext)
    private val locationRepository = LocationRepository()

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Iniciando monitoreo de mascotas...")

            // Verificar ubicaciones de mascotas
            checkPetLocations()

            // Verificar batería de rastreadores (simulado)
            checkBatteryLevels()

            Log.d(TAG, "Monitoreo completado exitosamente")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error durante el monitoreo: ${e.message}", e)

            // Reintentar si falla
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    /**
     * Verifica las ubicaciones de las mascotas y envía notificaciones si están fuera de zona
     */
    private suspend fun checkPetLocations() {
        val result = locationRepository.getAllRecentLocations(limit = 10)

        result.fold(
            onSuccess = { locations ->
                // Obtener las ubicaciones más recientes de cada mascota
                val latestLocations = locations.groupBy { it.petId }
                    .mapValues { (_, locs) -> locs.maxByOrNull { it.timestamp } }

                latestLocations.values.forEach { location ->
                    if (location != null && !location.isInSafeZone) {
                        // Enviar notificación de que la mascota está fuera de zona
                        notificationHelper.sendGeofenceExitNotification(
                            petName = location.petName
                        )

                        Log.d(TAG, "${location.petName} está fuera de la zona segura")
                    }
                }
            },
            onFailure = { error ->
                Log.e(TAG, "Error al obtener ubicaciones: ${error.message}")
            }
        )
    }

    /**
     * Simula la verificación de niveles de batería de los rastreadores
     * En producción, esto se conectaría con el dispositivo GPS real
     */
    private suspend fun checkBatteryLevels() {
        // Simulación de verificación de batería
        val pets = listOf("Buddy", "Max", "Charlie")

        pets.forEach { petName ->
            // Simular nivel de batería (en producción vendría del rastreador GPS)
            val batteryLevel = (50..100).random()

            if (batteryLevel < 20) {
                notificationHelper.sendLowBatteryNotification(
                    petName = petName,
                    batteryLevel = batteryLevel
                )

                Log.d(TAG, "Alerta de batería baja enviada para $petName: $batteryLevel%")
            }
        }
    }
}