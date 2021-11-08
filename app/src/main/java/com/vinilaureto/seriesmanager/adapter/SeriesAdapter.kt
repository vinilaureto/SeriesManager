package com.vinilaureto.seriesmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.vinilaureto.seriesmanager.databinding.LayoutSeriesBinding
import com.vinilaureto.seriesmanager.entities.series.Series

class SeriesAdapter (
    val localContext: Context,
    val layout: Int,
    val seriesList: MutableList<Series>
): ArrayAdapter<Series>(localContext, layout, seriesList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val seriesLayoutView: View
        if (convertView != null) {
            seriesLayoutView = convertView
        } else {
            val layoutSeriesBinding = LayoutSeriesBinding.inflate(localContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
            ) as LayoutInflater
            )
            with(layoutSeriesBinding) {
                root.tag = SeriesLayoutHolder(seriesTitleTv, seriesChannelTv, seriesSeasonsTv)
                seriesLayoutView = layoutSeriesBinding.root
            }
        }

        val series = seriesList[position]

        val seriesLayoutHolder = seriesLayoutView.tag as SeriesLayoutHolder
        with(seriesLayoutHolder) {
            titleTv.text = series.title
            channelTv.text = "Emissora: ${series.channel}"
            seasonsTv.text = "Temporadas: ${series.seasons.toString()}"
        }

        return seriesLayoutView
    }

    private data class SeriesLayoutHolder(
        val titleTv: TextView,
        val channelTv: TextView,
        val seasonsTv: TextView
    )
}