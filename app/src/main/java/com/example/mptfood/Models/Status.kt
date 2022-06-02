package com.example.mptfood.Models
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Status
    (
        var ID : Int = 0,
        var Name: String? = null
    ) : Parcelable