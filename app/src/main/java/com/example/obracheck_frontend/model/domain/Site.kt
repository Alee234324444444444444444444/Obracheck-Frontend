package com.example.obracheck_frontend.model.domain


data class Site(
    val id: Long,
    val name: String,
    val address: String,
    val user: User,
    val workers: List<WorkerSummary>,
    val progresses: List<ProgressSummary>
)
