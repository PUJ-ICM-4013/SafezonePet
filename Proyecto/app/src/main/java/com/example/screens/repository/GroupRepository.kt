package com.example.screens.repository

import com.example.screens.data.Group
import com.example.screens.data.GroupMember
import com.example.screens.data.Pet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GroupRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val groups = firestore.collection("groups")
    private val users = firestore.collection("users")
    private val pets = firestore.collection("pets")

    suspend fun createGroup(ownerId: String, name: String, description: String): Group {
        val doc = groups.document()
        val group = Group(
            id = doc.id,
            name = name.trim(),
            description = description.trim(),
            ownerId = ownerId
        )
        doc.set(group.toMap()).await()

        // Owner como miembro (role = OWNER)
        doc.collection("members").document(ownerId)
            .set(
                GroupMember(uid = ownerId, role = "OWNER").toMap()
            ).await()

        return group
    }

    suspend fun getGroupMembers(groupId: String): List<GroupMember> {
        val snap = groups.document(groupId).collection("members").get().await()
        return snap.documents.mapNotNull { d ->
            val data = d.data ?: return@mapNotNull null
            GroupMember(
                uid = data["uid"] as? String ?: d.id,
                name = data["name"] as? String ?: "",
                email = data["email"] as? String ?: "",
                role = data["role"] as? String ?: "MEMBER",
                joinedAt = (data["joinedAt"] as? Number)?.toLong() ?: 0L
            )
        }
    }

    suspend fun addMemberByEmail(groupId: String, email: String): Boolean {
        return try {
            val userSnap = users.whereEqualTo("email", email.trim().lowercase()).limit(1).get().await()
            val userDoc = userSnap.documents.firstOrNull() ?: return false
            val uid = userDoc.id
            val name = userDoc.getString("name").orEmpty()
            val em = userDoc.getString("email").orEmpty()

            groups.document(groupId).collection("members").document(uid)
                .set(
                    GroupMember(uid = uid, name = name, email = em, role = "MEMBER").toMap()
                ).await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun removeMember(groupId: String, uid: String): Boolean = try {
        groups.document(groupId).collection("members").document(uid).delete().await()
        true
    } catch (e: Exception) {
        e.printStackTrace(); false
    }

    suspend fun getGroupPets(groupId: String): List<Pet> {
        val members = getGroupMembers(groupId).map { it.uid }.distinct()
        if (members.isEmpty()) return emptyList()

        // Firestore whereIn tiene límite (10). Hacemos chunks.
        val chunks = members.chunked(10)
        val result = mutableListOf<Pet>()

        for (chunk in chunks) {
            val snap = pets.whereIn("ownerId", chunk).get().await()
            snap.documents.forEach { d ->
                val data = d.data ?: return@forEach
                result += Pet.fromFirestore(d.id, data) // crea este helper en Pet (como hicimos antes)
            }
        }
        return result
    }

    suspend fun getMyGroups(uid: String): List<Group> {
        // Opción simple: collectionGroup query sobre members
        val memberHits = firestore.collectionGroup("members")
            .whereEqualTo("uid", uid)
            .get().await()

        val groupIds = memberHits.documents.mapNotNull { it.reference.parent.parent?.id }.distinct()
        if (groupIds.isEmpty()) return emptyList()

        // Traer documentos de groups
        val groupsDocs = groupIds.map { groups.document(it).get() }.let { tasks ->
            tasks.map { it.await() }
        }

        return groupsDocs.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            Group(
                id = doc.id,
                name = data["name"] as? String ?: "",
                description = data["description"] as? String ?: "",
                ownerId = data["ownerId"] as? String ?: "",
                createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L
            )
        }
    }

    private fun Group.toMap() = mapOf(
        "name" to name,
        "description" to description,
        "ownerId" to ownerId,
        "createdAt" to createdAt
    )

    private fun GroupMember.toMap() = mapOf(
        "uid" to uid,
        "name" to name,
        "email" to email,
        "role" to role,
        "joinedAt" to joinedAt
    )
}
