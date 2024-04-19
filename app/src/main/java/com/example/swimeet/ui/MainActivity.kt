package com.example.swimeet.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.swimeet.R
import com.example.swimeet.databinding.ActivityMainBinding
import com.example.swimeet.util.FirebaseUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        initNavigation()
        loadAvatarImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ6qH7tD9ciKXsWO59t7OOqfgvnsa1ljgr5q1_lVof_JQ&s")
    }

    private fun loadAvatarImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .transform(CircleCrop())
            .into(binding.avatar)
    }

    private fun initNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        binding.bottomMenu.setupWithNavController(navController)

    }
}