package com.example.obracheck_frontend.model.mapper

import com.example.obracheck_frontend.model.domain.Worker
import com.example.obracheck_frontend.model.domain.WorkerSummary
import com.example.obracheck_frontend.model.dto.WorkerDto
import com.example.obracheck_frontend.model.dto.WorkerSummaryDto

fun WorkerDto.toDomain(): Worker = Worker(
    id = id,
    name = name,
    role = role,
    ci = ci,
    site = site.toDomain()
)

fun WorkerSummaryDto.toDomain(): WorkerSummary = WorkerSummary(
    id = id,
    name = name,
    role = role
)
