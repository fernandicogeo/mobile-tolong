package com.example.tolong.api.model

import com.example.tolong.model.ModelModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiServiceModel {
    @Multipart
    @POST("/")
    suspend fun model(
        @Part filePart: MultipartBody.Part
    ): Response<ModelModel>
}