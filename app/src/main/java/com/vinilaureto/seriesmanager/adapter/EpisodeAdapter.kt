package com.vinilaureto.seriesmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.vinilaureto.seriesmanager.databinding.LayoutEpisodeBinding
import com.vinilaureto.seriesmanager.entities.episode.Episode

class EpisodeAdapter (
    val localContext: Context,
    val layout: Int,
    val episodeList: MutableList<Episode>
): ArrayAdapter<Episode>(localContext, layout, episodeList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val episodeLayoutView: View
        if (convertView != null) {
            episodeLayoutView = convertView
        } else {
            val layoutEpisodeBinding = LayoutEpisodeBinding.inflate(localContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
            ) as LayoutInflater
            )
            with(layoutEpisodeBinding) {
                root.tag = EpisodeLayoutHolder(episodeNumberNameTv, episodeDurationTv, episodeWatchedTv)
                episodeLayoutView = layoutEpisodeBinding.root
            }
        }

        val episode = episodeList[position]

        val episodeLayoutHolder = episodeLayoutView.tag as EpisodeLayoutHolder
        with(episodeLayoutHolder) {
            numberNameTv.text = "${episode.number.toString()} - ${episode.title.toString()}"
            durantionTv.text = "Duração: ${episode.duration}.toString()"
            watchedTv.text = "Assistido: " + episode.watched.toString()
        }

        return episodeLayoutView
    }

    private data class EpisodeLayoutHolder(
        val numberNameTv: TextView,
        val durantionTv: TextView,
        val watchedTv: TextView
    )
}