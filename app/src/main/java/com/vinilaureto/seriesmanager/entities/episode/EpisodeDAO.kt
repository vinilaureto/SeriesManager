package com.vinilaureto.seriesmanager.entities.Episode

interface EpisodeDAO {
    fun createEpisode(episode: Episode): Long
    fun findAllEpisodesBySeason(seasonId: String): MutableList<Episode>
    fun updateEpisode(episode: Episode): Int
    fun removeEpisode(episode: Episode): Int
    fun findOneEpisodeBySeasonId(seasonId: String, episodeNumber: Int): MutableList<Episode>
}