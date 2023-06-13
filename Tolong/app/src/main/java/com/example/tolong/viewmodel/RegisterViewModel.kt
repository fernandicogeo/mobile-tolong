package com.example.tolong.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tolong.repository.Repository

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    fun signup(userName: String, userEmail: String, userPassword: String, userConfPassword: String) = repository.register(userName, userEmail, userPassword, userConfPassword)
}