package com.example.obracheck_frontend.model.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendanceBulkUpsertRequestDto(
    @Json(name = "site_id") val siteId: Long,
    val date: String? = null, // yyyy-MM-dd o null
    val items: List<Item>
) {
    @JsonClass(generateAdapter = true)
    data class Item(
        @Json(name = "worker_id") val workerId: Long,
        val status: AttendanceStatus
    )
}
