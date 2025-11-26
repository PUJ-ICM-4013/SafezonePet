package com.example.screens.repository

import android.util.Log
import com.google.firebase.database.*
import com.example.screens.data.PetData

/**
 * Repository para manejar mascotas en Firebase Realtime Database
 */
class PetRepository {

    private val db = FirebaseDatabase.getInstance()
    private val petsRef = db.getReference("pets")
    private var petsListener: ValueEventListener? = null

    /**
     * Guarda o actualiza una mascota en Firebase
     */
    fun savePet(
        pet: PetData,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        petsRef.child(pet.petId).setValue(pet)
            .addOnSuccessListener {
                Log.d("PetRepository", "✅ Mascota guardada: ${pet.name}")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("PetRepository", "❌ Error guardando mascota: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }

    /**
     * Obtiene todas las mascotas de un dueño específico
     */
    fun getPetsByOwner(
        ownerId: String,
        callback: (List<PetData>) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        petsRef.orderByChild("ownerId").equalTo(ownerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pets = snapshot.children.mapNotNull {
                        it.getValue(PetData::class.java)
                    }
                    Log.d("PetRepository", "📋 Mascotas del usuario $ownerId: ${pets.size}")
                    callback(pets)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PetRepository", "❌ Error obteniendo mascotas: ${error.message}")
                    onError(error.message)
                }
            })
    }

    /**
     * Escucha cambios en tiempo real de las mascotas de un dueño
     */
    fun listenToPetsByOwner(
        ownerId: String,
        callback: (List<PetData>) -> Unit,
        onError: (String) -> Unit = {}
    ): ValueEventListener {
        petsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val pets = mutableListOf<PetData>()
                    snapshot.children.forEach { childSnapshot ->
                        val pet = childSnapshot.getValue(PetData::class.java)
                        if (pet != null && pet.ownerId == ownerId) {
                            pets.add(pet)
                        }
                    }
                    Log.d("PetRepository", " Mascotas actualizadas: ${pets.size}")
                    callback(pets)
                } catch (e: Exception) {
                    Log.e("PetRepository", " Error parseando mascotas: ${e.message}")
                    onError(e.message ?: "Error parseando datos")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PetRepository", " Error en listener: ${error.message}")
                onError(error.message)
            }
        }

        petsRef.addValueEventListener(petsListener!!)
        return petsListener!!
    }

    /**
     * Elimina una mascota
     */
    fun deletePet(
        petId: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        petsRef.child(petId).removeValue()
            .addOnSuccessListener {
                Log.d("PetRepository", "🗑️ Mascota eliminada: $petId")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("PetRepository", "Error eliminando mascota: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }

    /**
     * Obtiene una mascota específica
     */
    fun getPet(
        petId: String,
        callback: (PetData?) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        petsRef.child(petId).get()
            .addOnSuccessListener { snapshot ->
                val pet = snapshot.getValue(PetData::class.java)
                callback(pet)
            }
            .addOnFailureListener { exception ->
                Log.e("PetRepository", "Error obteniendo mascota: ${exception.message}")
                onError(exception.message ?: "Error desconocido")
            }
    }

    /**
     * Remueve el listener activo
     */
    fun removeListener() {
        petsListener?.let {
            petsRef.removeEventListener(it)
            petsListener = null
            Log.d("PetRepository", "🧹 Listener removido")
        }
    }
}
