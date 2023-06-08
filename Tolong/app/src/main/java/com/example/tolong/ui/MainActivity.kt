package com.example.tolong.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tolong.R
import com.example.tolong.databinding.ActivityLoginBinding
import com.example.tolong.databinding.ActivityMainBinding
import com.example.tolong.databinding.ActivityRegisterBinding
import com.example.tolong.ui.call.AmbulanceCallActivity
import com.example.tolong.ui.call.FirefighterCallActivity
import com.example.tolong.ui.call.PoliceCallActivity
import com.example.tolong.ui.firstaid.CameraActivity
import com.example.tolong.ui.firstaid.FirstAidActivity
import com.example.tolong.ui.nearby.NearbyActivity
import com.example.tolong.ui.profile.ProfileActivity
import com.example.tolong.ui.setting.SettingActivity
import com.example.tolong.viewmodel.RegisterViewModel
import com.example.tolong.viewmodel.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        callActivity()
        bottomNav()
    }

    fun callActivity() {
        binding.cardAmbulance.setOnClickListener {
            startActivity(Intent(this, AmbulanceCallActivity::class.java))
        }

        binding.cardPolice.setOnClickListener {
            startActivity(Intent(this, PoliceCallActivity::class.java))
        }

        binding.cardFirefighter.setOnClickListener {
            startActivity(Intent(this, FirefighterCallActivity::class.java))
        }
    }

    fun bottomNav() {
        binding.bottomNavigationView.setOnNavigationItemReselectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.first_aid -> {
                    if (!allPermissionsGranted()) {
                        ActivityCompat.requestPermissions(
                            this,
                            REQUIRED_PERMISSIONS,
                            REQUEST_CODE_PERMISSIONS
                        )
                    } else startActivity(Intent(this, CameraActivity::class.java))
                }
                R.id.nearby -> {
                    startActivity(Intent(this, NearbyActivity::class.java))
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                R.id.settings -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("RtlHardcoded")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this@MainActivity,
                    "Gagal kamera.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishAffinity()
            System.exit(0)
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}