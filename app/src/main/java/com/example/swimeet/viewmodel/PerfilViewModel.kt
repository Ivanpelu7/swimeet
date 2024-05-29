package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.model.Mark
import com.example.swimeet.data.repository.UserRepository
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _markList = MutableLiveData<List<Mark>>()
    val markList: LiveData<List<Mark>> get() = _markList

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> get() = _loading

    fun getMarks() {
        viewModelScope.launch {
            userRepository.getUserMarks {
                _markList.value = it
                _loading.value = false
            }
        }
    }
}