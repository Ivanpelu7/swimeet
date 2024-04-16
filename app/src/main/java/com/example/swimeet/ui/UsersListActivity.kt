package com.example.swimeet.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swimeet.adapter.UsersAdapter
import com.example.swimeet.viewmodel.UsersListViewModel
import com.example.swimeet.databinding.ActivitySearchUsersBinding

class UsersListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchUsersBinding
    private val usersListViewModel: UsersListViewModel by viewModels()
    private lateinit var userAdapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        setUpRecyclerView()
        initListeners()
        initObservers()
    }

    private fun setUpRecyclerView() {
        userAdapter = UsersAdapter()
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(this@UsersListActivity)
            adapter = userAdapter
        }
    }

    private fun initObservers() {
        usersListViewModel.users.observe(this) { userList ->
            userAdapter.updateList(userList)
        }

        usersListViewModel.isLoading.observe(this) { isLoading ->
            if (!isLoading) binding.progressBar.visibility = View.GONE
        }
    }

    private fun initListeners() {
        binding.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}