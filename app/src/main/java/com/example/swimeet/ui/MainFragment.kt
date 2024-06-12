package com.example.swimeet.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swimeet.R
import com.example.swimeet.adapter.CompetitionsAdapter
import com.example.swimeet.adapter.EventsAdapter
import com.example.swimeet.adapter.ViewPagerAdapter
import com.example.swimeet.data.model.Advertisement
import com.example.swimeet.databinding.FragmentMainBinding
import com.example.swimeet.util.FirebaseUtil
import com.example.swimeet.viewmodel.AddAdvertisementViewModel
import com.example.swimeet.viewmodel.MainViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels()
    private val addAdvViewModel: AddAdvertisementViewModel by viewModels()
    private lateinit var competitionsAdapter: CompetitionsAdapter
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var vpAdapter: ViewPagerAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var isScrolling = false
    private lateinit var title: String
    private lateinit var message: String


    private fun onScrollStopped() {
        binding.fabButton.extend()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.init()
    }

    private fun initUI() {
        initObservers()
        initListeners()
        initRecyclerViews()
        initViewPager()
        mainViewModel.init()
    }

    private fun initObservers() {
        mainViewModel.loadCompleted.observe(viewLifecycleOwner) { loadCompleted ->
            if (!loadCompleted) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.GONE
                binding.fabButton.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                binding.mainLayout.visibility = View.VISIBLE
                binding.fabButton.visibility = View.VISIBLE
            }
        }

        mainViewModel.competitionList.observe(viewLifecycleOwner) { competitions ->
            if (competitions.isEmpty()) {
                competitionsAdapter.updateList(competitions)
                binding.tvNoCompetitions.visibility = View.VISIBLE

                val layoutParams =
                    binding.tvNextMeetings.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.topToBottom = binding.tvNoCompetitions.id
            } else {
                competitionsAdapter.updateList(competitions)
                binding.tvNoCompetitions.visibility = View.GONE

                val layoutParams =
                    binding.tvNextMeetings.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.topToBottom = binding.rvNextEvents.id
            }

        }

        mainViewModel.eventList.observe(viewLifecycleOwner) { events ->
            if (events.isEmpty()) {
                eventsAdapter.updateList(events)
                binding.tvNoEvents.visibility = View.VISIBLE
            } else {
                eventsAdapter.updateList(events)
                binding.tvNoEvents.visibility = View.GONE
            }
        }

        mainViewModel.advList.observe(viewLifecycleOwner) { adv ->
            if (adv.isEmpty()) {
                val dummyList = emptyList<Advertisement>()
                vpAdapter.updateList(dummyList)
                binding.viewPager.visibility = View.GONE
                val layoutParams =
                    binding.tvNextEvents.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.topToBottom = binding.tvNoAdvertisements.id
                binding.tvNextEvents.layoutParams = layoutParams
                binding.tvNoAdvertisements.visibility = View.VISIBLE
            } else {
                vpAdapter.updateList(adv)
                binding.viewPager.visibility = View.VISIBLE
                binding.tvNoAdvertisements.visibility = View.GONE
                val layoutParams =
                    binding.tvNextEvents.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.topToBottom = binding.viewPager.id
            }
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

    private fun showCustomDialogue() {
        val dialogView = layoutInflater.inflate(R.layout.new_advertisements_dialog, null)
        val etTitle = dialogView.findViewById<TextInputEditText>(R.id.etTitle)
        val etMessage = dialogView.findViewById<TextInputEditText>(R.id.etMessage)
        val btnAccept = dialogView.findViewById<Button>(R.id.btnAccept)

        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        btnAccept.setOnClickListener {
            title = etTitle.text.toString()
            message = etMessage.text.toString()

            val adv = Firebase.auth.currentUser?.displayName?.let { it ->
                Advertisement(
                    authorUsername = it,
                    title = title,
                    message = message
                )
            }
            if (adv != null) {
                addAdvViewModel.addAdv(adv)
            }

            dialog.dismiss()

            mainViewModel.init()
        }

        dialog.show()
    }

    private fun initListeners() {

        binding.textButton.setOnClickListener {
            showCustomDialogue()
        }

        binding.mainLayout.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
            isScrolling = true
            binding.fabButton.shrink()
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                if (isScrolling) {
                    isScrolling = false
                    onScrollStopped()
                }
            }, 200)
        })

        binding.fabButton.setOnClickListener {
            val intent = Intent(requireContext(), AddEventActivity::class.java)
            startActivity(intent)
        }
    }
}