package com.vinilaureto.seriesmanager.entities.Episode

import com.vinilaureto.seriesmanager.entities.Series.Series

interface EpisodeDAO {
    fun createEpisode(episode: Episode): Long
    fun findAllEpisodesBySeason(seasonId: String): MutableList<Episode>
    fun findSeriesByTitle(title: String): MutableList<Series>
    fun updateEpisode(episode: Episode): Int
    fun removeEpisode(episode: Episode): Int
    fun findOneEpisodeBySeasonId(seasonId: String, episodeNumber: Int): MutableList<Episode>
}