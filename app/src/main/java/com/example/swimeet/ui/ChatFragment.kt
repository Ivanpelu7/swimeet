package com.example.swimeet.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swimeet.adapter.ChatAdapter
import com.example.swimeet.databinding.FragmentChatBinding
import com.example.swimeet.viewmodel.RecentChatsViewModel

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val recentChatsViewModel: RecentChatsViewModel by viewModels()
    private lateinit var recyclerAdapter: ChatAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        recentChatsViewModel.getRecentChats()
        setUpRecyclerView()
        initObservers()
        initListeners()
    }

    private fun initListeners() {
        binding.addChat.setOnClickListener {
            val intent = Intent(requireContext(), UsersListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initObservers() {
        recentChatsViewModel.recentChats.observe(viewLifecycleOwner) { recentChats ->
            binding.layoutMain.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            recyclerAdapter.updateList(recentChats)
            binding.layoutMain.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }


    }

    private fun setUpRecyclerView() {
        recyclerAdapter = ChatAdapter()
        binding.rvChats.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChats.adapter = recyclerAdapter
    }
}






