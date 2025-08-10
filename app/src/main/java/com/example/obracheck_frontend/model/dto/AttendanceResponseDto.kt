package com.example.obracheck_frontend.model.dto

import com.squareup.moshi.Json

data class AttendanceResponseDto(
    val id: Long,
    val workerId: Long,
    val workerName: String,
    val ci: String? = null,
    val siteId: Long,
    val siteName: String,
    val date: String,              // yyyy-MM-dd
    val status: AttendanceStatus
)
