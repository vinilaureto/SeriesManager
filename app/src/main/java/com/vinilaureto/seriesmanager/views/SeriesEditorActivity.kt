package com.vinilaureto.seriesmanager.views

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.vinilaureto.seriesmanager.R
import com.vinilaureto.seriesmanager.auth.AuthFirebase
import com.vinilaureto.seriesmanager.databinding.ActivitySeriesEditorBinding
import com.vinilaureto.seriesmanager.entities.Series.Series

class SeriesEditorActivity : AppCompatActivity() {
    private lateinit var activitySeriesEditorBinding: ActivitySeriesEditorBinding

    private var position = -1
    private lateinit var series: Series

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySeriesEditorBinding = ActivitySeriesEditorBinding.inflate(layoutInflater)
        setContentView(activitySeriesEditorBinding.root)

        val series = intent.getParcelableExtra<Series>(MainActivity.EXTRA_SERIES)
        position = intent.getIntExtra(MainActivity.EXTRA_SERIES_POSITION, -1)
        if (series != null) {
            activitySeriesEditorBinding.seriesNameEt.setText(series.title)
            activitySeriesEditorBinding.seriesYearEt.setText(series.year.toString())
            activitySeriesEditorBinding.seriesChannelEt.setText(series.channel)
            activitySeriesEditorBinding.seriesGenreSp.setSelection(spinnerValueAdapter(series.genre))
        }

        supportActionBar?.title = series?.title ?: "Nova série"
        supportActionBar?.subtitle = "Detalhes da série"
    }

    fun spinnerValueAdapter(value: String?): Int {
        val res: Resources = resources
        val spinnerValues = res.getStringArray(R.array.series_genres)
        for ((index, element) in spinnerValues.withIndex()) {
            if (element == value) return index
        }
        return 0
    }

    fun saveAction(view: View) {
        val currentSeries = intent.getParcelableExtra<Series>(MainActivity.EXTRA_SERIES)
        val editValue = currentSeries != null
        val currentUser = AuthFirebase.firebaseAuth.currentUser?.uid.toString()

        if (validateForms(editValue)) {
            val series = Series(
                currentUser,
                activitySeriesEditorBinding.seriesNameEt.text.toString(),
                activitySeriesEditorBinding.seriesYearEt.text.toString().toInt(),
                activitySeriesEditorBinding.seriesChannelEt.text.toString(),
                activitySeriesEditorBinding.seriesGenreSp.selectedItem.toString()
            )

            val intentResult = Intent()
            intentResult.putExtra(MainActivity.EXTRA_SERIES, series)
            if (position != -1) {
                val currentSeries = intent.getParcelableExtra<Series>(MainActivity.EXTRA_SERIES)
                if (currentSeries != null) {
                    series.id = currentSeries.id
                    series.seasons = currentSeries.seasons
                }
                intentResult.putExtra(MainActivity.EXTRA_SERIES_POSITION, position)
            }

            setResult(RESULT_OK, intentResult)
            finish()
        }
    }

    fun cancel(view: View) {
        finish()
    }

    fun validateForms(editValue: Boolean): Boolean {
        if (activitySeriesEditorBinding.seriesNameEt.text.toString() == "" ||
            activitySeriesEditorBinding.seriesYearEt.text.toString() == "" ||
            activitySeriesEditorBinding.seriesChannelEt.text.toString() == "") {
            Snackbar.make(activitySeriesEditorBinding.root, "Todos os campos devem ser preenchidos", Snackbar.LENGTH_SHORT).show()
            return false
        }

        val seriesList = intent.getParcelableArrayListExtra<Series>(MainActivity.EXTRA_SERIES_LIST)
        val resultsInDatabase = if (editValue) 1 else 0
        var resultsFound = 0
        seriesList.forEach {
            if (it.title == activitySeriesEditorBinding.seriesNameEt.text.toString()) {
                resultsFound++
            }
        }
        if (resultsFound > resultsInDatabase) {
            Snackbar.make(activitySeriesEditorBinding.root, "Título da séries já existe", Snackbar.LENGTH_SHORT).show()
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