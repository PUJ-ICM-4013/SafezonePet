package com.example.screens.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.screens.sensors.AccelerometerSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.sqrt

/**
 * ViewModel que escucha los valores del acelerómetro y detecta movimiento.
 */
class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val accelerometerSensor = AccelerometerSensor(application)

    private val _isMoving = MutableStateFlow(false)
    val isMoving: StateFlow<Boolean> = _isMoving

    init {
        // Escucha constante de los valores del acelerómetro
        viewModelScope.launch {
            accelerometerSensor.sensorValues.collectLatest { values ->
                val magnitude = sqrt(
                    (values[0] * values[0] +
                            values[1] * values[1] +
                            values[2] * values[2]).toDouble()
                )

                // Si la magnitud supera un umbral, el dispositivo se considera en movimiento
                _isMoving.value = magnitude > 11
            }
        }
    }

    fun startListening() = accelerometerSensor.startListening()
    fun stopListening() = accelerometerSensor.stopListening()
}