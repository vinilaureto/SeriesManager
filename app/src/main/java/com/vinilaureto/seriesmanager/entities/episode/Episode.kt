package com.vinilaureto.seriesmanager.entities.Episode

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Episode (
    var number: Int = 0,
    var title: String = "",
    var duration: String = "",
    var watched: Boolean = false,
    var seasonId: String = "",
    var id: String = UUID.randomUUID().toString()
) : Parcelable