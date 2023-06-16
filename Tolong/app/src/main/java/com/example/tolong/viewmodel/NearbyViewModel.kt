package com.example.tolong.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tolong.repository.nearby.RepositoryNearby

class NearbyViewModel(private val repository: RepositoryNearby) : ViewModel() {
    fun search(search: String) = repository.search(search)
}