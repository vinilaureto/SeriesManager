package com.vinilaureto.seriesmanager.entities.series

interface SeriesDAO {
    fun createSeries(series: Series): Long
    fun findAllSeries(): MutableList<Series>
    fun updateSeries(series: Series): Int
    fun removeSeries(series: Series): Int
}