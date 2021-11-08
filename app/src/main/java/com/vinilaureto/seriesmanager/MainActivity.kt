package com.vinilaureto.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.vinilaureto.seriesmanager.adapter.SeriesAdapter
import com.vinilaureto.seriesmanager.controllers.SeriesController
import com.vinilaureto.seriesmanager.databinding.ActivityMainBinding
import com.vinilaureto.seriesmanager.entities.series.Series

class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_SERIES = "EXTRA_SERIES"
        const val EXTRA_SERIES_POSITION = "EXTRA_SERIES_POSITION"
    }

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var addSeriesEditorActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var editSeriesEditorActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var seasonActivityLauncher: ActivityResultLauncher<Intent>

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

        // Menus
        registerForContextMenu(activityMainBinding.seriesLv)

        // Actions
        activityMainBinding.newSeriesBt.setOnClickListener {
            addSeriesEditorActivityLauncher.launch(Intent(this, SeriesEditorActivity::class.java))
        }

        activityMainBinding.seriesLv.setOnItemClickListener{_,_, position, _ ->
            val series = seriesList[position]
            val consultSeriesIntent = Intent(this, SeasonActivity::class.java)
            consultSeriesIntent.putExtra(EXTRA_SERIES, series)
            consultSeriesIntent.putExtra(EXTRA_SERIES_POSITION, position)
            seasonActivityLauncher.launch(consultSeriesIntent)
        }

        // Launchers
        addSeriesEditorActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val series = result.data?.getParcelableExtra<Series>(EXTRA_SERIES)
                if (series != null) {
                    seriesList.add(series)
                    seriesController.newSeries(series)
                    seriesAdapter.notifyDataSetChanged()
                }
            }
        }

        editSeriesEditorActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val position = result.data?.getIntExtra(EXTRA_SERIES_POSITION, -1)
                result.data?.getParcelableExtra<Series>(EXTRA_SERIES)?.apply {
                    if (position != null && position != -1) {
                        seriesController.updateSeries(this)
                        seriesList[position] = this
                        seriesAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        seasonActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val position = result.data?.getIntExtra(EXTRA_SERIES_POSITION, -1)
                result.data?.getParcelableExtra<Series>(EXTRA_SERIES)?.apply {
                    if (position != null && position != -1) {
                        println(this)
                        seriesController.updateSeries(this)
                        seriesList[position] = this
                        seriesAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu_item, menu)
    }

    // implementar as atividades do menu
}