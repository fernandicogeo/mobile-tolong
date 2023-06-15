package com.example.tolong.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tolong.repository.auth.RepositoryAuth

class EditViewModel(private val repository: RepositoryAuth) : ViewModel() {

    fun edit(userName: String, userEmail: String, userAddress: String?, userNohp: String?, userPassword: String?) = repository.edit(userName, userEmail, userAddress, userNohp, userPassword)
}