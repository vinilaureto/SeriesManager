package com.vinilaureto.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.vinilaureto.seriesmanager.database.Database
import com.vinilaureto.seriesmanager.databinding.ActivityEpisodeEditorBinding
import com.vinilaureto.seriesmanager.entities.Episode.Episode
import com.vinilaureto.seriesmanager.entities.Season.Season

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
            activityEpisodeEditorBinding.episodeTitleEt.setText(episode.title)
            activityEpisodeEditorBinding.episodeDurationEt.setText(episode.duration)
            activityEpisodeEditorBinding.episodeWatchedCb.isChecked = episode.watched
        }

        supportActionBar?.title = episode?.title ?: "Novo episódio"
        supportActionBar?.subtitle = "Detalhes do episódio"
    }

    fun saveAction(view: View) {
        val currentEpisode = intent.getParcelableExtra<Episode>(MainActivity.EXTRA_EPISODE)
        val editValue = currentEpisode != null

        if (validateForms(editValue)) {
            val episode = Episode(
                activityEpisodeEditorBinding.episodeNumberEt.text.toString().toInt(),
                activityEpisodeEditorBinding.episodeTitleEt.text.toString(),
                activityEpisodeEditorBinding.episodeDurationEt.text.toString(),
                activityEpisodeEditorBinding.episodeWatchedCb.isChecked
            )

            val season = intent.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)!!
            episode.seasonId = season.id

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
    }

    fun cancelAction(view: View) {
        finish()
    }

    fun validateForms(editValue: Boolean): Boolean {
        val season = intent.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)!!

        if (activityEpisodeEditorBinding.episodeNumberEt.text.toString() == "" ||
            activityEpisodeEditorBinding.episodeTitleEt.text.toString() == "" ||
            activityEpisodeEditorBinding.episodeDurationEt.text.toString() == "") {
            Snackbar.make(activityEpisodeEditorBinding.root, "Todos os campos devem ser preenchidos", Snackbar.LENGTH_SHORT).show()
            return false
        }

        val database = Database(this)
        val resultsInDatabase = if (editValue) 1 else 0
        println("=====================================")
        println(resultsInDatabase)
        if (database.findOneEpisodeBySeasonId(season.id, activityEpisodeEditorBinding.episodeNumberEt.text.toString().toInt()).count() != resultsInDatabase) {
            Snackbar.make(activityEpisodeEditorBinding.root, "Número do episódio já existe", Snackbar.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}