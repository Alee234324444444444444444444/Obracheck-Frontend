package com.example.obracheck_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obracheck_frontend.model.domain.Progress
import com.example.obracheck_frontend.model.dto.CreateProgressRequestDto
import com.example.obracheck_frontend.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProgressViewModel(
    private val repo: ProgressRepository = ProgressRepository()
) : ViewModel() {

    private val _progresses = MutableStateFlow<List<Progress>>(emptyList())
    val progresses: StateFlow<List<Progress>> = _progresses

    fun loadProgresses() = viewModelScope.launch {
        _progresses.value = repo.getAllProgress()
    }

    fun getProgress(id: Long, onResult: (Progress) -> Unit) = viewModelScope.launch {
        onResult(repo.getByIdProgress(id))
    }

    fun createProgress(request: CreateProgressRequestDto, onComplete: () -> Unit) = viewModelScope.launch {
        repo.createProgress(request)
        loadProgresses()
        onComplete()
    }

    fun updateProgress(id: Long, request: CreateProgressRequestDto, onComplete: () -> Unit) = viewModelScope.launch {
        repo.updateProgress(id, request)
        loadProgresses()
        onComplete()
    }

    fun deleteProgress(id: Long, onComplete: () -> Unit) = viewModelScope.launch {
        repo.deleteProgress(id)
        loadProgresses()
        onComplete()
    }
}
