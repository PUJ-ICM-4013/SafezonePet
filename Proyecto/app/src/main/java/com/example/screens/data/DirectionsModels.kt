package com.example.screens.data

data class DirectionsResponse(
    val routes: List<Route>,
    val status: String,
    val error_message: String? = null
)

data class Route(
    val legs: List<Leg>,
    val overview_polyline: OverviewPolyline,
    val summary: String
)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val end_address: String,
    val start_address: String,
    val steps: List<Step>
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class Step(
    val distance: Distance,
    val duration: Duration,
    val html_instructions: String,
    val polyline: Polyline,
    val travel_mode: String
)

data class Polyline(
    val points: String
)

data class OverviewPolyline(
    val points: String
)