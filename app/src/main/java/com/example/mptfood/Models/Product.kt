package com.example.mptfood.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Product(
    @SerializedName("id")
    val ID : Int,
    @SerializedName("name")
    val Name: String,
    @SerializedName("price")
    val Price : Int,
    @SerializedName("quantityAvailable")
    val QuantityAvailable : Int,
    @SerializedName("image")
    val Image : String
) : Parcelable