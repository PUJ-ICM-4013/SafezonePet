package com.example.screens.Data

import com.example.screens.Data.remote.ApiClient
import com.example.screens.Data.remote.model.CreateUserRequest
import com.example.screens.Data.remote.model.CreateUserResponse

class UserRepository {
    private val api = ApiClient.userApi
    suspend fun createUser(body: CreateUserRequest): CreateUserResponse = api.createUser(body)
}
