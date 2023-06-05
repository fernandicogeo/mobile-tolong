package com.example.tolong.helper

import android.content.Context
import com.example.tolong.api.ApiConfig
import com.example.tolong.preferences.UserPreference
import com.example.tolong.repository.Repository

object Injection {
    fun provideRepository(context: Context): Repository {
        val preference = UserPreference(context)
        val api = ApiConfig.getApiService()
        return Repository.getInstance(preference, api)
    }
}