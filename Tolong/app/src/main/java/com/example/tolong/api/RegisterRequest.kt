package com.example.tolong.api

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val confPassword: String
)
