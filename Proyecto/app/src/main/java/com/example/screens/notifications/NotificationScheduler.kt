package com.example.screens.notifications

import android.content.Context
import androidx.work.*
import com.example.screens.workers.PetMonitoringWorker
import java.util.concurrent.TimeUnit

/**
 * Clase para programar y gestionar las notificaciones periódicas
 */
class NotificationScheduler(private val context: Context) {

    companion object {
        const val TAG = "NotificationScheduler"
        private const val MONITORING_WORK_NAME = "pet_monitoring_periodic"
    }

    /**
     * Inicia el monitoreo periódico de mascotas
     * @param intervalMinutes Intervalo en minutos entre cada verificación
     */
    fun startPeriodicMonitoring(intervalMinutes: Long = 15) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Requiere conexión a internet
            .setRequiresBatteryNotLow(true) // No ejecutar si batería muy baja
            .build()

        val monitoringRequest = PeriodicWorkRequestBuilder<PetMonitoringWorker>(
            intervalMinutes, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag(PetMonitoringWorker.TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MONITORING_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Mantener el trabajo existente si ya está programado
            monitoringRequest
        )

        android.util.Log.d(TAG, "Monitoreo periódico iniciado cada $intervalMinutes minutos")
    }

    /**
     * Detiene el monitoreo periódico
     */
    fun stopPeriodicMonitoring() {
        WorkManager.getInstance(context).cancelUniqueWork(MONITORING_WORK_NAME)
        android.util.Log.d(TAG, "Monitoreo periódico detenido")
    }

    /**
     * Ejecuta una verificación inmediata (one-time)
     */
    fun runImmediateCheck() {
        val immediateRequest = OneTimeWorkRequestBuilder<PetMonitoringWorker>()
            .addTag(PetMonitoringWorker.TAG)
            .build()

        WorkManager.getInstance(context).enqueue(immediateRequest)
        android.util.Log.d(TAG, "Verificación inmediata solicitada")
    }

    /**
     * Verifica si el monitoreo está activo
     */
    fun isMonitoringActive(): Boolean {
        return try {
            val workManager = WorkManager.getInstance(context)
            val workInfos = workManager.getWorkInfosForUniqueWorkLiveData(MONITORING_WORK_NAME).value
            workInfos?.any { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED } ?: false
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error verificando estado del monitoreo: ${e.message}")
            false
        }
    }

    /**
     * Programa una notificación de recordatorio única
     */
    fun scheduleReminder(
        title: String,
        message: String,
        delayMinutes: Long
    ) {
        val data = workDataOf(
            "title" to title,
            "message" to message
        )

        val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .addTag("reminder")
            .build()

        WorkManager.getInstance(context).enqueue(reminderRequest)
        android.util.Log.d(TAG, "Recordatorio programado para dentro de $delayMinutes minutos")
    }
}

/**
 * Worker para enviar notificaciones de recordatorio
 */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "SafeZonePet"
        val message = inputData.getString("message") ?: "Recordatorio"

        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.sendReminderNotification(title, message)

        return Result.success()
    }
}