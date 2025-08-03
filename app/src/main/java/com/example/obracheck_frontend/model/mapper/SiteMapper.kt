package com.example.obracheck_frontend.model.mapper

import com.example.obracheck_frontend.model.domain.Site
import com.example.obracheck_frontend.model.domain.SiteSummary
import com.example.obracheck_frontend.model.dto.SiteDto
import com.example.obracheck_frontend.model.dto.SiteSummaryDto

fun SiteDto.toDomain(): Site = Site(
    id = id,
    name = name,
    address = address,
    user = user.toDomain(),
    workers = workers.map { it.toDomain() },
    progresses = progresses.map { it.toDomain() }
)

fun SiteSummaryDto.toDomain(): SiteSummary = SiteSummary(
    id = id,
    name = name
)
