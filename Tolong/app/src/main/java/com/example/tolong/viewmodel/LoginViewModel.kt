package com.example.tolong.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tolong.repository.auth.RepositoryAuth

class LoginViewModel(private val repository: RepositoryAuth) : ViewModel() {
    fun login(userEmail: String, userPassword: String) = repository.login(userEmail, userPassword)
}