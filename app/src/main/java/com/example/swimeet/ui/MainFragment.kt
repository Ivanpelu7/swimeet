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
    private lateinit var vpAdapter: ViewPagerAdapter

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
        mainViewModel.init()
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

        mainViewModel.eventList.observe(viewLifecycleOwner) { events ->
            eventsAdapter.updateList(events)
        }

        mainViewModel.advList.observe(viewLifecycleOwner) { adv ->
            vpAdapter.updateList(adv)
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

    private fun initViewPager() {

        vpAdapter = ViewPagerAdapter()
        binding.viewPager.adapter = vpAdapter
    }

    private fun initListeners() {
        binding.buttonLogOut.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.tvSalir.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        binding.tvAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddEventActivity::class.java)
            startActivity(intent)
        }

        binding.tvAddEvent.setOnClickListener {
            val intent = Intent(requireContext(), AddEventActivity::class.java)
            startActivity(intent)
        }

        binding.tvAddAdvertisement.setOnClickListener {
            val intent = Intent(requireContext(), AddAdvertisementActivity::class.java)
            startActivity(intent)
        }
    }

}