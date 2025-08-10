package com.example.obracheck_frontend.model.domain

import com.example.obracheck_frontend.model.dto.AttendanceStatus

data class Attendance(
    val workerId: Long,
    val workerName: String,
    val ci: String,
    val state: AttendanceStatus     // Estado de asistencia para la UI
)
