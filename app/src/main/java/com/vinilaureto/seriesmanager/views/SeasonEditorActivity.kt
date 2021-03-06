package com.vinilaureto.seriesmanager.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.vinilaureto.seriesmanager.auth.AuthFirebase
import com.vinilaureto.seriesmanager.databinding.ActivitySeasonEditorBinding
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

        supportActionBar?.title = if (season != null) "Temporada ${season.number}" else "Nova temporada"
        supportActionBar?.subtitle = "Detalhes da temporada"
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

        val seasonList = intent.getParcelableArrayListExtra<Season>(MainActivity.EXTRA_SEASON_LIST)
        val resultsInDatabase = if (editValue) 1 else 0
        var resultsFound = 0
        seasonList.forEach {
            if (it.number.toString() == activitySeasonEditorBinding.seasonNumberEt.text.toString()) {
                resultsFound++
            }
        }
        if (resultsFound > resultsInDatabase) {
            Snackbar.make(activitySeasonEditorBinding.root, "N??mero da temporada j?? existe", Snackbar.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onStart() {
        super.onStart()
        if (AuthFirebase.firebaseAuth.currentUser == null) {
            finish()
        }
    }
}