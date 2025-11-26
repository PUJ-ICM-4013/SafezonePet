package com.example.screens.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.screens.sensors.GyroscopeSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GyroViewModel(application: Application) : AndroidViewModel(application) {

    private val _isTiltForward = MutableStateFlow(false)
    val isTiltForward = _isTiltForward.asStateFlow()

    private val gyroSensor = GyroscopeSensor(application.applicationContext) { tilt ->
        viewModelScope.launch {
            _isTiltForward.value = tilt
        }
    }

    fun startListening() = gyroSensor.start()
    fun stopListening() = gyroSensor.stop()
}