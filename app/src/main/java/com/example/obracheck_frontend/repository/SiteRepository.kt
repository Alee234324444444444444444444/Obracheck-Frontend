package com.example.obracheck_frontend.repository

import com.example.obracheck_frontend.model.domain.Site
import com.example.obracheck_frontend.model.dto.CreateSiteRequestDto
import com.example.obracheck_frontend.model.mapper.toDomain
import com.example.obracheck_frontend.network.ApiClient

class SiteRepository {

    private val api = ApiClient.api

    suspend fun getSitesByUser(userId: Long): List<Site> {
        val response = api.listSites()
        return response
            .map { it.toDomain() }
            .filter { it.user.id == userId }
    }


    suspend fun getByIdSite(id: Long): Site =
        api.getSiteById(id).toDomain()

    suspend fun createSite(request: CreateSiteRequestDto): Site =
        api.createSite(request).toDomain()

    suspend fun updateSite(id: Long, request: CreateSiteRequestDto): Site =
        api.updateSite(id, request).toDomain()

    suspend fun deleteSite(id: Long) {
        api.deleteSite(id)
    }
}
