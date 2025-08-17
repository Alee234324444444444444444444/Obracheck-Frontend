package com.example.obracheck_frontend.model.dto

import com.squareup.moshi.Json

data class EvidenceSummaryDto(
    val id: Long,
    @Json(name = "file_name") val fileName: String
)
