package com.vinilaureto.seriesmanager.controllers

import com.vinilaureto.seriesmanager.MainActivity
import com.vinilaureto.seriesmanager.database.Database
import com.vinilaureto.seriesmanager.entities.Series.Series

class SeriesController(mainActivity: MainActivity) {
    private val database = Database(mainActivity)

    fun newSeries(series: Series) = database.createSeries(series)
    fun findAllSeries() = database.findAllSeries()
    fun updateSeries(series: Series) = database.updateSeries(series)
    fun removeSeries(series: Series) = database.removeSeries(series)
}