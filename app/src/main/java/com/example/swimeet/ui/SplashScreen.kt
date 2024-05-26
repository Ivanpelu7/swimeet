package com.example.swimeet.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.swimeet.R
import com.example.swimeet.util.FirebaseUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        // Cambiar al modo claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)



        Firebase.firestore.clearPersistence()
        checkCurrentSesion()

    }

    private fun checkCurrentSesion(): Boolean {
        var check: Boolean = false

        if (Firebase.auth.currentUser != null) {
            FirebaseUtil.getCurrentUserDocumentRef()
                .get()
                .addOnSuccessListener {
                    check = true
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }


        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return check
    }
}