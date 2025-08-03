package com.example.obracheck_frontend.repository

import com.example.obracheck_frontend.model.domain.Evidence
import com.example.obracheck_frontend.model.mapper.toDomain
import com.example.obracheck_frontend.network.ApiClient
import okhttp3.MultipartBody

class EvidenceRepository {

    private val api = ApiClient.api

    suspend fun getAllEvidences(): List<Evidence> =
        api.listEvidences().images.map { it.toDomain() }

    suspend fun getByIdEvidence(id: Long): Evidence =
        api.getEvidenceById(id).toDomain()

    suspend fun uploadEvidence(file: MultipartBody.Part, progressId: Long): Evidence =
        api.uploadEvidence(file, progressId).image!!.toDomain()

    suspend fun updateEvidence(id: Long, file: MultipartBody.Part): Evidence =
        api.updateEvidence(id, file).image!!.toDomain()

    suspend fun deleteEvidence(id: Long) {
        api.deleteEvidence(id)
    }
}
