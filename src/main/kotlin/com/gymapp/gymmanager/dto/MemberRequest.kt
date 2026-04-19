package com.gymapp.gymmanager.dto

data class MemberRequest(
    val nombre: String,
    val telefono: String,
    val email: String,
    val activo: Boolean? = null
)