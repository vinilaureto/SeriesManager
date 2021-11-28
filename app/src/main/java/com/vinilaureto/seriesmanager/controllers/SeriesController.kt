package com.vinilaureto.seriesmanager.controllers

import com.vinilaureto.seriesmanager.database.SeriesFirebaseDb
import com.vinilaureto.seriesmanager.entities.Series.Series

class SeriesController() {
    private val seriesFirebaseDb: SeriesFirebaseDb = SeriesFirebaseDb()

    fun newSeries(series: Series) = seriesFirebaseDb.createSeries(series)
    fun findAllSeries() = seriesFirebaseDb.findAllSeries()
    fun updateSeries(series: Series) = seriesFirebaseDb.updateSeries(series)
    fun removeSeries(series: Series) = seriesFirebaseDb.removeSeries(series)
}