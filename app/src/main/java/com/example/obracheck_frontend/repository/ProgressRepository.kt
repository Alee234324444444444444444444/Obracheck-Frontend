package com.example.obracheck_frontend.repository

import com.example.obracheck_frontend.model.domain.Progress
import com.example.obracheck_frontend.model.dto.CreateProgressRequestDto
import com.example.obracheck_frontend.model.mapper.toDomain
import com.example.obracheck_frontend.network.ApiClient

class ProgressRepository {

    private val api = ApiClient.api

    suspend fun getAllProgress(): List<Progress> =
        api.listProgresses().map { it.toDomain() }

    suspend fun getByIdProgress(id: Long): Progress =
        api.getProgressById(id).toDomain()

    suspend fun createProgress(request: CreateProgressRequestDto): Progress =
        api.createProgress(request).toDomain()

    suspend fun updateProgress(id: Long, request: CreateProgressRequestDto): Progress =
        api.updateProgress(id, request).toDomain()

    suspend fun deleteProgress(id: Long) {
        api.deleteProgress(id)
    }
}
