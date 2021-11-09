package com.vinilaureto.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.vinilaureto.seriesmanager.database.Database
import com.vinilaureto.seriesmanager.databinding.ActivitySeasonEditorBinding
import com.vinilaureto.seriesmanager.entities.Episode.Episode
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
        val currentSeason = intent.getParcelableExtra<Season>(MainActivity.EXTRA_SEASON)
        val editValue = currentSeason != null

        if (validateForms(editValue)) {
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
    }
    fun cancelAction(view: View) {
        finish()
    }

    fun validateForms(editValue: Boolean): Boolean {
        val series = intent.getParcelableExtra<Series>(MainActivity.EXTRA_SERIES)!!

        if (activitySeasonEditorBinding.seasonNumberEt.text.toString() == "" ||
            activitySeasonEditorBinding.seasonYearEt.text.toString() == "") {
            Snackbar.make(activitySeasonEditorBinding.root, "Todos os campos devem ser preenchidos", Snackbar.LENGTH_SHORT).show()
            return false
        }

        val database = Database(this)
        val resultsInDatabase = if (editValue) 1 else 0
        if (database.findOneSeasonBySeriesId(series.id, activitySeasonEditorBinding.seasonNumberEt.text.toString().toInt()).count() != resultsInDatabase) {
            Snackbar.make(activitySeasonEditorBinding.root, "Número da temporada já existe", Snackbar.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}