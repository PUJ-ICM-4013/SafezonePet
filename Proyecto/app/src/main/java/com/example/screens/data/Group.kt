
package com.example.screens.data

data class Group(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val ownerId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
