package com.example.obracheck_frontend.model.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendanceResponseDto(
    val id: Long,
    @Json(name = "worker_id") val workerId: Long,
    @Json(name = "worker_name") val workerName: String,
    val ci: String? = null,
    @Json(name = "site_id") val siteId: Long,
    @Json(name = "site_name") val siteName: String,
    val date: String,
    val status: AttendanceStatus
)
