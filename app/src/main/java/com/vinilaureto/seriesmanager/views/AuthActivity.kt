package com.vinilaureto.seriesmanager.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.vinilaureto.seriesmanager.auth.AuthFirebase
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
        activityAuthBinding.loginBt.setOnClickListener {
            val email = activityAuthBinding.emailEt.text.toString()
            val password = activityAuthBinding.passwordEt.text.toString()
            AuthFirebase.firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                startMainActivity()
            }.addOnFailureListener {
                Toast.makeText(this, "Usuário e/ou senha incorretos", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (AuthFirebase.firebaseAuth.currentUser != null) {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}