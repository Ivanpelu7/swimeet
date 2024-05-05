package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.model.Competition
import com.example.swimeet.data.model.Event
import com.example.swimeet.data.repository.CompetitionRepository
import kotlinx.coroutines.launch

class CompetitionDetailViewModel: ViewModel() {

    private val compRepository = CompetitionRepository()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _competition = MutableLiveData<Competition>()
    val competition: LiveData<Competition> get() = _competition

    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> get() = _event

    fun getCompetitionInfo(id: String) {
        viewModelScope.launch {
            _loading.value = true
            _competition.value = compRepository.getCompetitionInfo(id)
            _loading.value = false
        }
    }

    fun init(id: String) {
        getCompetitionInfo(id)
    }
}