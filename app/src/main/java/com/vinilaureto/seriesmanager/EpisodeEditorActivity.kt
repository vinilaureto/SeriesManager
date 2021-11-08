package com.vinilaureto.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.vinilaureto.seriesmanager.databinding.ActivityEpisodeEditorBinding
import com.vinilaureto.seriesmanager.entities.Episode.Episode
import com.vinilaureto.seriesmanager.entities.Season.Season
import com.vinilaureto.seriesmanager.entities.Series.Series

class EpisodeEditorActivity : AppCompatActivity() {
    private lateinit var activityEpisodeEditorBinding: ActivityEpisodeEditorBinding

    private var position = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEpisodeEditorBinding = ActivityEpisodeEditorBinding.inflate(layoutInflater)
        setContentView(activityEpisodeEditorBinding.root)

        val episode = intent.getParcelableExtra<Episode>(MainActivity.EXTRA_EPISODE)
        position = intent.getIntExtra(MainActivity.EXTRA_EPISODE_POSITION, -1)
        if (episode != null) {
            activityEpisodeEditorBinding.episodeNumberEt.setText(episode.number.toString())
            activityEpisodeEditorBinding.episodeTitleEt.setText(episode.title.toString())
        }
    }

    fun saveAction(view: View) {
        val season = intent.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)!!

        val episode = Episode(
            activityEpisodeEditorBinding.episodeNumberEt.text.toString().toInt(),
            activityEpisodeEditorBinding.episodeTitleEt.text.toString(),
            "",
            false
        )

        if (season != null) {
            episode.seasonId = season.id
        }

        val intentResult = Intent()
        intentResult.putExtra(MainActivity.EXTRA_EPISODE, episode)
        if (position != -1) {
            val currentEpisode = intent.getParcelableExtra<Episode>(MainActivity.EXTRA_EPISODE)!!
            episode.id = currentEpisode.id
            intentResult.putExtra(MainActivity.EXTRA_EPISODE_POSITION, position)
        }

        setResult(RESULT_OK, intentResult)
        finish()
    }

    fun cancelAction(view: View) {
        finish()
    }
}