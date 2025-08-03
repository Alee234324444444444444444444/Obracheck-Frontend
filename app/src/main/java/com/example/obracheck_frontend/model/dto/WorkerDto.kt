package com.example.obracheck_frontend.model.dto

data class WorkerDto(
    val id: Long,
    val name: String,
    val role: String,
    val ci: String,
    val site: SiteSummaryDto
)
