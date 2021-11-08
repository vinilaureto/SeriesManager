package com.vinilaureto.seriesmanager.entities.episode

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Episode (
    var number: Int,
    var title: String = "",
    var duration: String,
    var watched: Boolean,
    var seasonId: String = "",
    var id: String = UUID.randomUUID().toString()
) : Parcelable