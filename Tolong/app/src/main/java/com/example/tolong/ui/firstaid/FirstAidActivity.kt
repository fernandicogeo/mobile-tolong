package com.example.tolong.ui.firstaid

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.tolong.R
import com.example.tolong.databinding.ActivityFirstAidBinding
import com.example.tolong.helper.rotateBitmap
import com.example.tolong.ui.MainActivity
import com.example.tolong.ui.nearby.NearbyActivity
import com.example.tolong.ui.profile.ProfileActivity
import com.example.tolong.ui.setting.SettingActivity
import com.example.tolong.viewmodel.ViewModelFactoryAuth
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FirstAidActivity : AppCompatActivity() {
    private lateinit var factory: ViewModelFactoryAuth
    private lateinit var binding: ActivityFirstAidBinding
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
        binding = ActivityFirstAidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myFile = reduceFileImage(intent?.getSerializableExtra(EXTRA_PHOTO_RESULT) as File)
        val isBackCamera = intent?.getBooleanExtra(EXTRA_CAMERA_MODE, true) as Boolean
        val rotatedBitmap = rotateBitmap(
            BitmapFactory.decodeFile(myFile.path),
            isBackCamera
        )
        binding.ivGambarLuka.setImageBitmap(rotatedBitmap)
        bottomNav()

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }
    fun bottomNav() {
        binding.bottomNavigationView.setOnNavigationItemReselectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.first_aid -> {
                    startActivity(Intent(this, CameraActivity::class.java))
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
        binding.bottomNavigationView.setSelectedItemId(R.id.first_aid)
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
                    val intentNearby = Intent(this, NearbyActivity::class.java)

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

    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
    }
}