package com.example.obracheck_frontend.model.domain

data class Progress(
    val id: Long,
    val description: String,
    val date: String,
    val site: SiteSummary,
    val worker: WorkerSummary,
    val evidences: List<EvidenceSummary>
)

data class ProgressSummary(
    val id: Long,
    val description: String,
    val date: String
)
