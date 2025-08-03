package com.example.obracheck_frontend.model.dto

data class CreateSiteRequestDto(
    val name: String,
    val address: String,
    val user_id: Long
)
