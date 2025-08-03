package com.example.obracheck_frontend.model.mapper

import com.example.obracheck_frontend.model.domain.User
import com.example.obracheck_frontend.model.dto.UserDto

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email
)
