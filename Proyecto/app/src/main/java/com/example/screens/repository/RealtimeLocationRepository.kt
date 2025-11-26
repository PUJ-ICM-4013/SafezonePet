package com.example.screens.repository

import com.google.firebase.database.*
import com.example.screens.data.RealtimeLocation

class RealtimeLocationRepository {

    private val db = FirebaseDatabase.getInstance()
    private val ref = db.getReference("pet_locations")

    fun updatePetLocation(location: RealtimeLocation) {
        ref.child(location.petId).setValue(location)
    }

    fun listenToLocations(callback: (List<RealtimeLocation>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull {
                    it.getValue(RealtimeLocation::class.java)
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}