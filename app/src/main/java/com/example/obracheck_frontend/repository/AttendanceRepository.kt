package com.example.obracheck_frontend.repository

import com.example.obracheck_frontend.model.domain.Attendance
import com.example.obracheck_frontend.model.dto.AttendanceBulkUpsertRequestDto
import com.example.obracheck_frontend.model.mapper.toDomain
import com.example.obracheck_frontend.model.mapper.toBulkItem
import com.example.obracheck_frontend.network.ApiClient

class AttendanceRepository {

    private val api = ApiClient.api

    /** Obtener asistencia por sitio y fecha */
    suspend fun getAttendances(siteId: Long, date: String): List<Attendance> =
        api.getAttendancesBySiteAndDate(siteId, date)
            .items
            .map { it.toDomain() }

    /** Guardar/actualizar asistencia en bloque */
    suspend fun upsertAttendances(siteId: Long, date: String, list: List<Attendance>) {
        val request = AttendanceBulkUpsertRequestDto(
            siteId = siteId,
            date = date,
            items = list.map { it.toBulkItem() }
        )
        api.upsertAttendancesBulk(request)
    }
}
