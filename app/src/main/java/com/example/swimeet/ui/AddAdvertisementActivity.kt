package com.example.swimeet.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.swimeet.R
import com.example.swimeet.data.model.Advertisement
import com.example.swimeet.databinding.ActivityAddAdvertisementBinding
import com.example.swimeet.databinding.ActivityAddEventBinding
import com.example.swimeet.viewmodel.AddAdvertisementViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddAdvertisementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAdvertisementBinding
    private val addAdvViewModel: AddAdvertisementViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAdvertisementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        addAdvViewModel.advAdded.observe(this) {
            if (it) {
                Toast.makeText(applicationContext, "Mensaje publicado con éxito", Toast.LENGTH_SHORT).show()

                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()


            } else {
                binding.btnPublishMessage.text = "PUBLICANDO..."
            }
        }
    }

    private fun initListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnPublishMessage.setOnClickListener {
            if ((binding.etTitle.text.toString() != "") && (binding.etUbi.text.toString() != "")) {
                val title = binding.etTitle.text.toString()
                val message = binding.etUbi.text.toString()
                val adv = Firebase.auth.currentUser?.displayName?.let { it -> Advertisement(authorUsername = it, title = title, message = message) }
                if (adv != null) {
                    addAdvViewModel.addAdv(adv)
                }
            }
        }
    }
}