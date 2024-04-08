package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private var _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _isCreated = MutableLiveData(false)
    val isCreated: LiveData<Boolean> get() = _isCreated

    fun signUp(email: String, username: String, password: String, name: String, category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signUp(email, username, password, name, category) {
                _isCreated.value = it
            }
            _isLoading.value = false
        }
    }


}