package com.vinilaureto.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.vinilaureto.seriesmanager.adapter.SeasonAdapter
import com.vinilaureto.seriesmanager.controllers.SeasonController
import com.vinilaureto.seriesmanager.databinding.ActivitySeasonBinding
import com.vinilaureto.seriesmanager.entities.season.Season
import com.vinilaureto.seriesmanager.entities.series.Series

class SeasonActivity : AppCompatActivity() {
    private lateinit var activitySeasonsBinding: ActivitySeasonBinding
    private lateinit var addSeasonEditorActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var editSeasonEditorActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var episodesActivityLauncher: ActivityResultLauncher<Intent>

    private lateinit var series: Series
    private val seasonController: SeasonController by lazy {
        SeasonController(this)
    }

    private var seasonsList: MutableList<Season> = mutableListOf()
    private fun prepareSeasonsList(seriesId: String) {
        seasonsList = seasonController.findAllSeasonsBySeries(seriesId)
    }

    private val seasonAdapter: SeasonAdapter by lazy {
        SeasonAdapter(this, R.layout.layout_season, seasonsList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySeasonsBinding = ActivitySeasonBinding.inflate(layoutInflater)
        setContentView(activitySeasonsBinding.root)

        // Load data
        series = intent.getParcelableExtra<Series>(MainActivity.EXTRA_SERIES)!!
        prepareSeasonsList(series.id)
        activitySeasonsBinding.seasonLv.adapter = seasonAdapter
        supportActionBar?.title = series.title

        // Menu
        registerForContextMenu(activitySeasonsBinding.seasonLv)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu_item, menu)
    }
}