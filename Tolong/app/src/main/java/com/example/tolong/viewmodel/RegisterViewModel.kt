package com.example.tolong.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tolong.repository.auth.RepositoryAuth

class RegisterViewModel(private val repository: RepositoryAuth) : ViewModel() {

    fun signup(userName: String, userEmail: String, userPassword: String, userConfPassword: String) = repository.register(userName, userEmail, userPassword, userConfPassword)
}