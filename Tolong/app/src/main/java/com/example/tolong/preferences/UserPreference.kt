package com.example.tolong.preferences

import android.content.Context
import com.example.tolong.model.UserModel

class UserPreference(context: Context) {

    private val pref = context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)

    fun saveLoginSession(user: UserModel) {
        val prefEdit = pref.edit()
        prefEdit.putString(USER_NAME, user.name)
        prefEdit.putString(USER_EMAIL, user.email)
        prefEdit.putString(USER_TOKEN, user.accessToken)
        prefEdit.apply()
    }



    fun getLoginSession() : UserModel {
        val name = pref.getString(USER_NAME, null)
        val email = pref.getString(USER_EMAIL, null)
        val accessToken = pref.getString(USER_TOKEN, null)

        return UserModel(name, email, accessToken)
    }

    fun clearLoginSession() {
        val prefClear = pref.edit().clear()
        prefClear.apply()
    }



    companion object {
        private const val USER_NAME = "user_name"
        private const val USER_EMAIL = "user_email"
        private const val USER_TOKEN = "user_token"
        private const val USER_PREF = "user_pref"
    }
}