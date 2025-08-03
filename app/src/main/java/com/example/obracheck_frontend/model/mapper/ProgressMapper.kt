package com.example.obracheck_frontend.model.mapper

import com.example.obracheck_frontend.model.domain.Progress
import com.example.obracheck_frontend.model.domain.ProgressSummary
import com.example.obracheck_frontend.model.dto.ProgressDto
import com.example.obracheck_frontend.model.dto.ProgressSummaryDto

fun ProgressDto.toDomain(): Progress = Progress(
    id = id,
    description = description,
    date = date,
    site = site.toDomain(),
    worker = worker.toDomain(),
    evidences = evidences.map { it.toDomain() }
)

fun ProgressSummaryDto.toDomain(): ProgressSummary = ProgressSummary(
    id = id,
    description = description,
    date = date
)
