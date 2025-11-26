package com.example.screens.data

data class CommunityReport(
    val id: String = "",
    val status: String = "LOST", // "LOST" | "FOUND"
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",

    val reporterUid: String = "",
    val reporterName: String = "",
    val reporterEmail: String = "",
    val reporterPhone: String = "",

    val createdAt: Long = System.currentTimeMillis()
) {
    fun toFirestoreMap(): Map<String, Any?> = mapOf(
        "status" to status,
        "title" to title,
        "description" to description,
        "imageUrl" to imageUrl,
        "reporterUid" to reporterUid,
        "reporterName" to reporterName,
        "reporterEmail" to reporterEmail,
        "reporterPhone" to reporterPhone,
        "createdAt" to createdAt
    )

    companion object {
        fun fromFirestore(id: String, data: Map<String, Any?>): CommunityReport = CommunityReport(
            id = id,
            status = data["status"] as? String ?: "LOST",
            title = data["title"] as? String ?: "",
            description = data["description"] as? String ?: "",
            imageUrl = data["imageUrl"] as? String ?: "",
            reporterUid = data["reporterUid"] as? String ?: "",
            reporterName = data["reporterName"] as? String ?: "",
            reporterEmail = data["reporterEmail"] as? String ?: "",
            reporterPhone = data["reporterPhone"] as? String ?: "",
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L
        )
    }
}
