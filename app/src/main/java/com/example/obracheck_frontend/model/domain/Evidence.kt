package com.example.obracheck_frontend.model.domain

data class Evidence(
    val id: Long,
    val fileName: String,
    val originalFileName: String,
    val contentType: String,
    val fileSize: Long,
    val uploadDate: String
)

data class EvidenceSummary(
    val id: Long,
    val fileName: String
)
