package com.example.tolong.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    var name: String? = null,
    var email: String? = null,
    var accessToken: String? = null,
    var alamat: String? = null,
    var nomorhp: String? = null,
    var password: String? = null
) : Parcelable