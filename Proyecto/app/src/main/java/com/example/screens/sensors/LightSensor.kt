package com.example.screens.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/**
 * Clase que maneja el sensor de luminosidad del dispositivo
 */
class LightSensor(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private var listener: SensorEventListener? = null
    private var onLightChanged: ((Float) -> Unit)? = null

    companion object {
        private const val TAG = "LightSensor"
        const val DARK_MODE_THRESHOLD = 20f
    }

    fun isAvailable(): Boolean = lightSensor != null

    fun startListening(onLightChanged: (Float) -> Unit) {
        if (lightSensor == null) {
            Log.w(TAG, "Light sensor not available on this device")
            return
        }

        this.onLightChanged = onLightChanged

        listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val lux = event.values[0]
                onLightChanged(lux)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }
        }

        sensorManager.registerListener(
            listener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        Log.d(TAG, "Light sensor listener registered")
    }

    fun stopListening() {
        listener?.let {
            sensorManager.unregisterListener(it)
            Log.d(TAG, "Light sensor listener unregistered")
        }
        listener = null
        onLightChanged = null
    }

    fun isDarkMode(lux: Float): Boolean = lux < DARK_MODE_THRESHOLD
}

@Composable
fun rememberLightSensor(): Boolean {
    val context = LocalContext.current
    var isDarkMode by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val lightSensor = LightSensor(context)

        if (lightSensor.isAvailable()) {
            lightSensor.startListening { lux ->
                isDarkMode = lightSensor.isDarkMode(lux)
            }
        } else {
            Log.w("LightSensor", "Light sensor not available on this device")
        }

        onDispose {
            lightSensor.stopListening()
        }
    }

    return isDarkMode
}
