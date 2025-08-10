package com.example.obracheck_frontend.model.mapper

import com.example.obracheck_frontend.model.domain.Attendance
import com.example.obracheck_frontend.model.dto.*

fun AttendanceResponseDto.toDomain(): Attendance = Attendance(
    workerId = workerId,
    workerName = workerName,
    ci = ci?: "",
    state = status
)


fun Attendance.toBulkItem(): AttendanceBulkUpsertRequestDto.Item =
    AttendanceBulkUpsertRequestDto.Item(
        workerId = workerId,
        status = state
    )
