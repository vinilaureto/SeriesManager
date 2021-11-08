package com.vinilaureto.seriesmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.vinilaureto.seriesmanager.databinding.LayoutSeasonBinding
import com.vinilaureto.seriesmanager.entities.season.Season

class SeasonAdapter (
    val localContext: Context,
    val layout: Int,
    val seasonsList: MutableList<Season>
): ArrayAdapter<Season>(localContext, layout, seasonsList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val seasonLayoutView: View
        if (convertView != null) {
            seasonLayoutView = convertView
        } else {
            val layoutSeasonBinding = LayoutSeasonBinding.inflate(localContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
            ) as LayoutInflater
            )
            with(layoutSeasonBinding) {
                root.tag = SeasonLayoutHolder(seasonNumberTv, seasonYearTv, seasonEpisodesTv)
                seasonLayoutView = layoutSeasonBinding.root
            }
        }

        val season = seasonsList[position]

        val seasonsLayoutHolder = seasonLayoutView.tag as SeasonLayoutHolder
        with(seasonsLayoutHolder) {
            numberTv.text = "Temporada " + season.number.toString()
            yearTv.text = "Ano: " + season.year.toString()
            episodesTv.text = "Episódios: " + season.episodes.toString()
        }

        return seasonLayoutView
    }

    private data class SeasonLayoutHolder(
        val numberTv: TextView,
        val yearTv: TextView,
        val episodesTv: TextView
    )
}