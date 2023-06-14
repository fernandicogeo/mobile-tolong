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
import com.example.tolong.helper.readJsonFromAssets


class NearbyFragment : Fragment() {

    private lateinit var adapter: NearbyAdapter
    private lateinit var recyclerView: RecyclerView

    private var province: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            province = it.getString("key_province")
            Log.d("NearbyFragment", "PROVINCE: $province")
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nearby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        province?.let {
            getAmbulance(it)
            Log.d("NearbyFragment", "PROVINCE: $it")
        }
    }

    private fun getAmbulance(province: String) {
        val jsonFileName = when (province) {
            "Sumatera Selatan" -> "ambulance-sumsel.json"
            "Jawa" -> "jawa.json"
            else -> null
        }

        jsonFileName?.let {
            val jsonObject = readJsonFromAssets(requireContext(), it)
            if (jsonObject != null) {
                Log.d("NearbyFragment", "JSON OBJECT: $jsonObject")
                // Lakukan operasi dengan objek JSON di sini
                val name = jsonObject.getString("name_place")
                val city = jsonObject.getString("city")
                val call_number = jsonObject.getString("call_number")
                
//                setAmbulanceData(name, city, call_number)
            }
        }
    }

//    private fun setAmbulanceData(datas: Array<String>) {
//
//        val listTitle = ArrayList<String>()
//        val listAddress = ArrayList<String>()
//        val listNohp = ArrayList<String>()
//
//        for (data in datas) {
//            if (data.login != " ") {
//                listName.add(
//                    """
//                        ${data.login}
//                        """.trimIndent()
//                )
//            }
//        }
//        for (data in datas) {
//            if (data.avatarUrl != " ") {
//                listImg.add(
//                    """
//                        ${data.avatarUrl}
//                        """.trimIndent()
//                )
//            }
//        }
//
//        val layoutManager = LinearLayoutManager(context)
//        recyclerView = view?.findViewById(R.id.listFollow)!!
//        recyclerView.layoutManager = layoutManager
//        recyclerView.setHasFixedSize(true)
//        adapter = FollowAdapter(listName, listImg)
//        recyclerView.adapter = adapter
//    }

    companion object {
        fun newInstance(province: String): NearbyFragment {
            val fragment = NearbyFragment()
            val args = Bundle()
            args.putString("key_province", province)
            fragment.arguments = args
            return fragment
        }
    }


}