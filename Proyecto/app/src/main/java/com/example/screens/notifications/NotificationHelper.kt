package com.example.screens.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.screens.MainActivity
import com.example.screens.R
import com.example.screens.data.NotificationType

/**
 * Helper class para manejar todas las notificaciones de la app
 */
class NotificationHelper(private val context: Context) {

    companion object {
        // Canales de notificación
        const val CHANNEL_GEOFENCE = "geofence_alerts"
        const val CHANNEL_PET_UPDATES = "pet_updates"
        const val CHANNEL_GENERAL = "general_notifications"
        const val CHANNEL_BATTERY = "battery_alerts"

        // IDs base para notificaciones
        private const val NOTIFICATION_ID_GEOFENCE = 1000
        private const val NOTIFICATION_ID_PET = 2000
        private const val NOTIFICATION_ID_GENERAL = 3000
        private const val NOTIFICATION_ID_BATTERY = 4000
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannels()
    }

    /**
     * Verifica si se tienen permisos de notificación
     */
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            // En versiones anteriores a Android 13, el permiso se concede automáticamente
            true
        }
    }

    /**
     * Crea todos los canales de notificación necesarios
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_GEOFENCE,
                    "Alertas de Zona Segura",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notificaciones cuando las mascotas salen de la zona segura"
                    enableVibration(true)
                    enableLights(true)
                },

                NotificationChannel(
                    CHANNEL_PET_UPDATES,
                    "Actualizaciones de Mascotas",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Actualizaciones generales sobre tus mascotas"
                },

                NotificationChannel(
                    CHANNEL_GENERAL,
                    "Notificaciones Generales",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notificaciones generales de la aplicación"
                },

                NotificationChannel(
                    CHANNEL_BATTERY,
                    "Alertas de Batería",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alertas de batería baja en rastreadores"
                    enableVibration(true)
                }
            )

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channels.forEach { manager.createNotificationChannel(it) }
        }
    }

    /**
     * Notificación cuando una mascota sale de la zona segura
     */
    fun sendGeofenceExitNotification(petName: String, notificationId: Int? = null) {
        // Verificar permiso de notificaciones (Android 13+)
        if (!hasNotificationPermission()) {
            android.util.Log.w("NotificationHelper", "No se tienen permisos de notificación")
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "map")
            putExtra("pet_name", petName)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_GEOFENCE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚠️ $petName salió de la zona segura")
            .setContentText("$petName ha abandonado el área segura. Toca para ver su ubicación.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("¡Atención! $petName ha salido de la zona segura. Por favor revisa su ubicación inmediatamente y toma las medidas necesarias."))
            .build()

        try {
            notificationManager.notify(
                notificationId ?: (NOTIFICATION_ID_GEOFENCE + petName.hashCode()),
                notification
            )
        } catch (e: SecurityException) {
            android.util.Log.e("NotificationHelper", "Error al enviar notificación: ${e.message}")
        }
    }

    /**
     * Notificación cuando una mascota regresa a la zona segura
     */
    fun sendGeofenceEnterNotification(petName: String, notificationId: Int? = null) {
        if (!hasNotificationPermission()) {
            android.util.Log.w("NotificationHelper", "No se tienen permisos de notificación")
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "map")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_GEOFENCE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🟢 $petName está de vuelta")
            .setContentText("$petName ha regresado a la zona segura.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(
                notificationId ?: (NOTIFICATION_ID_GEOFENCE + petName.hashCode() + 1),
                notification
            )
        } catch (e: SecurityException) {
            android.util.Log.e("NotificationHelper", "Error al enviar notificación: ${e.message}")
        }
    }

    /**
     * Notificación de batería baja
     */
    fun sendLowBatteryNotification(petName: String, batteryLevel: Int) {
        if (!hasNotificationPermission()) {
            android.util.Log.w("NotificationHelper", "No se tienen permisos de notificación")
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "settings")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BATTERY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🔋 Batería Baja - $petName")
            .setContentText("El rastreador de $petName tiene $batteryLevel% de batería.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("El rastreador GPS de $petName está bajo en batería ($batteryLevel%). Por favor recárgalo pronto para mantener el seguimiento activo."))
            .build()

        try {
            notificationManager.notify(
                NOTIFICATION_ID_BATTERY + petName.hashCode(),
                notification
            )
        } catch (e: SecurityException) {
            android.util.Log.e("NotificationHelper", "Error al enviar notificación: ${e.message}")
        }
    }

    /**
     * Notificación de mascota perdida reportada en la comunidad
     */
    fun sendLostPetReportNotification(petName: String, location: String) {
        if (!hasNotificationPermission()) {
            android.util.Log.w("NotificationHelper", "No se tienen permisos de notificación")
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "community")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PET_UPDATES)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🐕 Mascota Perdida Reportada")
            .setContentText("$petName fue reportado como perdido cerca de $location")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(
                NOTIFICATION_ID_PET + petName.hashCode(),
                notification
            )
        } catch (e: SecurityException) {
            android.util.Log.e("NotificationHelper", "Error al enviar notificación: ${e.message}")
        }
    }

    /**
     * Notificación genérica personalizable
     */
    fun sendCustomNotification(
        title: String,
        message: String,
        type: NotificationType = NotificationType.GENERAL,
        actionRoute: String? = null,
        notificationId: Int? = null
    ) {
        if (!hasNotificationPermission()) {
            android.util.Log.w("NotificationHelper", "No se tienen permisos de notificación")
            return
        }

        val channelId = when (type) {
            NotificationType.ALERT -> CHANNEL_GEOFENCE
            NotificationType.PET -> CHANNEL_PET_UPDATES
            NotificationType.GENERAL -> CHANNEL_GENERAL
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            actionRoute?.let { putExtra("navigate_to", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val priority = when (type) {
            NotificationType.ALERT -> NotificationCompat.PRIORITY_HIGH
            else -> NotificationCompat.PRIORITY_DEFAULT
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        try {
            notificationManager.notify(
                notificationId ?: (NOTIFICATION_ID_GENERAL + title.hashCode()),
                notification
            )
        } catch (e: SecurityException) {
            android.util.Log.e("NotificationHelper", "Error al enviar notificación: ${e.message}")
        }
    }

    /**
     * Notificación programada para recordatorios
     */
    fun sendReminderNotification(title: String, message: String) {
        sendCustomNotification(
            title = title,
            message = message,
            type = NotificationType.GENERAL,
            actionRoute = "settings"
        )
    }

    /**
     * Cancela una notificación específica
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    /**
     * Cancela todas las notificaciones
     */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}