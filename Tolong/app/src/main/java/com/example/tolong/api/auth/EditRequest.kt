package com.example.tolong.api.auth

data class EditRequest(
    val name: String,
    val email: String,
    val alamat: String?,
    val nomorhp: String?,
    val password: String?,
)