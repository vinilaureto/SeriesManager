package com.vinilaureto.seriesmanager.entities.series

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Series (
    var title: String,
    var year: Int,
    var channel: String,
    var genre: String,
    var seasons: Int = 0,
    var id: String = UUID.randomUUID().toString()
) : Parcelable