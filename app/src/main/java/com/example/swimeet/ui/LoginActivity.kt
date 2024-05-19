package com.example.swimeet.ui

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.swimeet.R
import com.example.swimeet.databinding.ActivityLoginBinding
import com.example.swimeet.util.FirebaseUtil
import com.example.swimeet.viewmodel.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val registerViewModel: RegisterViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkCurrentSesion()
        initUI()
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        registerViewModel.isLoginCorrect.observe(this) { isLoginCorrect ->
            if (isLoginCorrect) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()

            } else {
                Toast.makeText(
                    baseContext,
                    "Datos introducidos incorrectos",
                    Toast.LENGTH_SHORT,
                ).show()

                binding.btnLogin.text = "ENTRAR"

            }
        }

        registerViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.btnLogin.text = "ENTRANDO..."
            }
        }
    }

    private fun initListeners() {
        binding.tvRegistrate.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            if ((binding.etLoginEmail.text.toString() != "") && (binding.etLoginPassword.text.toString() != "")) {
                val email = binding.etLoginEmail.text.toString()
                val password = binding.etLoginPassword.text.toString()

                registerViewModel.signIn(email, password)
            } else {
                Toast.makeText(
                    baseContext,
                    "Rellene todos los campos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

    }

    private fun checkCurrentSesion() {
        if (Firebase.auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}