package com.example.obracheck_frontend.model.dto

data class ProgressDto(
    val id: Long,
    val description: String,
    val date: String,
    val site: SiteSummaryDto,
    val worker: WorkerSummaryDto,
    val evidences: List<EvidenceSummaryDto>
)
