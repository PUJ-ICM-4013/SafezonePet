package com.example.screens.data

data class Pet(
    val id: String = "",           // id del documento
    val ownerId: String = "",      // uid del dueño
    val name: String = "",
    val breed: String = "",
    val age: Int = 0,
    val vet: String = "",
    val vetAddress: String = "",
    val imageUrl: String = "",     // ideal: URL (Storage). Si no, puedes guardar uri.toString()
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toFirestoreMap(): Map<String, Any?> = mapOf(
        "ownerId" to ownerId,
        "name" to name,
        "breed" to breed,
        "age" to age,
        "vet" to vet,
        "vetAddress" to vetAddress,
        "imageUrl" to imageUrl,
        "createdAt" to createdAt
    )

    companion object {
        fun fromFirestore(id: String, data: Map<String, Any?>): Pet = Pet(
            id = id,
            ownerId = data["ownerId"] as? String ?: "",
            name = data["name"] as? String ?: "",
            breed = data["breed"] as? String ?: "",
            age = (data["age"] as? Number)?.toInt() ?: 0,
            vet = data["vet"] as? String ?: "",
            vetAddress = data["vetAddress"] as? String ?: "",
            imageUrl = data["imageUrl"] as? String ?: "",
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }
}
