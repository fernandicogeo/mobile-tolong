package com.example.tolong.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.tolong.api.ApiService
import com.example.tolong.model.LoginModel
import com.example.tolong.model.RegisterModel
import com.example.tolong.preferences.UserPreference

class Repository(private val pref: UserPreference, private val apiService: ApiService) {
    fun login(userEmail: String, userPassword: String) : LiveData<ResultCondition<LoginModel>> = liveData {
        emit(ResultCondition.LoadingState)
        try {
            val response = apiService.login(userEmail, userPassword)
            if (response.error) {
                emit(ResultCondition.ErrorState(response.message))
            } else {
                emit(ResultCondition.SuccessState(response))
            }
        } catch (e: Exception) {
            emit(ResultCondition.ErrorState(e.message.toString()))
        }
    }

    fun register(userName: String, userEmail: String, userPassword: String): LiveData<ResultCondition<RegisterModel>> = liveData {
        emit(ResultCondition.LoadingState)
        try {
            val response = apiService.register(userName, userEmail, userPassword)
            if (response.error) {
                emit(ResultCondition.ErrorState(response.message))
            } else {
                emit(ResultCondition.SuccessState(response))
            }
        } catch (e: Exception) {
            emit(ResultCondition.ErrorState(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instanceRepo: Repository? = null
        fun getInstance(preference: UserPreference, api: ApiService): Repository =
            instanceRepo ?: synchronized(this) {
                instanceRepo ?: Repository(preference, api)
            }.also {
                instanceRepo = it
            }
    }
}