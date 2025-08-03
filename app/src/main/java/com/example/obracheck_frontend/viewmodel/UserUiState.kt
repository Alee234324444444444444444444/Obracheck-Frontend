package com.example.obracheck_frontend.viewmodel

import com.example.obracheck_frontend.model.domain.User

data class UserUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)
