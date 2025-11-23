package com.example.screens.data

data class OsrmResponse(
    val code: String,
    val routes: List<OsrmRoute>?,
    val waypoints: List<OsrmWaypoint>?,
    val message: String? = null
)

data class OsrmRoute(
    val geometry: String,  // Polyline codificada
    val legs: List<OsrmLeg>,
    val distance: Double,  // Distancia en metros
    val duration: Double
)

data class OsrmLeg(
    val steps: List<OsrmStep>,
    val distance: Double,
    val duration: Double,
    val summary: String? = null
)

data class OsrmStep(
    val geometry: String,
    val maneuver: OsrmManeuver,
    val mode: String,
    val distance: Double,
    val duration: Double,
    val name: String? = null,
    val instruction: String? = null
)

data class OsrmManeuver(
    val bearing_after: Int? = null,
    val bearing_before: Int? = null,
    val location: List<Double>,  // [lon, lat]
    val type: String,
    val modifier: String? = null,
    val instruction: String? = null
)

data class OsrmWaypoint(
    val hint: String? = null,
    val distance: Double,
    val name: String,
    val location: List<Double>  // [lon, lat]
)
