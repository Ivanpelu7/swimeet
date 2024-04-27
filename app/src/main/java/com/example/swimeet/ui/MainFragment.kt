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
import com.example.swimeet.viewmodel.MainViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel = MainViewModel()
    private lateinit var competitionsAdapter: CompetitionsAdapter
    private lateinit var eventsAdapter: EventsAdapter

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
        initObservers()
        initListeners()
        initRecyclerViews()
        initViewPager()

    }

    private fun initObservers() {
        mainViewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                binding.mainLayout.visibility = View.VISIBLE
            }
        }

        mainViewModel.competitionList.observe(viewLifecycleOwner) { competitions ->
            competitionsAdapter.updateList(competitions)
        }
    }

    private fun initRecyclerViews() {

        competitionsAdapter = CompetitionsAdapter()
        eventsAdapter = EventsAdapter()

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
            Competition(type = "Travesía", name = "XI Travessía Canal de Nado Gandía", location = GeoPoint(39.00955556293781, -0.1655901822236689), distance = 3),
            Competition(type = "Competición", name = "Campeonato Nacional Infantil de Verano"),
            Competition(type = "Competición", name = "5º Control Master"),
            Competition(type = "Travesía", name = "II Travesía por parejas Burriana", distance = 2),
            Competition(type = "Travesía", name = "IV Travessía Clara Morales", distance = 1)
        )
    }

    private fun eventsProvider(): List<Event> {
        return listOf(
            Event(type = "Comida", name = "Cena Navidad 2025"),
            Event(type = "Fiesta", name = "Sauvage"),
            Event(type = "Comida", name = "Comida post competición"),
            Event(type = "Quedada", name = "Nadar en Cullera"),
            Event(type = "Quedada", name = "Nadar en el rio Antella")
        )
    }

    private fun initViewPager() {
        val advertisementList = mutableListOf<Advertisement>()
        advertisementList.add(Advertisement(authorUsername = "Ivanpelu7", message = "Hola que tal", title = "Primer mensaje en el tablón"))
        advertisementList.add(Advertisement(authorUsername = "UsuarioInventado", message = "Mensaje de prueba", title = "Segundo mensaje en el tablón"))

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

        binding.tvAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddEventActivity::class.java)
            startActivity(intent)
        }
    }

}