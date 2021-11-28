package com.vinilaureto.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vinilaureto.seriesmanager.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var activityAuthBinding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAuthBinding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(activityAuthBinding.root)
        supportActionBar?.subtitle = "Login do usuário"

        activityAuthBinding.createUserBt.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        activityAuthBinding.recoveryPasswordBt.setOnClickListener {
            startActivity(Intent(this, RecoveryPasswordActivity::class.java))
        }
    }
}