package com.example.obracheck_frontend.model.dto

import com.squareup.moshi.Json

data class AttendanceListResponseDto(
    val siteId: Long,
    val siteName: String,
    val date: String, // yyyy-MM-dd
    val items: List<AttendanceResponseDto>
)
