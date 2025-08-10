package com.example.obracheck_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obracheck_frontend.model.domain.Worker
import com.example.obracheck_frontend.model.dto.CreateWorkerRequestDto
import com.example.obracheck_frontend.repository.WorkerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WorkerViewModel(
    private val repo: WorkerRepository = WorkerRepository()
) : ViewModel() {

    private val _workers = MutableStateFlow<List<Worker>>(emptyList())
    val workers: StateFlow<List<Worker>> = _workers

    private var currentSiteId: Long = -1L

    fun loadWorkersBySite(siteId: Long) = viewModelScope.launch {
        currentSiteId = siteId
        val allWorkers = repo.getAllWorkers()
        _workers.value = allWorkers.filter { it.site.id == siteId }
    }

    fun getWorker(id: Long, onResult: (Worker) -> Unit) = viewModelScope.launch {
        onResult(repo.getByIdWorker(id))
    }

    fun createWorker(
        request: CreateWorkerRequestDto,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            repo.createWorker(request)
            reloadCurrentSiteWorkers()
            onComplete()
        } catch (e: HttpException) {
            if (e.code() == 409) {
                onError("Ya existe un trabajador con esa cédula.")
            } else {
                onError("Error HTTP al crear trabajador: ${e.message()}")
            }
        } catch (e: Exception) {
            onError("Error inesperado: ${e.message}")
        }
    }

    fun updateWorker(
        id: Long,
        request: CreateWorkerRequestDto,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            repo.updateWorker(id, request)
            reloadCurrentSiteWorkers()
            onComplete()
        } catch (e: HttpException) {
            if (e.code() == 409) {
                onError("Ya existe un trabajador con esa cédula.")
            } else {
                onError("Error HTTP al actualizar trabajador: ${e.message()}")
            }
        } catch (e: Exception) {
            onError("Error inesperado: ${e.message}")
        }
    }

    fun deleteWorker(id: Long, onComplete: () -> Unit) = viewModelScope.launch {
        try {
            repo.deleteWorker(id)
            reloadCurrentSiteWorkers()
            onComplete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun reloadCurrentSiteWorkers() {
        if (currentSiteId != -1L) {
            loadWorkersBySite(currentSiteId)
        }
    }
}
