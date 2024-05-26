package com.example.swimeet.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swimeet.adapter.ChatAdapter
import com.example.swimeet.databinding.FragmentChatBinding
import com.example.swimeet.viewmodel.RecentChatsViewModel
import java.lang.ref.WeakReference

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val recentChatsViewModel: RecentChatsViewModel by viewModels()
    private lateinit var recyclerAdapter: ChatAdapter
    var isScrolling = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        setUpRecyclerView()
        initObservers()
        initListeners()
        recentChatsViewModel.getRecentChats()
    }

    private fun initListeners() {
        binding.fabButton.setOnClickListener {
            val intent = Intent(requireContext(), UsersListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initObservers() {
        recentChatsViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.layoutMain.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.layoutMain.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
            }
        }

        recentChatsViewModel.recentChats.observe(viewLifecycleOwner) { recentChats ->
            recyclerAdapter.updateList(recentChats)

        }


    }

    private fun setUpRecyclerView() {
        recyclerAdapter = ChatAdapter(fragmentRef = WeakReference(this))
        binding.rvChats.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChats.adapter = recyclerAdapter

        binding.rvChats.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            isScrolling = true
            binding.fabButton.shrink()
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                if (isScrolling) {
                    isScrolling = false
                    onScrollStopped()
                }
            }, 300)
        }
    }

    private fun onScrollStopped() {
        binding.fabButton.extend()
    }
}






