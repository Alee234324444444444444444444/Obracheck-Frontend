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

    // 👇 Cache de qué sitio/fecha están cargados en memoria
    private var cachedSiteId: Long? = null
    private var cachedDateIso: String? = null

    fun loadAttendances(siteId: Long, date: String) = viewModelScope.launch {
        try {
            val response = ApiClient.api.getAttendancesBySiteAndDate(siteId, date)
            val workers = ApiClient.api.listWorkers().filter { it.site.id == siteId }

            val apiMap = response.items.associateBy { it.workerId }
            val merged = workers.map { w ->
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

            _attendances.value = merged

            // ✅ Actualiza el cache de contexto cargado
            cachedSiteId = siteId
            cachedDateIso = date
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Cambia estado en memoria y guarda todo en bulk inmediatamente */
    fun updateAttendanceState(siteId: Long, date: String, workerId: Long, newStatus: AttendanceStatus) {
        viewModelScope.launch {
            try {
                val updated = _attendances.value.map {
                    if (it.workerId == workerId) it.copy(state = newStatus) else it
                }
                _attendances.value = updated

                val request = AttendanceBulkUpsertRequestDto(
                    siteId = siteId,
                    date = date,
                    items = updated.map { it.toBulkItem() }
                )
                ApiClient.api.upsertAttendancesBulk(request)

                // Nota: no tocamos cachedSiteId/cachedDateIso; ya apuntan al contexto actual
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** Para PDF: solo usa memoria si coincide sitio+fecha; si no, consulta esa fecha al backend. */
    suspend fun getAttendancesForPDF(siteId: Long, date: String): List<Attendance> {
        return try {
            val local = _attendances.value
            if (local.isNotEmpty() && cachedSiteId == siteId && cachedDateIso == date) {
                // ✅ Misma fecha/sitio: devuelve lo que ya marcaste en esa pantalla
                local
            } else {
                // 🔁 Otra fecha: pide al backend y fusiona con workers
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
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
