package com.example.tolong.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tolong.repository.Repository

class LoginViewModel(private val repository: Repository) : ViewModel() {
    fun login(userEmail: String, userPassword: String) = repository.login(userEmail, userPassword)
}