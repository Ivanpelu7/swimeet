package com.example.swimeet.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.swimeet.R
import com.example.swimeet.databinding.ActivityCompetitionDetailBinding
import com.example.swimeet.viewmodel.CompetitionDetailViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CompetitionDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityCompetitionDetailBinding
    private val compDetailViewModel = CompetitionDetailViewModel()
    private lateinit var id: String
    private lateinit var name: String
    private lateinit var longitude: String
    private lateinit var latitude: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompetitionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        initListeners()
        getIntents()
        binding.tvCompetitionName.text = name
        compDetailViewModel.init(id)
        initObservers()
        setMap()
    }

    private fun initListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getIntents() {
        id = intent.getStringExtra("id").toString()
        name = intent.getStringExtra("name").toString()
        longitude = intent.getStringExtra("longitude").toString()
        latitude = intent.getStringExtra("latitude").toString()
    }

    private fun initObservers() {
        compDetailViewModel.competition.observe(this) { competition ->
            if (competition != null) {
                binding.constraintLayout.visibility = View.VISIBLE
                binding.prgbar.visibility = View.INVISIBLE
                binding.tvCompetitionName.text = competition.name
                binding.tvTitle.text = competition.name

            } else {
                binding.constraintLayout.visibility = View.INVISIBLE
                binding.prgbar.visibility = View.VISIBLE
            }
        }
    }

    private fun setMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val ubi = LatLng(latitude.toDouble(), longitude.toDouble())

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubi, 15f))
        googleMap.addMarker(MarkerOptions().position(ubi))
    }
}