package com.vinilaureto.seriesmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SeasonEditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_season_editor)
    }

    fun cancelAction(view: View) {}
    fun saveAction(view: View) {}
}