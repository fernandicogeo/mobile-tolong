package com.example.tolong.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginModel(
    @field:SerializedName("loginResult")
    var loginResult: UserLoginModel? = null,

    @field:SerializedName("error")
    var error: Boolean,

    @field:SerializedName("message")
    var message: String
) : Parcelable