package com.example.obracheck_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obracheck_frontend.model.domain.Worker
import com.example.obracheck_frontend.model.dto.CreateWorkerRequestDto
import com.example.obracheck_frontend.repository.WorkerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkerViewModel(
    private val repo: WorkerRepository = WorkerRepository()
) : ViewModel() {

    private val _workers = MutableStateFlow<List<Worker>>(emptyList())
    val workers: StateFlow<List<Worker>> = _workers

    fun loadWorkers() = viewModelScope.launch {
        _workers.value = repo.getAllWorkers()
    }

    fun getWorker(id: Long, onResult: (Worker) -> Unit) = viewModelScope.launch {
        onResult(repo.getByIdWorker(id))
    }

    fun createWorker(request: CreateWorkerRequestDto, onComplete: () -> Unit) = viewModelScope.launch {
        repo.createWorker(request)
        loadWorkers()
        onComplete()
    }

    fun updateWorker(id: Long, request: CreateWorkerRequestDto, onComplete: () -> Unit) = viewModelScope.launch {
        repo.updateWorker(id, request)
        loadWorkers()
        onComplete()
    }

    fun deleteWorker(id: Long, onComplete: () -> Unit) = viewModelScope.launch {
        repo.deleteWorker(id)
        loadWorkers()
        onComplete()
    }
}
