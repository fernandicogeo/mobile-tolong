package com.example.tolong.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchModel(
    @field:SerializedName("Ambulance")
    var ambulance: Map<String, AmbulanceModel>,

    @field:SerializedName("Police")
    var police: Map<String, PoliceModel>,

    @field:SerializedName("FireDep")
    var fireDep: Map<String, FireDepModel>
) : Parcelable