package com.example.screens.location

import android.location.Location
import com.example.screens.Data.LocationHistory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class LocationRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val locationsCollection = firestore.collection("location_history")

    suspend fun saveLocation(location: LocationHistory): Result<String> {
        return try {
            val docRef = locationsCollection.add(location).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLocationHistory(petId: String, limit: Int = 50): Result<List<LocationHistory>> {
        return try {
            val snapshot = locationsCollection
                .whereEqualTo("petId", petId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val locations = snapshot.documents.mapNotNull { doc ->
                doc.toObject(LocationHistory::class.java)?.copy(id = doc.id)
            }
            Result.success(locations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllRecentLocations(limit: Int = 100): Result<List<LocationHistory>> {
        return try {
            val snapshot = locationsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val locations = snapshot.documents.mapNotNull { doc ->
                doc.toObject(LocationHistory::class.java)?.copy(id = doc.id)
            }
            Result.success(locations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOutOfZoneLocations(): Result<List<LocationHistory>> {
        return try {
            val snapshot = locationsCollection
                .whereEqualTo("isInSafeZone", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            val locations = snapshot.documents.mapNotNull { doc ->
                doc.toObject(LocationHistory::class.java)?.copy(id = doc.id)
            }
            Result.success(locations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isInSafeZone(petLocation: LatLng, centerLocation: LatLng, radiusMeters: Float): Boolean {
        val results = FloatArray(1)
        Location.distanceBetween(
            petLocation.latitude,
            petLocation.longitude,
            centerLocation.latitude,
            centerLocation.longitude,
            results
        )
        return results[0] <= radiusMeters
    }
}