package com.example.tolong.ui.nearby

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.example.tolong.R
import com.example.tolong.databinding.ActivityNearbyBinding
import com.example.tolong.helper.parseAddressLocation
import com.example.tolong.helper.parseAddressProvince
import com.example.tolong.helper.readJsonFromAssets
import com.example.tolong.model.SearchModel
import com.example.tolong.repository.ResultCondition
import com.example.tolong.ui.MainActivity
import com.example.tolong.ui.firstaid.CameraActivity
import com.example.tolong.ui.profile.ProfileActivity
import com.example.tolong.ui.setting.SettingActivity
import com.example.tolong.viewmodel.LoginViewModel
import com.example.tolong.viewmodel.NearbyViewModel
import com.example.tolong.viewmodel.ViewModelFactoryAuth
import com.example.tolong.viewmodel.ViewModelFactoryNearby
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class NearbyActivity : AppCompatActivity() {

    private val nearbyViewModel: NearbyViewModel by viewModels { factory }
    private lateinit var factory: ViewModelFactoryNearby
    private lateinit var binding: ActivityNearbyBinding
    private val sectionsPagerAdapter = SectionPagerAdapter(this)
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
        binding = ActivityNearbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactoryNearby.getInstanceNearby(binding.root.context)

        val lat = intent.getDoubleExtra(EXTRA_LAT, 0.0)
        val lon = intent.getDoubleExtra(EXTRA_LON, 0.0)
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        Log.d("NearbyActivity", "Lat: $lat dan Lon: $lon")
        binding.tvResultLokasi.text =
            parseAddressLocation(this, lat, lon)

        val province = parseAddressProvince(this, lat, lon)
        getData(province)
        bottomNav()
        sectionPage()
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
                    this,
                    "Gagal kamera.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getData(province: String) {
        val searchResultObserver = Observer<ResultCondition<SearchModel>> { result ->
            when (result) {
                is ResultCondition.SuccessState -> {
                    val searchModel = result.data
                    val ambulanceData = searchModel.ambulance
                    val policeData = searchModel.police
                    val firefighterData = searchModel.fireDep
                    Log.d("NearbyActivity", "province: $province, ambulanceData: $ambulanceData, policeData: $policeData. firefighterData: $firefighterData")
                    sectionsPagerAdapter.ambulance = ambulanceData
                    sectionsPagerAdapter.police = policeData
                    sectionsPagerAdapter.firefighter = firefighterData
                }
                else -> {
                    Log.d("NearbyActivity", "res: $result")
                }
            }
        }
        nearbyViewModel.search(province).observe(this, searchResultObserver)
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
        binding.bottomNavigationView.setSelectedItemId(R.id.nearby)
    }

    private fun sectionPage() {
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f
    }

    companion object {
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LON = "extra_lon"
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.menu_home,
            R.string.ambulance,
            R.string.police,
            R.string.firefighter,
        )

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}