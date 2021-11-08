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
import com.vinilaureto.seriesmanager.adapter.SeriesAdapter
import com.vinilaureto.seriesmanager.controllers.SeriesController
import com.vinilaureto.seriesmanager.databinding.ActivityMainBinding
import com.vinilaureto.seriesmanager.entities.Series.Series

/*
* 1 - Todos os campos funcionando com os valores no banco
* 2 - Display de série e temporada
* 4 - Flag de assistido no botão OU ver se é possivel adicionar os generos
*/

class MainActivity : AppCompatActivity() {
    companion object Extras {
        const val EXTRA_SERIES = "EXTRA_SERIES"
        const val EXTRA_SERIES_POSITION = "EXTRA_SERIES_POSITION"
        const val EXTRA_SEASON = "EXTRA_SEASON"
        const val EXTRA_SEASON_POSITION = "EXTRA_SEASON_POSITION"
        const val EXTRA_EPISODE = "EXTRA_EPISODE"
        const val EXTRA_EPISODE_POSITION = "EXTRA_EPISODE_POSITION"
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val seriesPosition = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position
        val series = seriesList[seriesPosition]

        return when (item.itemId) {
            R.id.editItemMi -> {
                val editSeriesIntent = Intent(this, SeriesEditorActivity::class.java)
                editSeriesIntent.putExtra(EXTRA_SERIES, series)
                editSeriesIntent.putExtra(EXTRA_SERIES_POSITION, seriesPosition)
                editSeriesEditorActivityLauncher.launch(editSeriesIntent)
                true
            }
            R.id.removeItemMi -> {
                with(AlertDialog.Builder(this)) {
                    setMessage("Deseja apagar a série ${series.title}?")
                    setPositiveButton("Sim") {_, _ ->
                        seriesList.removeAt(seriesPosition)
                        seriesAdapter.notifyDataSetChanged()
                        seriesController.removeSeries(series)
                        Snackbar.make(activityMainBinding.root, "Série removida", Snackbar.LENGTH_SHORT).show()
                    }
                    setNegativeButton("Cancelar") {_,_ ->
                        Snackbar.make(activityMainBinding.root, "Operação cancelada", Snackbar.LENGTH_SHORT).show()
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