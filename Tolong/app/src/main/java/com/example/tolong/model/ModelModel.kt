package com.example.tolong.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelModel(
    @field:SerializedName("prediction")
    var prediction: String,
) : Parcelable