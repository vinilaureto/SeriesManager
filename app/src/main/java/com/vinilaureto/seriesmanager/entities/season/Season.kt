package com.vinilaureto.seriesmanager.entities.season

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Season (
    var number: Int,
    var year: Int,
    var episodes: Int = 0,
    var seriesId: String = "",
    var id: String = UUID.randomUUID().toString()
) : Parcelable