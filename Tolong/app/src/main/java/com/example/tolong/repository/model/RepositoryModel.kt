package com.example.tolong.repository.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.tolong.api.model.ApiServiceModel
import com.example.tolong.model.ModelModel
import com.example.tolong.preferences.UserPreference
import com.example.tolong.repository.ResultCondition
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class RepositoryModel(private val pref: UserPreference, private val apiService: ApiServiceModel) {
    fun uploadImage(file: File): LiveData<ResultCondition<ModelModel>> = liveData {
        emit(ResultCondition.LoadingState)
        try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = apiService.model(filePart)

            if (response.isSuccessful) {
                val uploadResponse = response.body()
                emit(ResultCondition.SuccessState(uploadResponse!!))
            } else {
                emit(ResultCondition.ErrorState("Request failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(ResultCondition.ErrorState(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instanceRepo: RepositoryModel? = null
        fun getInstance(preference: UserPreference, api: ApiServiceModel): RepositoryModel =
            instanceRepo ?: synchronized(this) {
                instanceRepo ?: RepositoryModel(preference, api)
            }.also {
                instanceRepo = it
            }
    }
}