package com.example.tolong.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginModel(
    @field:SerializedName("error")
    var error: String,

    @field:SerializedName("msg")
    var msg: String,

    @field:SerializedName("loginresult")
    var loginresult: UserLoginModel? = null

) : Parcelable