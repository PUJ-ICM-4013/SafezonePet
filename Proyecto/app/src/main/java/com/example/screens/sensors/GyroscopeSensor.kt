package com.example.screens.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class GyroscopeSensor(context: Context, private val onTiltForward: (Boolean) -> Unit) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    // Umbral de giro en rad/s aproximado para “inclinación hacia adelante”
    private val tiltThreshold = 1.0f

    fun start() {
        gyroSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val rotationX = it.values[0] // eje X → inclinación adelante/atrás
            val isTiltForward = rotationX > tiltThreshold
            onTiltForward(isTiltForward)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }
}