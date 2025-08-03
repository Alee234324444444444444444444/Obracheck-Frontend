package com.example.obracheck_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obracheck_frontend.model.domain.Evidence
import com.example.obracheck_frontend.repository.EvidenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class EvidenceViewModel(
    private val repo: EvidenceRepository = EvidenceRepository()
) : ViewModel() {

    private val _evidences = MutableStateFlow<List<Evidence>>(emptyList())
    val evidences: StateFlow<List<Evidence>> = _evidences

    fun loadEvidences() = viewModelScope.launch {
        _evidences.value = repo.getAllEvidences()
    }

    fun getEvidence(id: Long, onResult: (Evidence) -> Unit) = viewModelScope.launch {
        onResult(repo.getByIdEvidence(id))
    }

    fun uploadEvidence(file: MultipartBody.Part, progressId: Long, onComplete: () -> Unit) = viewModelScope.launch {
        repo.uploadEvidence(file, progressId)
        loadEvidences()
        onComplete()
    }

    fun updateEvidence(id: Long, file: MultipartBody.Part, onComplete: () -> Unit) = viewModelScope.launch {
        repo.updateEvidence(id, file)
        loadEvidences()
        onComplete()
    }

    fun deleteEvidence(id: Long, onComplete: () -> Unit) = viewModelScope.launch {
        repo.deleteEvidence(id)
        loadEvidences()
        onComplete()
    }
}
