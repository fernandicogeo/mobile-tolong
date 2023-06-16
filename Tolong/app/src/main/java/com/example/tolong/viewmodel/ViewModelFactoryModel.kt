package com.example.tolong.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tolong.helper.Injection
import com.example.tolong.repository.model.RepositoryModel

class ViewModelFactoryModel private constructor(private val repository: RepositoryModel) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirstAidViewModel::class.java)) {
            return FirstAidViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactoryModel? = null
        fun getInstanceModel(context: Context): ViewModelFactoryModel {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactoryModel(Injection.provideRepositoryModel(context))
            }.also { instance = it }
        }
    }
}