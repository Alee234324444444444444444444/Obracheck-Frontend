package com.example.obracheck_frontend.model.domain

data class Worker(
    val id: Long,
    val name: String,
    val role: String,
    val ci: String,
    val site: SiteSummary
)

data class WorkerSummary(
    val id: Long,
    val name: String,
    val role: String
)

