package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isCreated = MutableLiveData<Boolean>()
    val isCreated: LiveData<Boolean> get() = _isCreated

    private val _isLoginCorrect = MutableLiveData<Boolean>()
    val isLoginCorrect: LiveData<Boolean> get() = _isLoginCorrect

    fun signUp(
        email: String,
        username: String,
        password: String,
        name: String,
        category: String,
        genre: String,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _isCreated.value = authRepository.signUp(email, username, password, name, category, genre)
            _isLoading.value = false
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signIn(email, password) {
                _isLoginCorrect.value = it
            }
            _isLoading.value = false
        }
    }


}