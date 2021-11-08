package com.vinilaureto.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.vinilaureto.seriesmanager.databinding.ActivitySeriesEditorBinding
import com.vinilaureto.seriesmanager.entities.series.Series

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
        }
    }

    fun saveAction(view: View) {
        val series = Series(
            activitySeriesEditorBinding.seriesNameEt.text.toString(),
            activitySeriesEditorBinding.seriesYearEt.text.toString().toInt(),
            activitySeriesEditorBinding.seriesChannelEt.text.toString(),
            ""
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

    fun cancelAction(view: View) {
        finish()
    }
}