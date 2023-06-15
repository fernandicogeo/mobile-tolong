package com.example.tolong.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AmbulanceModel(
    @SerializedName("No")
    val number: String,

    @SerializedName("name_place")
    val name: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("call_number")
    val callNumber: String
) : Parcelable