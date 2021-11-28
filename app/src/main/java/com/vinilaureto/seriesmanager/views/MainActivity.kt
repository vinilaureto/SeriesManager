package com.vinilaureto.seriesmanager.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.vinilaureto.seriesmanager.R
import com.vinilaureto.seriesmanager.adapter.SeriesAdapter
import com.vinilaureto.seriesmanager.auth.AuthFirebase
import com.vinilaureto.seriesmanager.controllers.SeriesController
import com.vinilaureto.seriesmanager.databinding.ActivityMainBinding
import com.vinilaureto.seriesmanager.entities.Series.Series

class MainActivity : AppCompatActivity() {
    companion object Extras {
        const val EXTRA_SERIES = "EXTRA_SERIES"
        const val EXTRA_SERIES_POSITION = "EXTRA_SERIES_POSITION"
        const val EXTRA_SERIES_LIST = "EXTRA_SERIES_LIST"
        const val EXTRA_SEASON = "EXTRA_SEASON"
        const val EXTRA_SEASON_POSITION = "EXTRA_SEASON_POSITION"
        const val EXTRA_SEASON_LIST = "EXTRA_SEASON_LIST"
        const val EXTRA_EPISODE = "EXTRA_EPISODE"
        const val EXTRA_EPISODE_POSITION = "EXTRA_EPISODE_POSITION"
        const val EXTRA_EPISODES_LIST = "EXTRA_EPISODES_LIST"
    }

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var addSeriesEditorActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var editSeriesEditorActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var seasonActivityLauncher: ActivityResultLauncher<Intent>

    private val seriesController = SeriesController()

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
        supportActionBar?.subtitle = "Lista de séries"


        // Load data
        activityMainBinding.seriesLv.adapter = seriesAdapter
        val getSeries = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void, Void, List<Series>>() {
            override fun doInBackground(vararg p0: Void?): List<Series> {
                Thread.sleep(3000)
                return seriesController.findAllSeries()
            }

            override fun onPreExecute() {
                super.onPreExecute()
                activityMainBinding.loadingTv.visibility = View.VISIBLE
            }

            override fun onPostExecute(result: List<Series>?) {
                super.onPostExecute(result)

                if (result != null) {
                    activityMainBinding.loadingTv.visibility = View.GONE
                    seriesList.clear()
                    seriesList.addAll(result)
                    seriesAdapter.notifyDataSetChanged()
                }
            }
        }
        getSeries.execute()

        // Menus
        registerForContextMenu(activityMainBinding.seriesLv)

        // Actions
        activityMainBinding.newSeriesBt.setOnClickListener {
            val addIntent = Intent(this, SeriesEditorActivity::class.java)
            addIntent.putParcelableArrayListExtra(EXTRA_SERIES_LIST, ArrayList(seriesList))
            addSeriesEditorActivityLauncher.launch(addIntent)
        }

        activityMainBinding.seriesLv.setOnItemClickListener{_,_, position, _ ->
            val series = seriesList[position]
            val consultSeriesIntent = Intent(this, SeasonActivity::class.java)
            consultSeriesIntent.putExtra(EXTRA_SERIES, series)
            consultSeriesIntent.putExtra(EXTRA_SERIES_POSITION, position)
            consultSeriesIntent.putParcelableArrayListExtra(EXTRA_SERIES_LIST, ArrayList(seriesList))
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
                        seriesController.updateSeries(this)
                        seriesList[position] = this
                        seriesAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (AuthFirebase.firebaseAuth.currentUser == null) {
            finish()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.logoutMi -> {
            AuthFirebase.firebaseAuth.signOut()
            finish()
            true
        }
        R.id.updateMi -> {
            seriesAdapter.notifyDataSetChanged()
            true
        }
        else -> { false }
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