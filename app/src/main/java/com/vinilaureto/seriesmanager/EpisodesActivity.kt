package com.vinilaureto.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.vinilaureto.seriesmanager.adapter.EpisodeAdapter
import com.vinilaureto.seriesmanager.controllers.EpisodeController
import com.vinilaureto.seriesmanager.databinding.ActivityEpisodesBinding
import com.vinilaureto.seriesmanager.entities.episode.Episode
import com.vinilaureto.seriesmanager.entities.season.Season
import com.vinilaureto.seriesmanager.entities.series.Series

class EpisodesActivity : AppCompatActivity() {
    private lateinit var activityEpisodeBinding: ActivityEpisodesBinding
    private lateinit var addEpisodeEditorActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var editEpisodeEditorActivityLauncher: ActivityResultLauncher<Intent>

    private lateinit var season: Season
    private lateinit var series: Series
    private val episodeController: EpisodeController by lazy {
        EpisodeController(this)
    }

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
        activityEpisodeBinding = ActivityEpisodesBinding.inflate(layoutInflater)
        setContentView(activityEpisodeBinding.root)

        // Load data
        season = intent.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)!!
        series = intent.getParcelableExtra<Series>(MainActivity.EXTRA_SERIES)!!
        prepareEpisodeList(season.id)
        activityEpisodeBinding.episodeLv.adapter = episodeAdapter
        supportActionBar?.title = series.title
        supportActionBar?.subtitle = "Temporada ${season.number}"


        // Menu
        registerForContextMenu(activityEpisodeBinding.episodeLv)

        // Actions
        activityEpisodeBinding.newEpisodeBt.setOnClickListener {
            val addIntent = Intent(this, EpisodeEditorActivity::class.java)
            addIntent.putExtra(MainActivity.EXTRA_SEASON, season)
            addEpisodeEditorActivityLauncher.launch(addIntent)
        }

        // Launchers
        addEpisodeEditorActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val episode = result.data?.getParcelableExtra<Episode>(MainActivity.EXTRA_EPISODE)
                if (episode != null) {
                    episodeList.add(episode)
                    episodeController.newEpisode(episode)
                    episodeAdapter.notifyDataSetChanged()

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
}