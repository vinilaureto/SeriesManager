package com.vinilaureto.seriesmanager.entities.Series

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Series (
    var userCode: String = "",
    var title: String? = null,
    var year: Int? = null,
    var channel: String? = null,
    var genre: String? = null,
    var seasons: Int = 0,
    var id: String = UUID.randomUUID().toString()
) : Parcelable