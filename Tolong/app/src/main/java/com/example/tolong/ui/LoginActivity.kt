package com.example.tolong.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.tolong.R
import com.example.tolong.databinding.ActivityLoginBinding
import com.example.tolong.model.LoginModel
import com.example.tolong.model.UserModel
import com.example.tolong.preferences.UserPreference
import com.example.tolong.repository.ResultCondition
import com.example.tolong.viewmodel.LoginViewModel
import com.example.tolong.viewmodel.ViewModelFactoryAuth
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels { factory }
    private lateinit var factory: ViewModelFactoryAuth
    private lateinit var preference: UserPreference
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactoryAuth.getInstanceAuth(binding.root.context)
        preference = UserPreference(this)

        setupView()
        setupAction()
        isLoginSession()

        binding.linkRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishAffinity()
            exitProcess(0)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun login(userEmail: String, userPassword: String) {
        loginViewModel.login(userEmail, userPassword).observe(this) {
            if (it != null) {
                when (it) {
                    is ResultCondition.LoadingState -> {
                        showLoading(true)
                    } is ResultCondition.ErrorState -> {
                        showLoading(false)
                        showDialog(false)
                    } is ResultCondition.SuccessState -> {
                        showLoading(false)
                        saveLoginSession(it.data)
                        showDialog(true)
                    }
                }
            }
        }
    }

    private fun isLoginSession() {
        if (preference.getLoginSession().name != null && preference.getLoginSession().email != null && preference.getLoginSession().accessToken != null)
            startActivity(Intent(this, MainActivity::class.java))
    }

    private fun showDialog(isSuccess: Boolean) {
        if (isSuccess) {
            AlertDialog.Builder(this).apply {
                setTitle("Login berhasil!!")
                setMessage("Selamat datang di Tolong!.")
                setPositiveButton("Lanjut") { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle("Login gagal!")
                setMessage("Email atau password anda salah!")
                create()
                show()
            }
        }
    }

    private fun saveLoginSession(login: LoginModel) {
        val loginPref = UserPreference(this)
        val loginResult = login.loginresult
        val user = UserModel(name = loginResult?.name, email = loginResult?.email, accessToken = loginResult?.accessToken, password = loginResult?.password, alamat = loginResult?.alamat, nomorhp = loginResult?.nomorhp)
        loginPref.saveLoginSession(user)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    private fun setupAction() {
        showLoading(false)
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.etlEmail.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.etlPassword.error = "Masukkan password"
                }
                else -> {
                    login(email, password)
                }
            }
        }
    }
}