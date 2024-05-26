package com.example.swimeet.ui


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.swimeet.R
import com.example.swimeet.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    companion object {
        const val REQUEST_CODE_UPDATE_PROFILE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE_PROFILE && resultCode == Activity.RESULT_OK) {
            val newPhotoUrl = data?.getStringExtra("newPhotoUrl")
            newPhotoUrl?.let {
                loadAvatarImage(it.toUri())
            }
        }
    }

    private fun initUI() {
        setListeners()
        initNavigation()
        loadAvatarImage(Firebase.auth.currentUser!!.photoUrl!!)
    }

    private fun setListeners() {
        binding.avatar.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadAvatarImage(imageUrl: Uri) {
        Glide.with(this)
            .load(imageUrl)
            .transform(CircleCrop())
            .into(binding.avatar)
    }

    private fun initNavigation() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        binding.bottomMenu.setupWithNavController(navController)
    }
}