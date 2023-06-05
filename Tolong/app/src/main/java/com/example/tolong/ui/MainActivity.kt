package com.example.tolong.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import com.example.tolong.R
import com.example.tolong.databinding.ActivityRegisterBinding
import com.example.tolong.viewmodel.RegisterViewModel
import com.example.tolong.viewmodel.ViewModelFactory
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}