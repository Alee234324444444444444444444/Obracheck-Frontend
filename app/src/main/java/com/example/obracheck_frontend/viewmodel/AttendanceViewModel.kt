package com.example.obracheck_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obracheck_frontend.model.domain.Attendance
import com.example.obracheck_frontend.model.dto.AttendanceStatus
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

    /** Carga: si el backend trae vacío, armamos lista NA con workers del sitio */
    fun loadAttendances(siteId: Long, date: String) = viewModelScope.launch {
        try {
            // 1) Intentar traer asistencias del día
            val fromApi = repo.getAttendances(siteId, date)
            if (fromApi.isNotEmpty()) {
                // (Opcional) enriquecer CI/nombre si en la respuesta falta
                val workers = ApiClient.api.listWorkers().filter { it.site.id == siteId }
                val map = workers.associateBy { it.id }
                val enriched = fromApi.map { a ->
                    val w = map[a.workerId]
                    a.copy(
                        workerName = if (a.workerName.isNotBlank()) a.workerName else (w?.name ?: "Trabajador ${a.workerId}"),
                        ci = if (a.ci.isNotBlank()) a.ci else (w?.ci ?: "")
                    )
                }
                _attendances.value = enriched
                return@launch
            }

            // 2) Primera vez: no hay asistencias -> usar workers del sitio y armar NA
            val workers = ApiClient.api.listWorkers().filter { it.site.id == siteId }
            _attendances.value = workers.map { w ->
                Attendance(
                    workerId = w.id,
                    workerName = w.name,
                    ci = w.ci,
                    state = AttendanceStatus.NA
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Aquí puedes setear un estado de error si usas un UiState
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
                repo.upsertAttendances(siteId, date, updated)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
