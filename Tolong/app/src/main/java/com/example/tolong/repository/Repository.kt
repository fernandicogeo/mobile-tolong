package com.example.tolong.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.tolong.api.ApiService
import com.example.tolong.api.LoginRequest
import com.example.tolong.api.RegisterRequest
import com.example.tolong.model.LoginModel
import com.example.tolong.model.RegisterModel
import com.example.tolong.preferences.UserPreference
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class Repository(private val pref: UserPreference, private val apiService: ApiService) {
    fun login(userEmail: String, userPassword: String) : LiveData<ResultCondition<LoginModel>> = liveData {
        emit(ResultCondition.LoadingState)
        try {
            val request = LoginRequest(userEmail, userPassword)
            val json = Gson().toJson(request)
            val requestBody = json.toRequestBody("application/json".toMediaType())

            val response = apiService.login(requestBody)
            if (response.error.toBoolean()) {
                emit(ResultCondition.ErrorState(response.message))
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