package com.example.swimeet.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.swimeet.R
import com.example.swimeet.databinding.ActivityCompetitionDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CompetitionDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityCompetitionDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompetitionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        setMap()
    }

    private fun setMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val ubi = LatLng(39.00955556293781, -0.1655901822236689)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubi, 13f))
        googleMap.addMarker(MarkerOptions().position(ubi))
    }
}