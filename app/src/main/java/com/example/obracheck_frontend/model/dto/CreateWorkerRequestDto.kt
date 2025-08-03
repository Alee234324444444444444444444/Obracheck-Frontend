package com.example.obracheck_frontend.model.dto

data class CreateWorkerRequestDto(
    val name: String,
    val role: String,
    val ci: String,
    val site_id: Long
)
