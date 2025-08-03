package com.example.obracheck_frontend.model.dto

data class EvidenceListResponseDto(
    val images: List<EvidenceDto>,
    val total: Long
)
