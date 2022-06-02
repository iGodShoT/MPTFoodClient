package com.example.mptfood.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Client
    (
        @SerializedName("id")
        var ID: Int? = 0,
        @SerializedName("surname")
        var Surname: String? = null,
        @SerializedName("name")
        var Name: String? = null,
        @SerializedName("email")
        var Email: String? = null,

    ) : Parcelable