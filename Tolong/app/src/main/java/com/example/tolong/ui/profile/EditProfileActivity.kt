package com.example.tolong.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.tolong.R
import com.example.tolong.databinding.ActivityEditProfileBinding
import com.example.tolong.databinding.ActivityLoginBinding
import com.example.tolong.model.EditModel
import com.example.tolong.model.LoginModel
import com.example.tolong.model.UserModel
import com.example.tolong.preferences.UserPreference
import com.example.tolong.repository.ResultCondition
import com.example.tolong.ui.MainActivity
import com.example.tolong.viewmodel.EditViewModel
import com.example.tolong.viewmodel.LoginViewModel
import com.example.tolong.viewmodel.ViewModelFactoryAuth

class EditProfileActivity : AppCompatActivity() {
    private val editViewModel: EditViewModel by viewModels { factory }
    private lateinit var factory: ViewModelFactoryAuth
    private lateinit var preference: UserPreference
    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactoryAuth.getInstanceAuth(binding.root.context)
        preference = UserPreference(this)
        setupAction()
        textChangedListener()
        setTextViewEnable()
    }

    private fun edit(userName: String, userEmail: String, userAddress: String?, userNohp: String?, userPassword: String?) {
        editViewModel.edit(userName, userEmail, userAddress, userNohp, userPassword).observe(this) {
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

    private fun showDialog(isSuccess: Boolean) {
        if (isSuccess) {
            AlertDialog.Builder(this).apply {
                setTitle("Edit berhasil!!")
                setPositiveButton("Lanjut") { _, _ ->
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle("Edit gagal!")
                setMessage("Coba lagi!")
                create()
                show()
            }
        }
    }

    private fun saveLoginSession(login: EditModel) {
        val editPref = UserPreference(this)
        val editResult = login.editresult
        val user = UserModel(name = editResult?.name, email = editResult?.email, alamat = editResult?.alamat, nomorhp = editResult?.nomorhp, password = editResult?.password, accessToken = editPref.getLoginSession().accessToken)
        Log.d("EditProfileActivity", "user: $user")
        editPref.saveLoginSession(user)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    private fun textChangedListener() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setTextViewEnable()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
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
        val editPref = UserPreference(this)
        showLoading(false)

        binding.etName.setText(editPref.getLoginSession().name)
        binding.etEmail.setText(editPref.getLoginSession().email)
        binding.etAlamat.setText(editPref.getLoginSession().alamat)
        binding.etNohp.setText(editPref.getLoginSession().nomorhp)
        binding.etEmail.isEnabled = false
        val red = binding.etEmail.hintTextColors


        Log.d("EditProfileActivity", "red: $red")
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val alamat = binding.etAlamat.text.toString()
            val nomorhp = binding.etNohp.text.toString()
            val password = binding.etPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.etlName.error = "Masukkan nama"
                }
                else -> {
                    edit(name, editPref.getLoginSession().email!!, alamat, nomorhp, password)
                }
            }
        }
    }

    private fun setTextViewEnable() {
        val resultName = binding.etName.text
        val resultPassword = binding.etPassword.text
        if (resultPassword.toString().isNotEmpty()) binding.btnSave.isEnabled = resultName.toString().length >= 3 && resultPassword.toString().length >= 8
        else binding.btnSave.isEnabled = resultName.toString().length >= 3
    }
}