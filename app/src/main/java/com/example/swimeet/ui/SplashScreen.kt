package com.example.swimeet.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.swimeet.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.swimeet.util.FirebaseUtil

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        Firebase.firestore.clearPersistence()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun checkCurrentSesion() {
        if (Firebase.auth.currentUser != null) {
            FirebaseUtil.getCurrentUserDocumentRef()
                .get()
                .addOnSuccessListener {

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }


        } else {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        checkCurrentSesion()
    }
}