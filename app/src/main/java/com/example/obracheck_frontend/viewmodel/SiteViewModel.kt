package com.example.obracheck_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obracheck_frontend.model.domain.Site
import com.example.obracheck_frontend.model.dto.CreateSiteRequestDto
import com.example.obracheck_frontend.repository.SiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SiteViewModel(
    private val repo: SiteRepository = SiteRepository()
) : ViewModel() {

    private val _sites = MutableStateFlow<List<Site>>(emptyList())
    val sites: StateFlow<List<Site>> = _sites

    fun loadSites() = viewModelScope.launch {
        try {
            _sites.value = repo.getAllSites()
        } catch (e: Exception) {
            e.printStackTrace()
            _sites.value = emptyList()
        }
    }

    fun getSite(id: Long, onResult: (Site) -> Unit) = viewModelScope.launch {
        try {
            val site = repo.getByIdSite(id)
            onResult(site)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createSite(request: CreateSiteRequestDto, onComplete: () -> Unit) = viewModelScope.launch {
        try {
            repo.createSite(request)
            loadSites()
            onComplete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateSite(id: Long, request: CreateSiteRequestDto, onComplete: () -> Unit) = viewModelScope.launch {
        try {
            repo.updateSite(id, request)
            loadSites()
            onComplete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteSite(id: Long, onComplete: () -> Unit) = viewModelScope.launch {
        try {
            repo.deleteSite(id)
            loadSites()
            onComplete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
