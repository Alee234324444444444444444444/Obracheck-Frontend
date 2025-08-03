package com.example.obracheck_frontend.model.dto

data class CreateProgressRequestDto(
    val description: String,
    val date: String,
    val site_id: Long,
    val worker_id: Long
)
