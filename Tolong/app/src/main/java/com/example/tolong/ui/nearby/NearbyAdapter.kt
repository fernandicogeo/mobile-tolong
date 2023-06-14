package com.example.tolong.ui.nearby

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.tolong.R

class NearbyAdapter(
    private val listTitle: ArrayList<String>,
    private val listAddress: ArrayList<String>,
    private val listNohp: ArrayList<String>,
) : RecyclerView.Adapter<NearbyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cvNearby: CardView = view.findViewById(R.id.cv_nearby)
        val tvTitle: TextView = view.findViewById(R.id.tv_item_title)
        val tvAddress: TextView = view.findViewById(R.id.tv_item_address)
        val tvNohp: TextView = view.findViewById(R.id.tv_item_nohp)

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
        holder.cvNearby.setOnClickListener {
            val phoneNumber = listNohp[position]
            val dialNumberIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            holder.itemView.context.startActivity(dialNumberIntent)
        }

    }
}