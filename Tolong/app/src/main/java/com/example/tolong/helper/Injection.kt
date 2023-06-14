package com.example.tolong.helper

import android.content.Context
import com.example.tolong.api.auth.ApiConfigAuth
import com.example.tolong.api.nearby.ApiConfigNearby
import com.example.tolong.preferences.UserPreference
import com.example.tolong.repository.auth.RepositoryAuth
import com.example.tolong.repository.nearby.RepositoryNearby

object Injection {
    fun provideRepositoryAuth(context: Context): RepositoryAuth {
        val preference = UserPreference(context)
        val api = ApiConfigAuth.getApiService()
        return RepositoryAuth.getInstance(preference, api)
    }

    fun provideRepositoryNearby(context: Context): RepositoryNearby {
        val preference = UserPreference(context)
        val api = ApiConfigNearby.getApiService()
        return RepositoryNearby.getInstance(preference, api)
    }
}