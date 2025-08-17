package com.example.obracheck_frontend.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obracheck_frontend.model.domain.Evidence
import com.example.obracheck_frontend.repository.EvidenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.File

class EvidenceViewModel(
    private val repo: EvidenceRepository = EvidenceRepository()
) : ViewModel() {

    private val _evidences = MutableStateFlow<List<Evidence>>(emptyList())
    val evidences: StateFlow<List<Evidence>> = _evidences

    private var currentProgressId: Long = -1L

    /** Carga y filtra SOLO por progressId (orden: uploadDate desc, id desc) */
    fun loadEvidencesByProgress(progressId: Long) = viewModelScope.launch {
        currentProgressId = progressId
        try {
            val all = repo.getAllEvidences()
            val filtered = all.filter { it.progressId == progressId }
            val sorted = filtered.sortedWith(
                compareByDescending<Evidence> { it.uploadDate }.thenByDescending { it.id }
            )
            _evidences.value = sorted
        } catch (e: Exception) {
            Log.e("EvidenceViewModel", "Error cargando evidencias: ${e.message}", e)
        }
    }

    /** DESCARGA: bytes de la evidencia para mostrar imagen en el diálogo */
    suspend fun getImageBytes(id: Long): ByteArray? = try {
        repo.downloadEvidence(id)
    } catch (e: Exception) {
        Log.e("EvidenceViewModel", "Error descargando imagen $id: ${e.message}", e)
        null
    }

    fun uploadEvidence(
        file: File,
        progressId: Long,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            repo.uploadEvidence(file, progressId)
            delay(300) // pequeño respiro para el backend
            reloadCurrent()
            onComplete()
        } catch (e: HttpException) {
            onError("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            onError(e.message ?: "Error inesperado")
        }
    }

    fun updateEvidence(
        id: Long,
        file: File,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            repo.updateEvidence(id, file)
            reloadCurrent()
            onComplete()
        } catch (e: HttpException) {
            onError("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            onError(e.message ?: "Error inesperado")
        }
    }

    fun deleteEvidence(
        id: Long,
        onComplete: () -> Unit
    ) = viewModelScope.launch {
        try {
            repo.deleteEvidence(id)
            reloadCurrent()
            onComplete()
        } catch (e: Exception) {
            Log.e("EvidenceViewModel", "Error eliminando evidencia: ${e.message}", e)
        }
    }

    private fun reloadCurrent() {
        if (currentProgressId != -1L) {
            loadEvidencesByProgress(currentProgressId)
        }
    }
}
