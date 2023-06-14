package com.example.tolong.api.auth

import de.hdodenhof.circleimageview.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiConfigAuth {
    companion object {
        fun getApiService(): ApiServiceAuth {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://backend-dot-capstone-tolong.et.r.appspot.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiServiceAuth::class.java)
        }
    }
}