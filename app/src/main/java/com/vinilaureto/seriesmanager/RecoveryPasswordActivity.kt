package com.vinilaureto.seriesmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vinilaureto.seriesmanager.databinding.ActivityAuthBinding
import com.vinilaureto.seriesmanager.databinding.ActivityRecoveryPasswordBinding

class RecoveryPasswordActivity : AppCompatActivity() {
    private lateinit var activityRecoveryPasswordBinding: ActivityRecoveryPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRecoveryPasswordBinding = ActivityRecoveryPasswordBinding.inflate(layoutInflater)
        setContentView(activityRecoveryPasswordBinding.root)
        supportActionBar?.subtitle = "Recuperar senha"
    }
}