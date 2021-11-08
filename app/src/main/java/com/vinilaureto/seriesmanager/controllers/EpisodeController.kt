package com.vinilaureto.seriesmanager.controllers

import com.vinilaureto.seriesmanager.EpisodesActivity
import com.vinilaureto.seriesmanager.database.Database
import com.vinilaureto.seriesmanager.entities.episode.Episode

class EpisodeController(episodeActivity: EpisodesActivity) {
    private val database = Database(episodeActivity)

    fun newEpisode(episode: Episode) = database.createEpisode(episode)
    fun findAllEpisodeBySeason(seriesId: String) = database.findAllEpisodesBySeason(seriesId)
    fun updateEpisode(episode: Episode) = database.updateEpisode(episode)
    fun removeEpisode(episode: Episode) = database.removeEpisode(episode)
}