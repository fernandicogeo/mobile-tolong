package com.example.tolong.ui.nearby

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tolong.model.AmbulanceModel
import com.example.tolong.model.FireDepModel
import com.example.tolong.model.PoliceModel
import com.example.tolong.ui.call.FirefighterCallActivity
import java.io.Serializable

class SectionPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    var ambulance: Map<String, AmbulanceModel> = emptyMap()
    var police: Map<String, PoliceModel> = emptyMap()
    var firefighter: Map<String, FireDepModel> = emptyMap()
    override fun createFragment(position: Int): Fragment {
        val fragment = NearbyFragment()
        fragment.arguments = Bundle().apply {
            putInt(NearbyFragment.ARG_POSITION, position)
            putSerializable(NearbyFragment.ARG_AMBULANCE, ambulance as Serializable)
            putSerializable(NearbyFragment.ARG_POLICE, police as Serializable)
            putSerializable(NearbyFragment.ARG_FIREFIGHTER, firefighter as Serializable)
        }
        return fragment
    }

    override fun getItemCount(): Int {
        return 3
    }
}