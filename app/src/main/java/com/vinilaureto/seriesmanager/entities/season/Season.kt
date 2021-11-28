package com.vinilaureto.seriesmanager.entities.Season

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Season (
    var number: Int = 0,
    var year: Int = 0,
    var episodes: Int = 0,
    var seriesId: String = "",
    var id: String = UUID.randomUUID().toString()
) : Parcelable