package com.example.tolong.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tolong.R
import com.example.tolong.databinding.ActivityMainBinding
import com.example.tolong.ui.call.AmbulanceCallActivity
import com.example.tolong.ui.call.FirefighterCallActivity
import com.example.tolong.ui.call.PoliceCallActivity
import com.example.tolong.ui.firstaid.CameraActivity
import com.example.tolong.ui.nearby.NearbyActivity
import com.example.tolong.ui.profile.ProfileActivity
import com.example.tolong.ui.setting.SettingActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocation: FusedLocationProviderClient

    private var lat: Double? = 0.0
    private var lon: Double? = 0.0

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
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
                    getMyLocation()
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

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocation.lastLocation.addOnSuccessListener {
                if (it != null) {
                    lat = it.latitude
                    lon = it.longitude
                    val intentNearby = Intent(this@MainActivity, NearbyActivity::class.java)

                    intentNearby.putExtra(NearbyActivity.EXTRA_LAT, lat)
                    intentNearby.putExtra(NearbyActivity.EXTRA_LON, lon)

                    Log.d("MainActivity", "Lat: $lat dan Lon: $lon")
                    startActivity(intentNearby)

                } else {
                    showDialog("2")
                }
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showDialog(isSuccess: String) {
        if (isSuccess == "2") {
            AlertDialog.Builder(this).apply {
                setTitle("Lokasi tidak ditemukan!!")
                setMessage("Silakan coba lagi.")
                create()
                show()
            }
        } else if (isSuccess == "3") {
            AlertDialog.Builder(this).apply {
                setTitle("Upload story gagal!!")
                setMessage("Silakan coba lagi.")
                create()
                show()
            }
        } else if (isSuccess == "4") {
            AlertDialog.Builder(this).apply {
                setTitle("Permission tidak diberikan!!")
                setMessage("Anda harus memberikan permission untuk menggunakan fitur ini.")
                create()
                show()
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