package com.vinilaureto.seriesmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.vinilaureto.seriesmanager.auth.AuthFirebase
import com.vinilaureto.seriesmanager.databinding.ActivityRecoveryPasswordBinding
import com.vinilaureto.seriesmanager.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var activitySignupdBinding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignupdBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(activitySignupdBinding.root)
        supportActionBar?.subtitle = "Cadastro do usuário"

        with(activitySignupdBinding) {
            signupBt.setOnClickListener {
                val email = emailEt.text.toString()
                val password = passwordEt.text.toString()
                val checkPassword = checkPasswordEt.text.toString()
                if (password == checkPassword) {
                    AuthFirebase.firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                        Toast.makeText(this@SignupActivity, "Usuário $email cadastrado com sucesso", Toast.LENGTH_LONG).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this@SignupActivity, "Falha no cadastro", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@SignupActivity, "Senha e confirmação de senha devem ser iguais", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}