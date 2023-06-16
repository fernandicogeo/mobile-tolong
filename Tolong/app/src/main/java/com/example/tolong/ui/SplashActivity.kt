package com.example.tolong.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tolong.R
import com.example.tolong.preferences.UserPreference

class SplashActivity : AppCompatActivity() {
    private lateinit var preference: UserPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        preference = UserPreference(this)

        isLoginSession()
    }

    private fun isLoginSession() {
        if (preference.getLoginSession().name != null && preference.getLoginSession().email != null && preference.getLoginSession().accessToken != null)
            startActivity(Intent(this, MainActivity::class.java))
        else startActivity(Intent(this, LoginActivity::class.java))
    }
}