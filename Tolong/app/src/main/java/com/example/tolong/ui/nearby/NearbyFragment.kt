package com.example.tolong.ui.nearby

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tolong.R
import com.example.tolong.databinding.FragmentNearbyBinding
import com.example.tolong.helper.readJsonFromAssets
import com.example.tolong.model.AmbulanceModel
import com.example.tolong.model.FireDepModel
import com.example.tolong.model.PoliceModel


class NearbyFragment : Fragment() {

    private lateinit var adapter: NearbyAdapter
    private lateinit var recyclerView: RecyclerView
    private var _binding: FragmentNearbyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNearbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ambulance = arguments?.getSerializable(ARG_AMBULANCE) as Map<String, AmbulanceModel>
        police = arguments?.getSerializable(ARG_POLICE) as Map<String, PoliceModel>
        firefighter = arguments?.getSerializable(ARG_FIREFIGHTER) as Map<String, FireDepModel>

        val ambulanceList: List<AmbulanceModel> = ambulance.values.toList()
        val policeList: List<PoliceModel> = police.values.toList()
        val firefighterList: List<FireDepModel> = firefighter.values.toList()

        arguments?.let {
            position = it.getInt(ARG_POSITION)
        }

        when (position) {
            1 -> setAmbulanceData(ambulanceList)
            2 -> setPoliceData(policeList)
            3 -> setFirefighterData(firefighterList)
        }
    }

    private fun setAmbulanceData(datas: List<AmbulanceModel>) {
        val listName = ArrayList<String>()
        val listAddress = ArrayList<String>()
        val listNohp = ArrayList<String>()

        for (data in datas) {
            if (data.name != " ") {
                listName.add(
                    """
                   ${data.name}
                    """.trimIndent()
                )
            }
        }
        for (data in datas) {
            if (data.location != " ") {
                listAddress.add(
                    """
                    ${data.location}
                    """.trimIndent()
                )
            }
        }
        for (data in datas) {
            if (data.callNumber != " ") {
                listNohp.add(
                    """
                    ${data.callNumber}
                    """.trimIndent()
                )
            }
        }
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view?.findViewById(R.id.rv_nearby)!!
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = NearbyAdapter(listName, listAddress, listNohp)
        recyclerView.adapter = adapter
    }

    private fun setPoliceData(datas: List<PoliceModel>) {
        val listName = ArrayList<String>()
        val listAddress = ArrayList<String>()
        val listNohp = ArrayList<String>()

        for (data in datas) {
            if (data.city != " ") {
                listName.add(
                    """
                            ${data.city}
                            """.trimIndent()
                )
            }
        }
        for (data in datas) {
            if (data.location != " ") {
                listAddress.add(
                    """
                            ${data.location}
                            """.trimIndent()
                )
            }
        }
        for (data in datas) {
            if (data.callNumber != " ") {
                listNohp.add(
                    """
                        ${data.callNumber}
                        """.trimIndent()
                )
            }
        }

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view?.findViewById(R.id.rv_nearby)!!
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = NearbyAdapter(listName, listAddress, listNohp)
        recyclerView.adapter = adapter
    }

    private fun setFirefighterData(datas: List<FireDepModel>) {
        val listName = ArrayList<String>()
        val listAddress = ArrayList<String>()
        val listNohp = ArrayList<String>()

        for (data in datas) {
            if (data.city != " ") {
                listName.add(
                    """
                            ${data.city}
                            """.trimIndent()
                )
            }
        }
        for (data in datas) {
            if (data.location != " ") {
                listAddress.add(
                    """
                            ${data.location}
                            """.trimIndent()
                )
            }
        }
        for (data in datas) {
            if (data.callNumber != " ") {
                listNohp.add(
                    """
                        ${data.callNumber}
                        """.trimIndent()
                )
            }
        }

        val layoutManager = LinearLayoutManager(context)
        recyclerView = view?.findViewById(R.id.rv_nearby)!!
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = NearbyAdapter(listName, listAddress, listNohp)
        recyclerView.adapter = adapter
    }

    companion object {
        const val ARG_POSITION = "arg_position"
        const val ARG_AMBULANCE = "arg_ambulance"
        const val ARG_POLICE = "arg_police"
        const val ARG_FIREFIGHTER = "arg_firefighter"
        var position: Int = 0
        var ambulance: Map<String, AmbulanceModel> = emptyMap()
        var police: Map<String, PoliceModel> = emptyMap()
        var firefighter: Map<String, FireDepModel> = emptyMap()
    }
}




