package com.example.obracheck_frontend.repository

import com.example.obracheck_frontend.model.domain.Site
import com.example.obracheck_frontend.model.domain.User
import com.example.obracheck_frontend.model.dto.CreateSiteRequestDto
import com.example.obracheck_frontend.model.mapper.toDomain
import com.example.obracheck_frontend.network.ApiClient

class SiteRepository {

    private val api = ApiClient.api

    suspend fun getAllSites(): List<Site> {
        val fakeUser = User(id = 1, name = "Admin", email = "admin@obra.com") // adapta los campos


        return listOf(
            Site(
                id = 1,
                name = "Obra Norte",
                address = "Av. Norte 123",
                user = fakeUser,
                progresses = listOf(),
                workers = listOf()
            ),
            Site(
                id = 2,
                name = "Obra Sur",
                address = "Av. Sur 456",
                user = fakeUser,
                progresses = emptyList(),
                workers = emptyList()
            )
        )
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
