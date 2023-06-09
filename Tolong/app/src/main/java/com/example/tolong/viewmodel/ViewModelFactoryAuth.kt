package com.example.tolong.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tolong.helper.Injection
import com.example.tolong.repository.auth.RepositoryAuth

class ViewModelFactoryAuth private constructor(private val repository: RepositoryAuth) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
         if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
            return EditViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactoryAuth? = null
        fun getInstanceAuth(context: Context): ViewModelFactoryAuth {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactoryAuth(Injection.provideRepositoryAuth(context))
            }.also { instance = it }
        }
    }
}