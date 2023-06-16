package com.example.tolong.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tolong.helper.Injection
import com.example.tolong.repository.nearby.RepositoryNearby

class ViewModelFactoryNearby private constructor(private val repository: RepositoryNearby) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NearbyViewModel::class.java)) {
            return NearbyViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactoryNearby? = null
        fun getInstanceNearby(context: Context): ViewModelFactoryNearby {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactoryNearby(Injection.provideRepositoryNearby(context))
            }.also { instance = it }
        }
    }
}