package com.vinilaureto.seriesmanager.entities.Season

interface SeasonDAO {
    fun createSeason(season: Season): Long
    fun findSeasonsBySeriesId(seriesId: String): MutableList<Season>
    fun updateSeason(season: Season): Int
    fun removeSeason(season: Season): Int
    fun findOneSeasonBySeriesId(seriesId: String, seasonNumber: Int): MutableList<Season>
}