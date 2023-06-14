package com.example.tolong.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tolong.model.SearchModel

class SharedViewModel : ViewModel() {
    private val _searchModel = MutableLiveData<SearchModel>()
    val searchModel: LiveData<SearchModel> = _searchModel

    fun setSearchModel(model: SearchModel) {
        _searchModel.value = model
    }
}