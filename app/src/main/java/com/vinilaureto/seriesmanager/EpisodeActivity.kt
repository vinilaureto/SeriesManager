package com.vinilaureto.seriesmanager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
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
import com.vinilaureto.seriesmanager.adapter.EpisodeAdapter
import com.vinilaureto.seriesmanager.auth.AuthFirebase
import com.vinilaureto.seriesmanager.controllers.EpisodeController
import com.vinilaureto.seriesmanager.databinding.ActivityEpisodeBinding
import com.vinilaureto.seriesmanager.entities.Episode.Episode
import com.vinilaureto.seriesmanager.entities.Season.Season
import com.vinilaureto.seriesmanager.entities.Series.Series

class EpisodeActivity : AppCompatActivity() {
    private lateinit var activityEpisodeBinding: ActivityEpisodeBinding
    private lateinit var addEpisodeEditorActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var editEpisodeEditorActivityLauncher: ActivityResultLauncher<Intent>

    private lateinit var season: Season
    private lateinit var series: Series
    private val episodeController = EpisodeController()

    // Data source
    private var episodeList: MutableList<Episode> = mutableListOf()
    private fun prepareEpisodeList(seasonId: String) {
        episodeList = episodeController.findAllEpisodeBySeason(seasonId)
    }

    private val episodeAdapter: EpisodeAdapter by lazy {
        EpisodeAdapter(this, R.layout.layout_episode, episodeList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEpisodeBinding = ActivityEpisodeBinding.inflate(layoutInflater)
        setContentView(activityEpisodeBinding.root)

        // Load data
        season = intent.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)!!
        series = intent.getParcelableExtra<Series>(MainActivity.EXTRA_SERIES)!!
        //prepareEpisodeList(season.id)
        activityEpisodeBinding.episodeLv.adapter = episodeAdapter
        val getEpisodes = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void, Void, List<Episode>>() {
            override fun doInBackground(vararg p0: Void?): List<Episode> {
                Thread.sleep(3000)
                return episodeController.findAllEpisodeBySeason(season.id)
            }

            override fun onPreExecute() {
                super.onPreExecute()
                activityEpisodeBinding.loadingTv.visibility = View.VISIBLE
            }

            override fun onPostExecute(result: List<Episode>?) {
                super.onPostExecute(result)

                if (result != null) {
                    activityEpisodeBinding.loadingTv.visibility = View.GONE
                    episodeList.clear()
                    episodeList.addAll(result)
                    episodeAdapter.notifyDataSetChanged()
                }
            }
        }
        getEpisodes.execute()


        supportActionBar?.title = series.title
        supportActionBar?.subtitle = "Temporada ${season.number} - ${episodeList.count()} ${if (episodeList.count() != 1) "episódio" else "episódios"}"


        // Menu
        registerForContextMenu(activityEpisodeBinding.episodeLv)

        // Actions
        activityEpisodeBinding.newEpisodeBt.setOnClickListener {
            val addIntent = Intent(this, EpisodeEditorActivity::class.java)
            addIntent.putExtra(MainActivity.EXTRA_SEASON, season)
            addEpisodeEditorActivityLauncher.launch(addIntent)
        }

        activityEpisodeBinding.episodeLv.setOnItemClickListener{_,_, position, _ ->
            val episode = episodeList[position]
            val editEpisodeIntent = Intent(this, EpisodeEditorActivity::class.java)
            val season = intent.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)!!

            editEpisodeIntent.putExtra(MainActivity.EXTRA_EPISODE, episode)
            editEpisodeIntent.putExtra(MainActivity.EXTRA_EPISODE_POSITION, position)
            editEpisodeIntent.putExtra(MainActivity.EXTRA_SEASON, season)
            editEpisodeEditorActivityLauncher.launch(editEpisodeIntent)
        }

        // Launchers
        addEpisodeEditorActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val episode = result.data?.getParcelableExtra<Episode>(MainActivity.EXTRA_EPISODE)
                if (episode != null) {
                    episodeList.add(episode)
                    episodeController.newEpisode(episode)
                    episodeAdapter.notifyDataSetChanged()
                    supportActionBar?.subtitle = "Temporada ${season.number} - ${episodeList.count()} ${if (episodeList.count() != 1) "episódios" else "episódio"}"
                }
            }
        }

        editEpisodeEditorActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val position = result.data?.getIntExtra(MainActivity.EXTRA_EPISODE_POSITION, -1)
                result.data?.getParcelableExtra<Episode>(MainActivity.EXTRA_EPISODE)?.apply {
                    if (position != null && position != -1) {
                        episodeController.updateEpisode(this)
                        episodeList[position] = this
                        episodeAdapter.notifyDataSetChanged()
                        supportActionBar?.subtitle = "Temporada ${season.number} - ${episodeList.count()} ${if (episodeList.count() != 1) "episódio" else "episódios"}"
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
        val episodePosition = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position
        val episode = episodeList[episodePosition]

        return when (item.itemId) {
            R.id.editItemMi -> {
                val editEpisodeIntent = Intent(this, EpisodeEditorActivity::class.java)
                val season = intent.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)!!

                editEpisodeIntent.putExtra(MainActivity.EXTRA_EPISODE, episode)
                editEpisodeIntent.putExtra(MainActivity.EXTRA_EPISODE_POSITION, episodePosition)
                editEpisodeIntent.putExtra(MainActivity.EXTRA_SEASON, season)
                editEpisodeEditorActivityLauncher.launch(editEpisodeIntent)
                true
            }
            R.id.removeItemMi -> {
                with(AlertDialog.Builder(this)) {
                    setMessage("Deseja apagar o episódio ${episode.title}?")
                    setPositiveButton("Sim") {_, _ ->
                        episodeList.removeAt(episodePosition)
                        episodeAdapter.notifyDataSetChanged()
                        episodeController.removeEpisode(episode)
                        supportActionBar?.subtitle = "Temporada ${season.number} - ${episodeList.count()} ${if (episodeList.count() != 1) "episódio" else "episódios"}"
                        Snackbar.make(activityEpisodeBinding.root, "Episódio removido", Snackbar.LENGTH_SHORT).show()
                    }
                    setNegativeButton("Cancelar") {_,_ ->
                        Snackbar.make(activityEpisodeBinding.root, "Operação cancelada", Snackbar.LENGTH_SHORT).show()
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

    override fun onBackPressed() {
        val position = intent.getIntExtra(MainActivity.EXTRA_SEASON_POSITION, -1)
        val intentResult = Intent()
        season.episodes = episodeList.count()
        intentResult.putExtra(MainActivity.EXTRA_SEASON, season)
        intentResult.putExtra(MainActivity.EXTRA_SEASON_POSITION, position)
        setResult(RESULT_OK, intentResult)
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (AuthFirebase.firebaseAuth.currentUser == null) {
            finish()
        }
    }
}