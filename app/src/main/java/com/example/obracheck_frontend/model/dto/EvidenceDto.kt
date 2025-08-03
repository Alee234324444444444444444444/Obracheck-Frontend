package com.example.obracheck_frontend.model.dto

data class EvidenceDto(
    val id: Long,
    val fileName: String,
    val originalFileName: String,
    val contentType: String,
    val fileSize: Long,
    val uploadDate: String
)
