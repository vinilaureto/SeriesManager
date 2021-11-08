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

        // Actions
        activitySeasonsBinding.newSeasonBt.setOnClickListener {
            val addIntent = Intent(this, SeasonEditorActivity::class.java)
            addIntent.putExtra(MainActivity.EXTRA_SERIES, series)
            addSeasonEditorActivityLauncher.launch(addIntent)
        }

        activitySeasonsBinding.seasonLv.setOnItemClickListener{_,_, position, _ ->
            val season = seasonsList[position]
            val consultEpisodesIntent = Intent(this, EpisodesActivity::class.java)
            consultEpisodesIntent.putExtra(MainActivity.EXTRA_SEASON, season)
            consultEpisodesIntent.putExtra(MainActivity.EXTRA_SERIES, series)
            consultEpisodesIntent.putExtra(MainActivity.EXTRA_SEASON_POSITION, position)
            episodesActivityLauncher.launch(consultEpisodesIntent)
        }

        // Launchers
        addSeasonEditorActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val season = result.data?.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)
                if (season != null) {
                    seasonsList.add(season)
                    seasonController.newSeason(season)
                    seasonAdapter.notifyDataSetChanged()
                }
            }
        }

        editSeasonEditorActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val position = result.data?.getIntExtra(MainActivity.EXTRA_SEASON_POSITION, -1)
                result.data?.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)?.apply {
                    if (position != null && position != -1) {
                        seasonController.updateSeason(this)
                        seasonsList[position] = this
                        seasonAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        episodesActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val position = result.data?.getIntExtra(MainActivity.EXTRA_SEASON_POSITION, -1)
                result.data?.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)?.apply {
                    if (position != null && position != -1) {
                        seasonController.updateSeason(this)
                        seasonsList[position] = this
                        seasonAdapter.notifyDataSetChanged()
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val seasonPosition = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position
        val season = seasonsList[seasonPosition]

        return when (item.itemId) {
            R.id.editItemMi -> {
                val editSeasonIntent = Intent(this, SeasonEditorActivity::class.java)
                val series = intent.getParcelableExtra<Series>(MainActivity.EXTRA_SERIES)!!

                editSeasonIntent.putExtra(MainActivity.EXTRA_SEASON, season)
                editSeasonIntent.putExtra(MainActivity.EXTRA_SEASON_POSITION, seasonPosition)
                editSeasonIntent.putExtra(MainActivity.EXTRA_SERIES, series)
                editSeasonEditorActivityLauncher.launch(editSeasonIntent)
                true
            }
            R.id.removeItemMi -> {
                with(AlertDialog.Builder(this)) {
                    setMessage("Deseja apagar a temporada ${season.number}?")
                    setPositiveButton("Sim") {_, _ ->
                        seasonsList.removeAt(seasonPosition)
                        seasonAdapter.notifyDataSetChanged()
                        seasonController.removeSeason(season)
                        Snackbar.make(activitySeasonsBinding.root, "Temporada removida", Snackbar.LENGTH_SHORT).show()
                    }
                    setNegativeButton("Cancelar") {_,_ ->
                        Snackbar.make(activitySeasonsBinding.root, "Operação cancelada", Snackbar.LENGTH_SHORT).show()
                    }
                    create()
                }.show()
                true
            }
            else -> {
                false
            }
        }
    }
}