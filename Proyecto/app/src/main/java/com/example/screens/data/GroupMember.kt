package com.example.screens.data

data class GroupMember(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "MEMBER",
    val joinedAt: Long = System.currentTimeMillis()
)
