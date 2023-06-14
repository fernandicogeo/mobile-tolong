package com.example.tolong.api.nearby

import com.example.tolong.model.SearchModel
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiServiceNearby {
    @POST("search")
    @Headers("Content-Type: application/json")
    suspend fun search(@Body requestBody: RequestBody) : Response<SearchModel>
}