package com.vinilaureto.seriesmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vinilaureto.seriesmanager.adapter.SeriesAdapter
import com.vinilaureto.seriesmanager.controllers.SeriesController
import com.vinilaureto.seriesmanager.databinding.ActivityMainBinding
import com.vinilaureto.seriesmanager.entities.series.Series

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    private val seriesController: SeriesController by lazy {
        SeriesController(this)
    }

    // Data source
    private var seriesList: MutableList<Series> = mutableListOf()
    private fun prepareSeriesList() {
        seriesList = seriesController.findAllSeries()
    }

    // Adapter
    private val seriesAdapter: SeriesAdapter by lazy {
        SeriesAdapter(this, R.layout.layout_series, seriesList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        supportActionBar?.title = "SeriesManager"

        // Load data
        prepareSeriesList()
        activityMainBinding.seriesLv.adapter = seriesAdapter
    }
}