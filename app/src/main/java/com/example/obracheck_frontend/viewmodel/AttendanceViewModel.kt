package com.example.obracheck_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obracheck_frontend.model.domain.Attendance
import com.example.obracheck_frontend.model.dto.AttendanceStatus
import com.example.obracheck_frontend.model.dto.AttendanceBulkUpsertRequestDto
import com.example.obracheck_frontend.model.mapper.toDomain
import com.example.obracheck_frontend.model.mapper.toBulkItem
import com.example.obracheck_frontend.network.ApiClient
import com.example.obracheck_frontend.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(
    private val repo: AttendanceRepository = AttendanceRepository()
) : ViewModel() {

    private val _attendances = MutableStateFlow<List<Attendance>>(emptyList())
    val attendances: StateFlow<List<Attendance>> = _attendances

    fun loadAttendances(siteId: Long, date: String) = viewModelScope.launch {
        try {
            val response = ApiClient.api.getAttendancesBySiteAndDate(siteId, date)
            val workers = ApiClient.api.listWorkers().filter { it.site.id == siteId }

            val apiMap = response.items.associateBy { it.workerId }
            val merged = workers.map { w ->
                apiMap[w.id]?.toDomain()?.copy(
                    // usa nombre/ci actuales del worker
                    workerName = w.name,
                    ci = w.ci
                ) ?: Attendance(
                    workerId = w.id,
                    workerName = w.name,
                    ci = w.ci,
                    state = AttendanceStatus.NA
                )
            }.sortedBy { it.workerName }

            _attendances.value = merged
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /** Cambia estado en memoria y guarda todo en bulk inmediatamente */
    fun updateAttendanceState(siteId: Long, date: String, workerId: Long, newStatus: AttendanceStatus) {
        viewModelScope.launch {
            try {
                // 1) Actualizar en memoria
                val updated = _attendances.value.map {
                    if (it.workerId == workerId) it.copy(state = newStatus) else it
                }
                _attendances.value = updated

                println("💾 VM: Guardando estado $newStatus para worker $workerId") // Debug

                // ✅ USAR TU ESTRUCTURA REAL DE DTOs
                val request = AttendanceBulkUpsertRequestDto(
                    siteId = siteId,
                    date = date,
                    items = updated.map { attendance ->
                        attendance.toBulkItem() // ✅ Usar tu mapper
                    }
                )

                val response = ApiClient.api.upsertAttendancesBulk(request)
                println("✅ VM: Guardado exitoso, ${response.size} registros procesados") // Debug

            } catch (e: Exception) {
                println("❌ VM: Error guardando: ${e.message}") // Debug
                e.printStackTrace()
            }
        }
    }

    suspend fun getAttendancesForPDF(siteId: Long, date: String): List<Attendance> {
        return try {
            // Si ya cargaste esta fecha y hay algo en memoria, úsalo
            val local = attendances.value
            if (local.isNotEmpty()) return local

            // Si no, fusiona API + workers
            val response = ApiClient.api.getAttendancesBySiteAndDate(siteId, date)
            val workers = ApiClient.api.listWorkers().filter { it.site.id == siteId }

            val apiMap = response.items.associateBy { it.workerId }
            workers.map { w ->
                apiMap[w.id]?.toDomain()?.copy(
                    workerName = w.name,
                    ci = w.ci
                ) ?: Attendance(
                    workerId = w.id,
                    workerName = w.name,
                    ci = w.ci,
                    state = AttendanceStatus.NA
                )
            }.sortedBy { it.workerName }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

}