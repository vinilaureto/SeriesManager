package com.vinilaureto.seriesmanager.controllers

import com.vinilaureto.seriesmanager.database.SeasonFirebaseDb
import com.vinilaureto.seriesmanager.entities.Season.Season

class SeasonController() {
    private val database = SeasonFirebaseDb()

    fun newSeason(season: Season) = database.createSeason(season)
    fun findAllSeasonsBySeries(seriesId: String) = database.findSeasonsBySeriesId(seriesId)
    fun updateSeason(season: Season) = database.updateSeason(season)
    fun removeSeason(season: Season) = database.removeSeason(season)
}