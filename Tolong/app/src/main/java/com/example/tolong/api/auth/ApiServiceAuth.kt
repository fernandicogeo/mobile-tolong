package com.example.tolong.api.auth

import com.example.tolong.model.EditModel
import com.example.tolong.model.LoginModel
import com.example.tolong.model.RegisterModel
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiServiceAuth {
    @POST("login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body requestBody: RequestBody) : LoginModel

    @POST("users")
    @Headers("Content-Type: application/json")
    suspend fun register(@Body requestBody: RequestBody): RegisterModel

    @PATCH("edit")
    @Headers("Content-Type: application/json")
    suspend fun edit(@Body requestBody: RequestBody): EditModel
}