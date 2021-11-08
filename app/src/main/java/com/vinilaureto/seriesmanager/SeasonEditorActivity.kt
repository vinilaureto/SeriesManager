package com.vinilaureto.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.vinilaureto.seriesmanager.databinding.ActivitySeasonEditorBinding
import com.vinilaureto.seriesmanager.databinding.ActivitySeasonsBinding
import com.vinilaureto.seriesmanager.databinding.ActivitySeriesEditorBinding
import com.vinilaureto.seriesmanager.entities.Season.Season
import com.vinilaureto.seriesmanager.entities.Series.Series

class SeasonEditorActivity : AppCompatActivity() {
    private lateinit var activitySeasonEditorBinding : ActivitySeasonEditorBinding

    private var position = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySeasonEditorBinding = ActivitySeasonEditorBinding.inflate(layoutInflater)
        setContentView(activitySeasonEditorBinding.root)

        val season = intent.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)
        position = intent.getIntExtra(MainActivity.EXTRA_SEASON_POSITION, -1)
        if (season != null) {
            activitySeasonEditorBinding.seasonNumberEt.setText(season.number.toString())
            activitySeasonEditorBinding.seasonYearEt.setText(season.year.toString())
        }
    }

    fun saveAction(view: View) {
        val series = intent.getParcelableExtra<Series>(MainActivity.EXTRA_SERIES)

        val season = Season(
            activitySeasonEditorBinding.seasonNumberEt.text.toString().toInt(),
            activitySeasonEditorBinding.seasonYearEt.text.toString().toInt()
        )

        if (series != null) {
            season.seriesId = series.id
        }

        val intentResult = Intent()
        intentResult.putExtra(MainActivity.EXTRA_SEASON, season)
        if (position != -1) {
            val currentSeason = intent.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)!!
            season.episodes = currentSeason.episodes
            season.id = currentSeason.id
            season.seriesId = currentSeason.seriesId
            intentResult.putExtra(MainActivity.EXTRA_SEASON_POSITION, position)
        }

        setResult(RESULT_OK, intentResult)
        finish()
    }
    fun cancelAction(view: View) {
        finish()
    }
}