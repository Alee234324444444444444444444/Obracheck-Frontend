package com.example.obracheck_frontend.repository

import com.example.obracheck_frontend.model.domain.Evidence
import com.example.obracheck_frontend.model.mapper.toDomain
import com.example.obracheck_frontend.network.ApiClient
import com.example.obracheck_frontend.network.MultipartUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.io.File

class EvidenceRepository {

    private val api = ApiClient.api

    suspend fun getAllEvidences(): List<Evidence> =
        api.listEvidences().images.map { it.toDomain() }

    suspend fun getByIdEvidence(id: Long): Evidence =
        api.getEvidenceById(id).toDomain()

    suspend fun downloadEvidence(id: Long): ByteArray =
        withContext(Dispatchers.IO) {           // <- mueve la red a IO
            api.downloadEvidence(id).bytes()    // <- aquí estaba el crash
        }

    suspend fun uploadEvidence(file: File, progressId: Long): Evidence {
        val part = MultipartUtils.filePartFromFile(file)
        val pidBody = MultipartUtils.progressIdBody(progressId)
        return api.uploadEvidence(part, pidBody).image!!.toDomain()
    }

    suspend fun updateEvidence(id: Long, file: File): Evidence {
        val part = MultipartUtils.filePartFromFile(file)
        return api.updateEvidence(id, part).image!!.toDomain()
    }


    suspend fun deleteEvidence(id: Long) {
        api.deleteEvidence(id)
    }
}
