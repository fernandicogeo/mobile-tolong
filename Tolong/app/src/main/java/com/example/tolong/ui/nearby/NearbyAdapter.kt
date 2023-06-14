package com.example.tolong.ui.nearby

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tolong.R

class NearbyAdapter(
    private val listTitle: ArrayList<String>,
    private val listAddress: ArrayList<String>,
    private val listNohp: ArrayList<String>,
) : RecyclerView.Adapter<NearbyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_item_title)
        val tvAddress: TextView = view.findViewById(R.id.tv_item_address)
        val tvNohp: TextView = view.findViewById(R.id.tv_item_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_nearby, parent, false)
        )

    override fun getItemCount(): Int = listTitle.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (listTitle[position] != "" && listAddress[position] != "" && listNohp[position] != "") {
            holder.tvTitle.text = listTitle[position]
            holder.tvAddress.text = listAddress[position]
            holder.tvNohp.text = listNohp[position]
        }
    }
}