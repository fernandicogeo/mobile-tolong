package com.example.tolong.preferences

import android.content.Context
import com.example.tolong.model.UserModel

class UserPreference(context: Context) {

    private val pref = context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)

    fun saveLoginSession(user: UserModel) {
        val prefEdit = pref.edit()
        prefEdit.putString(USER_NAME, user.name)
        prefEdit.putString(USER_ID, user.userId)
        prefEdit.putString(USER_TOKEN, user.token)
        prefEdit.apply()
    }

    fun getLoginSession() : UserModel {
        val name = pref.getString(USER_NAME, null)
        val id = pref.getString(USER_ID, null)
        val token = pref.getString(USER_TOKEN, null)

        return UserModel(name, id, token)
    }

    fun clearLoginSession() {
        val prefClear = pref.edit().clear()
        prefClear.apply()
    }



    companion object {
        private const val USER_NAME = "user_name"
        private const val USER_ID = "user_id"
        private const val USER_TOKEN = "user_token"
        private const val USER_PREF = "user_pref"
    }
}