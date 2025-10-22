package com.example.screens.Data.remote


import com.example.screens.Data.remote.model.CreateUserRequest
import com.example.screens.Data.remote.model.CreateUserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    // Cambia "users" por tu ruta real: "api/users", etc.
    @POST("/api/user/new")
    suspend fun createUser(@Body body: CreateUserRequest): CreateUserResponse
}
