package com.example.obracheck_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obracheck_frontend.model.domain.User
import com.example.obracheck_frontend.model.dto.CreateUserRequestDto
import com.example.obracheck_frontend.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repo: UserRepository = UserRepository()
) : ViewModel() {


    // 🔵 Funciones CRUD previas
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun loadUsers() = viewModelScope.launch {
        _users.value = repo.getAllUsers()
    }

    fun getUser(id: Long, onResult: (User) -> Unit) = viewModelScope.launch {
        onResult(repo.getUserById(id))
    }

    fun createUser(request: CreateUserRequestDto, onComplete: (Long?) -> Unit) = viewModelScope.launch {
        try {
            val createdUser = repo.createUser(request)
            loadUsers()
            onComplete(createdUser.id)
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(null)
        }
    }

    fun updateUser(id: Long, request: CreateUserRequestDto, onComplete: () -> Unit) = viewModelScope.launch {
        repo.updateUser(id, request)
        loadUsers()
        onComplete()
    }

    fun deleteUser(id: Long, onComplete: () -> Unit) = viewModelScope.launch {
        repo.deleteUser(id)
        loadUsers()
        onComplete()
    }
}
