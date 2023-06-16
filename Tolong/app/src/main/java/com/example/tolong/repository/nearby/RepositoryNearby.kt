package com.example.tolong.repository.nearby

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.tolong.api.nearby.ApiServiceNearby
import com.example.tolong.api.nearby.SearchRequest
import com.example.tolong.model.SearchModel
import com.example.tolong.preferences.UserPreference
import com.example.tolong.repository.ResultCondition
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class RepositoryNearby(private val pref: UserPreference, private val apiService: ApiServiceNearby) {
    fun search(search: String) : LiveData<ResultCondition<SearchModel>> = liveData {
        emit(ResultCondition.LoadingState)
        try {
            val request = SearchRequest(search)
            val json = Gson().toJson(request)
            val requestBody = json.toRequestBody("application/json".toMediaType())

            val response = apiService.search(requestBody)

            if (response.isSuccessful) {
                val searchModel = response.body()
                emit(ResultCondition.SuccessState(searchModel!!))
            } else {
                emit(ResultCondition.ErrorState("Request failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(ResultCondition.ErrorState(e.message.toString()))
        }
    }


    companion object {
        @Volatile
        private var instanceRepo: RepositoryNearby? = null
        fun getInstance(preference: UserPreference, api: ApiServiceNearby): RepositoryNearby =
            instanceRepo ?: synchronized(this) {
                instanceRepo ?: RepositoryNearby(preference, api)
            }.also {
                instanceRepo = it
            }
    }
}