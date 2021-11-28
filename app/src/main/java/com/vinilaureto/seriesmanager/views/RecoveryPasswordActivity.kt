package com.vinilaureto.seriesmanager.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.vinilaureto.seriesmanager.auth.AuthFirebase
import com.vinilaureto.seriesmanager.databinding.ActivityRecoveryPasswordBinding

class RecoveryPasswordActivity : AppCompatActivity() {
    private lateinit var activityRecoveryPasswordBinding: ActivityRecoveryPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRecoveryPasswordBinding = ActivityRecoveryPasswordBinding.inflate(layoutInflater)
        setContentView(activityRecoveryPasswordBinding.root)
        supportActionBar?.subtitle = "Recuperar senha"

        with(activityRecoveryPasswordBinding) {
            recoveryPasswordBt.setOnClickListener {
                val email = emailEt.text.toString()
                if (email.isNotEmpty()) {
                    AuthFirebase.firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                        Toast.makeText(this@RecoveryPasswordActivity, "E-mail de recuperação enviado com sucesso!", Toast.LENGTH_LONG).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this@RecoveryPasswordActivity, "Ops! Algum problema aconteceu", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@RecoveryPasswordActivity, "O campo de e-mail não pode ser vazio", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}