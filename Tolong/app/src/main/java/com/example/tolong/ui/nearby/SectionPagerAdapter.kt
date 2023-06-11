package com.example.tolong.ui.nearby

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = AmbulanceFragment()
            1 -> fragment = PoliceFragment()
            2 -> fragment = FirefighterFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 3
    }
}