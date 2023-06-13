package com.example.tolong.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.tolong.R
import com.example.tolong.databinding.ActivityRegisterBinding
import com.example.tolong.repository.ResultCondition
import com.example.tolong.ui.customview.PasswordEditText
import com.example.tolong.ui.customview.RegisterButton
import com.example.tolong.viewmodel.RegisterViewModel
import com.example.tolong.viewmodel.ViewModelFactory
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val signupViewModel: RegisterViewModel by viewModels { factory }
    private lateinit var factory: ViewModelFactory

    private lateinit var titleTextView: TextView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var passwordTextView: TextView
    private lateinit var linkToLogin: TextView

    private lateinit var signupButton: RegisterButton
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var confirmPasswordEditText: PasswordEditText
    private lateinit var nameEditTextLayout: TextInputLayout
    private lateinit var emailEditTextLayout: TextInputLayout
    private lateinit var passwordEditTextLayout: TextInputLayout
    private lateinit var confirmPasswordEditTextLayout: TextInputLayout
    private lateinit var smallPasswordTextView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        factory = ViewModelFactory.getInstance(binding.root.context)

        setupView()
        setupAction()

        textChangedListener()
        setTextViewEnable()

        linkToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    private fun textChangedListener() {
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setTextViewEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setTextViewEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setTextViewEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        confirmPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setTextViewEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun setupAction() {
        showLoading(false)
        signupButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confPassword = confirmPasswordEditText.text.toString()
            when {
                name.isEmpty() -> {
                    emailEditTextLayout.error = "Masukkan nama"
                }
                email.isEmpty() -> {
                    emailEditTextLayout.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    passwordEditTextLayout.error = "Masukkan password"
                }
                confPassword.isEmpty() -> {
                    confirmPasswordEditTextLayout.error = "Masukkan konfirmasi password"
                }
                else -> {
                    signup(name, email, password, confPassword)
                }
            }
        }
    }

    private fun signup(userName: String, userEmail: String, userPassword: String, userConfPassword: String) {
        signupViewModel.signup(userName, userEmail, userPassword, userConfPassword).observe(this) {
            if (it != null) {
                when (it) {
                    is ResultCondition.LoadingState -> {
                        showLoading(true)
                    } is ResultCondition.ErrorState -> {
                        showLoading(false)
                        showDialog(false)
                    } is ResultCondition.SuccessState -> {
                        showLoading(false)
                        showDialog(true)
                    }
                }
            }
        }
    }

    private fun showDialog(isSuccess: Boolean) {
        if (isSuccess) {
            AlertDialog.Builder(this).apply {
                setTitle("Signup berhasil!!")
                setMessage("Anda sudah terdaftar di Story App! Silakan login.")
                setPositiveButton("Lanjut") { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle("Signup gagal!")
                setMessage("Format email anda salah/sudah terdaftar!")
                create()
                show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) progressBar.visibility = View.VISIBLE
        else progressBar.visibility = View.GONE
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

        titleTextView = findViewById(R.id.tv_register_title)
        nameTextView = findViewById(R.id.tv_name)
        emailTextView = findViewById(R.id.tv_email)
        passwordTextView = findViewById(R.id.tv_password)
        linkToLogin = findViewById(R.id.link_login)
        signupButton = findViewById(R.id.btn_register)
        nameEditText = findViewById(R.id.et_name)
        emailEditText = findViewById(R.id.et_email)
        passwordEditText = findViewById(R.id.et_password)
        confirmPasswordEditText = findViewById(R.id.et_confirm)
        nameEditTextLayout = findViewById(R.id.etl_name)
        emailEditTextLayout = findViewById(R.id.etl_email)
        passwordEditTextLayout = findViewById(R.id.etl_password)
        confirmPasswordEditTextLayout = findViewById(R.id.etl_confirm)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setTextViewEnable() {
        val resultPassword = passwordEditText.text
        signupButton.isEnabled = nameEditText.text.isNotEmpty() == true && emailEditText.text.isNotEmpty() == true && resultPassword.toString().length >= 8
//        smallPasswordTextView.alpha = if (resultPassword.toString().isEmpty() || resultPassword.toString().length >= 8) 0F else 1F
    }
}