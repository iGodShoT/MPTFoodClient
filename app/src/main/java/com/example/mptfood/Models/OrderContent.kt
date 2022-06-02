package com.example.mptfood.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderContent
    (
        @SerializedName("id")
        var ID : Int = 0,
        @SerializedName("orderId")
        var OrderID : Int = 0,
        @SerializedName("productId")
        var ProductID : Int = 0,
        @SerializedName("quantity")
        var Quantity : Int = 0

    ) : Parcelable