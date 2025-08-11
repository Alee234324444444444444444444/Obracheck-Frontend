package com.example.obracheck_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obracheck_frontend.model.domain.Progress
import com.example.obracheck_frontend.model.dto.CreateProgressRequestDto
import com.example.obracheck_frontend.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProgressViewModel(
    private val repo: ProgressRepository = ProgressRepository()
) : ViewModel() {

    private val _progresses = MutableStateFlow<List<Progress>>(emptyList())
    val progresses: StateFlow<List<Progress>> = _progresses

    private var currentSiteId: Long = -1L
    private var currentWorkerId: Long = -1L

    /** Carga y filtra SOLO por siteId + workerId (orden: fecha desc, id desc) */
    fun loadProgressesByWorker(siteId: Long, workerId: Long) = viewModelScope.launch {
        currentSiteId = siteId
        currentWorkerId = workerId

        val all = repo.getAllProgress()
        _progresses.value = all
            .filter { it.site.id == siteId && it.worker.id == workerId }
            .sortedWith(compareByDescending<Progress> { it.date }.thenByDescending { it.id })
    }



    fun getProgress(id: Long, onResult: (Progress) -> Unit) = viewModelScope.launch {
        onResult(repo.getByIdProgress(id))
    }

    fun createProgress(
        request: CreateProgressRequestDto,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            repo.createProgress(request)
            reloadCurrent()
            onComplete()
        } catch (e: HttpException) {
            onError("Error HTTP al crear progreso: ${e.message()}")
        } catch (e: Exception) {
            onError("Error inesperado: ${e.message}")
        }
    }

    fun updateProgress(
        id: Long,
        request: CreateProgressRequestDto,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            repo.updateProgress(id, request)
            reloadCurrent()
            onComplete()
        } catch (e: HttpException) {
            onError("Error HTTP al actualizar progreso: ${e.message()}")
        } catch (e: Exception) {
            onError("Error inesperado: ${e.message}")
        }
    }

    fun deleteProgress(id: Long, onComplete: () -> Unit) = viewModelScope.launch {
        try {
            repo.deleteProgress(id)
            reloadCurrent()
            onComplete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun reloadCurrent() {
        if (currentSiteId != -1L && currentWorkerId != -1L) {
            loadProgressesByWorker(currentSiteId, currentWorkerId)
        }
    }
}
