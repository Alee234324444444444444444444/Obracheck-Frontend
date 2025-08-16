package com.example.obracheck_frontend.model.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EvidenceDto(
    val id: Long,
    @Json(name = "file_name") val fileName: String,
    @Json(name = "original_file_name") val originalFileName: String,
    @Json(name = "content_type") val contentType: String,
    @Json(name = "file_size") val fileSize: Long,
    @Json(name = "upload_date") val uploadDate: String
)
