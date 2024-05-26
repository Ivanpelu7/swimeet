package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.model.User
import com.example.swimeet.data.repository.UserRepository
import kotlinx.coroutines.launch

class UsersListViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private var _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _users.value = userRepository.getAllUsers()
            _isLoading.value = false
        }
    }


}