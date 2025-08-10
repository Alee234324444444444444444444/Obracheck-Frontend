package com.example.obracheck_frontend.model.dto

import com.squareup.moshi.Json

data class AttendanceBulkUpsertRequestDto(
    val siteId: Long,
    val date: String? = null, // yyyy-MM-dd o null
    val items: List<Item>
) {
    data class Item(
        val workerId: Long,
        val status: AttendanceStatus
    )
}
