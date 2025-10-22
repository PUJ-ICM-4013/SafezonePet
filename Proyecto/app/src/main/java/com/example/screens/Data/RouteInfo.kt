package com.example.screens.Data

import com.google.android.gms.maps.model.LatLng

data class RouteInfo(
    val distance: String = "",
    val duration: String = "",
    val polylinePoints: List<LatLng> = emptyList(),
    val steps: List<String> = emptyList()
)


