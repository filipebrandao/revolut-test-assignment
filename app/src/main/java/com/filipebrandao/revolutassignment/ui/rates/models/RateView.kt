package com.filipebrandao.revolutassignment.ui.rates.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RateView(val shortName: String, val longName: String, val value: Double) : Parcelable
