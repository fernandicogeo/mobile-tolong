package com.example.tolong.ui.nearby

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.StringRes
import androidx.viewpager2.widget.ViewPager2
import com.example.tolong.R
import com.example.tolong.databinding.ActivityNearbyBinding
import com.example.tolong.helper.parseAddressLocation
import com.example.tolong.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class NearbyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNearbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lat = intent.getDoubleExtra(EXTRA_LAT, 0.0)
        val lon = intent.getDoubleExtra(EXTRA_LON, 0.0)

        Log.d("NearbyActivity", "Lat: $lat dan Lon: $lon")
        binding.tvResultLokasi.text =
            parseAddressLocation(this, lat, lon)

        sectionPage()
    }



    private fun sectionPage() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
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
            R.string.ambulance,
            R.string.police,
            R.string.firefighter,
        )
    }




}