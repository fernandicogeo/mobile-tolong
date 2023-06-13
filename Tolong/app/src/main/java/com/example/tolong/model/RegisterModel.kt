package com.example.tolong.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterModel(
    @field:SerializedName("error")
    val error: String,

    @field:SerializedName("msg")
    val message: String
) : Parcelable