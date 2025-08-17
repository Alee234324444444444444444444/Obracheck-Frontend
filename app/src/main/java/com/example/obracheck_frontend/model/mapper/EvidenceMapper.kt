package com.example.obracheck_frontend.model.mapper

import com.example.obracheck_frontend.model.domain.Evidence
import com.example.obracheck_frontend.model.domain.EvidenceSummary
import com.example.obracheck_frontend.model.dto.EvidenceDto
import com.example.obracheck_frontend.model.dto.EvidenceSummaryDto

fun EvidenceDto.toDomain(): Evidence = Evidence(
    id = id,
    fileName = fileName,
    originalFileName = originalFileName,
    contentType = contentType,
    fileSize = fileSize,
    uploadDate = uploadDate,
    progressId = progressId

)

fun EvidenceSummaryDto.toDomain(): EvidenceSummary = EvidenceSummary(
    id = id,
    fileName = fileName
)
