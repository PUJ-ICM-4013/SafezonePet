package com.example.screens.repository

import android.util.Log
import com.example.screens.data.OsrmResponse
import com.example.screens.data.RouteInfo
import com.example.screens.network.OsrmApiService
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil

class RouteRepository {
    private val osrmService = OsrmApiService.create()

    suspend fun getRoute(origin: LatLng, destination: LatLng): Result<RouteInfo> {
        return try {
            // OSRM usa formato lon,lat (no lat,lon como Google Maps)
            val coordinates = "${origin.longitude},${origin.latitude};${destination.longitude},${destination.latitude}"

            Log.d("RouteRepository", "Solicitando ruta desde $coordinates")

            val response = osrmService.getRoute(
                coordinates = coordinates,
                overview = "full",
                geometries = "polyline",
                steps = true
            )

            if (response.code == "Ok" && !response.routes.isNullOrEmpty()) {
                val route = response.routes.first()

                // Decodificar la polyline
                val polylinePoints = PolyUtil.decode(route.geometry)

                // Calcular distancia y duracion
                val distanceKm = route.distance / 1000.0
                val durationMin = (route.duration / 60.0).toInt()

                val routeInfo = RouteInfo(
                    polylinePoints = polylinePoints,
                    distance = String.format("%.2f km", distanceKm),
                    duration = "$durationMin min"
                )

                Log.d("RouteRepository", "Ruta obtenida: ${routeInfo.distance}, ${routeInfo.duration}")
                Result.success(routeInfo)
            } else {
                val errorMsg = response.message ?: "No se pudo calcular la ruta"
                Log.e("RouteRepository", "Error OSRM: $errorMsg (code: ${response.code})")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("RouteRepository", "Error al obtener ruta: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getRouteAlternatives(origin: LatLng, destination: LatLng, numAlternatives: Int = 3): Result<List<RouteInfo>> {
        return try {
            val coordinates = "${origin.longitude},${origin.latitude};${destination.longitude},${destination.latitude}"

            val response = osrmService.getRoute(
                coordinates = coordinates,
                overview = "full",
                geometries = "polyline",
                steps = true
            )

            if (response.code == "Ok" && !response.routes.isNullOrEmpty()) {
                val routes = response.routes.take(numAlternatives).map { route ->
                    val polylinePoints = PolyUtil.decode(route.geometry)
                    val distanceKm = route.distance / 1000.0
                    val durationMin = (route.duration / 60.0).toInt()

                    RouteInfo(
                        polylinePoints = polylinePoints,
                        distance = String.format("%.2f km", distanceKm),
                        duration = "$durationMin min"
                    )
                }

                Result.success(routes)
            } else {
                val errorMsg = response.message ?: "No se pudo calcular la ruta"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
