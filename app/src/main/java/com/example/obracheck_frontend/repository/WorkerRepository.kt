package com.example.obracheck_frontend.repository

import com.example.obracheck_frontend.model.domain.Worker
import com.example.obracheck_frontend.model.dto.CreateWorkerRequestDto
import com.example.obracheck_frontend.model.mapper.toDomain
import com.example.obracheck_frontend.network.ApiClient

class WorkerRepository {

    private val api = ApiClient.api

    suspend fun getAllWorkers(): List<Worker> =
        api.listWorkers().map { it.toDomain() }

    suspend fun getByIdWorker(id: Long): Worker =
        api.getWorkerById(id).toDomain()

    suspend fun createWorker(request: CreateWorkerRequestDto): Worker =
        api.createWorker(request).toDomain()

    suspend fun updateWorker(id: Long, request: CreateWorkerRequestDto): Worker =
        api.updateWorker(id, request).toDomain()

    suspend fun deleteWorker(id: Long) {
        api.deleteWorker(id)
    }
}
