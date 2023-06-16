package com.example.tolong.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tolong.repository.model.RepositoryModel
import java.io.File

class FirstAidViewModel(private val repository: RepositoryModel) : ViewModel() {
    fun uploadImage(image: File) = repository.uploadImage(image)
}