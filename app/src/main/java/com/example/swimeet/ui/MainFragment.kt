package com.example.swimeet.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swimeet.adapter.CompetitionsAdapter
import com.example.swimeet.adapter.EventsAdapter
import com.example.swimeet.adapter.ViewPagerAdapter
import com.example.swimeet.data.model.Advertisement
import com.example.swimeet.data.model.Competition
import com.example.swimeet.data.model.Event
import com.example.swimeet.databinding.FragmentMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {
        initListeners()
        initViewPager()
        initRecyclerViews()
    }

    private fun initRecyclerViews() {
        val competitionsAdapter = CompetitionsAdapter(competitionsProvider())
        val eventsAdapter = EventsAdapter(eventsProvider())

        binding.rvNextEvents.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = competitionsAdapter
        }

        binding.rvNextMeetings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventsAdapter
        }
    }

    private fun competitionsProvider(): List<Competition> {
        return listOf(
            Competition(type = "Travesía", name = "XI Travessía Canal de Nado Gandía", site = "Playa de Gandía", distance = 3.0),
            Competition(type = "Competición", name = "Campeonato Nacional Infantil de Verano", site = "Málaga"),
            Competition(type = "Competición", name = "5º Control Master", site = "Xátiva"),
            Competition(type = "Travesía", name = "II Travesía por parejas Burriana", site = "Puerto de Burriana", distance = 2.0),
            Competition(type = "Travesía", name = "IV Travessía Clara Morales", site = "Alboraya", distance = 1.5)
        )
    }

    private fun eventsProvider(): List<Event> {
        return listOf(
            Event(type = "Comida", name = "Cena Navidad 2025", site = "Kilauea"),
            Event(type = "Fiesta", name = "Sauvage", site = "Discoteca Sauvage"),
            Event(type = "Comida", name = "Comida post competición", site = "Benimamet"),
            Event(type = "Quedada", name = "Nadar en Cullera", site = "Cullera"),
            Event(type = "Quedada", name = "Nadar en el rio Antella", site = "Antella")
        )
    }

    private fun initViewPager() {
        val advertisementList = mutableListOf<Advertisement>()
        advertisementList.add(Advertisement(authorUsername = "Ivanpelu7", message = "Hola que tal", title = "Primer mensaje en el tablón"))
        advertisementList.add(Advertisement(authorUsername = "UsuarioInventado", message = "Que ganas tengo de irme a casa", title = "Segundo mensaje en el tablón"))

        val adapter = ViewPagerAdapter(advertisementList)
        binding.viewPager.adapter = adapter
    }

    private fun initListeners() {
        binding.buttonLogOut.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

}