package com.example.tolong.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class UserLoginModel (
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("accessToken")
    val accessToken: String,

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("alamat")
    val alamat: String,

    @field:SerializedName("nomorhp")
    val nomorhp: String,
) : Parcelable