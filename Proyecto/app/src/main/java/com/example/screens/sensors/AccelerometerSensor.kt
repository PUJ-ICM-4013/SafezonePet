package com.example.screens.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Clase encargada de obtener lecturas del acelerómetro.
 * Expone los valores [x, y, z] en un StateFlow observable.
 */

class AccelerometerSensor(context: Context) {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _sensorValues = MutableStateFlow(FloatArray(3))
    val sensorValues: StateFlow<FloatArray> = _sensorValues

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            _sensorValues.value = event.values.clone() // copia segura
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    /** Inicia la escucha del sensor. */
    fun startListening() {
        accelerometer?.also { sensor ->
            sensorManager.registerListener(
                listener,
                sensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    /** Detiene la escucha del sensor. */
    fun stopListening() {
        sensorManager.unregisterListener(listener)
    }
}