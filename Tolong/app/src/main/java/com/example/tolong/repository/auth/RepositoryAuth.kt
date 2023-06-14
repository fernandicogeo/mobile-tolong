package com.example.tolong.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.tolong.api.auth.ApiServiceAuth
import com.example.tolong.api.auth.LoginRequest
import com.example.tolong.api.auth.RegisterRequest
import com.example.tolong.model.LoginModel
import com.example.tolong.model.RegisterModel
import com.example.tolong.preferences.UserPreference
import com.example.tolong.repository.ResultCondition
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class RepositoryAuth(private val pref: UserPreference, private val apiService: ApiServiceAuth) {
    fun login(userEmail: String, userPassword: String) : LiveData<ResultCondition<LoginModel>> = liveData {
        emit(ResultCondition.LoadingState)
        try {
            val request = LoginRequest(userEmail, userPassword)
            val json = Gson().toJson(request)
            val requestBody = json.toRequestBody("application/json".toMediaType())

            val response = apiService.login(requestBody)
            if (response.error.toBoolean()) {
                emit(ResultCondition.ErrorState(response.msg))
            } else {
                emit(ResultCondition.SuccessState(response))
            }
        } catch (e: Exception) {
            emit(ResultCondition.ErrorState(e.message.toString()))
        }
    }

    fun register(userName: String, userEmail: String, userPassword: String, userConfPassword: String): LiveData<ResultCondition<RegisterModel>> = liveData {
        emit(ResultCondition.LoadingState)
        try {
            val request = RegisterRequest(userName, userEmail, userPassword, userConfPassword)
            val json = Gson().toJson(request)
            val requestBody = json.toRequestBody("application/json".toMediaType())

            val response = apiService.register(requestBody)
            if (response.error.toBoolean()) {
                emit(ResultCondition.ErrorState(response.msg))
            } else {
                emit(ResultCondition.SuccessState(response))
            }
        } catch (e: Exception) {
            emit(ResultCondition.ErrorState(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instanceRepo: RepositoryAuth? = null
        fun getInstance(preference: UserPreference, api: ApiServiceAuth): RepositoryAuth =
            instanceRepo ?: synchronized(this) {
                instanceRepo ?: RepositoryAuth(preference, api)
            }.also {
                instanceRepo = it
            }
    }
}