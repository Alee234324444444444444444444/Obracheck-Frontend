package com.example.obracheck_frontend.model.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendanceListResponseDto(
    @Json(name = "site_id") val siteId: Long,
    @Json(name = "site_name") val siteName: String,
    val date: String, // yyyy-MM-dd
    val items: List<AttendanceResponseDto>
)
