package com.example.tolong.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class UserEditModel (
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("alamat")
    val alamat: String? = null,

    @field:SerializedName("nomorhp")
    val nomorhp: String? = null,

    @field:SerializedName("password")
    val password: String? = null,
) : Parcelable