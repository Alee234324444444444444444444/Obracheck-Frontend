package com.example.obracheck_frontend.repository

import com.example.obracheck_frontend.model.domain.User
import com.example.obracheck_frontend.model.dto.CreateUserRequestDto
import com.example.obracheck_frontend.model.mapper.toDomain
import com.example.obracheck_frontend.network.ApiClient

class UserRepository {

    private val api = ApiClient.api

    suspend fun getAllUsers(): List<User> {
        val response = api.listUsers()
        return response.map { it.toDomain() }
    }

    suspend fun getUserById(id: Long): User {
        val dto = api.getUserById(id)
        return dto.toDomain()
    }

    suspend fun createUser(request: CreateUserRequestDto): User {
        val dto = api.createUser(request)
        return dto.toDomain()
    }

    suspend fun updateUser(id: Long, request: CreateUserRequestDto): User {
        val dto = api.updateUser(id, request)
        return dto.toDomain()
    }

    suspend fun deleteUser(id: Long) {
        api.deleteUser(id)
    }
}
