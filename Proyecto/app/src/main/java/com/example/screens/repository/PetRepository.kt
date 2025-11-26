package com.example.screens.repository

import com.example.screens.data.Pet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class PetRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val pets = firestore.collection("dogs") // o "dogs" si quieres, pero "pets" es más general

    private val _currentPet = MutableStateFlow<Pet?>(null)
    val currentPet: StateFlow<Pet?> = _currentPet.asStateFlow()

    suspend fun createPet(pet: Pet): Pet {
        val docRef = if (pet.id.isBlank()) pets.document() else pets.document(pet.id)
        val petToSave = pet.copy(id = docRef.id)

        docRef.set(petToSave.toFirestoreMap()).await()

        _currentPet.value = petToSave
        return petToSave
    }

    suspend fun getPet(petId: String): Pet? {
        val doc = pets.document(petId).get().await()
        if (!doc.exists()) return null
        return Pet.fromFirestore(doc.id, doc.data ?: emptyMap<String, Any?>()).also {
            _currentPet.value = it
        }
    }

    suspend fun getPetsByOwner(ownerId: String): List<Pet> {
        val snap = pets.whereEqualTo("ownerId", ownerId).get().await()
        return snap.documents.mapNotNull { d ->
            d.data?.let { Pet.fromFirestore(d.id, it) }
        }
    }

    suspend fun deletePet(petId: String): Boolean = try {
        pets.document(petId).delete().await()
        if (_currentPet.value?.id == petId) _currentPet.value = null
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
