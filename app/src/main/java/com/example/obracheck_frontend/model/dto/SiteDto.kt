package com.example.obracheck_frontend.model.dto

data class SiteDto(
    val id: Long,
    val name: String,
    val address: String,
    val user: UserDto,
    val workers: List<WorkerSummaryDto>,
    val progresses: List<ProgressSummaryDto>
)
