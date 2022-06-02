package com.example.mptfood.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Parcelize
data class Order
    (
    @SerializedName("id")
        var ID: Int = 0,
    @SerializedName("clientId")
        var ClientID: Int = 0,
    @SerializedName("employeeId")
        var EmployeeID: Int = 0,
    @SerializedName("statusId")
        var StatusID: Int = 0,
    @SerializedName("date")
        var Date: String? = null,
    @SerializedName("total")
        var Total: Double = 0.0

    ) : Parcelable