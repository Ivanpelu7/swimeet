package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.model.Advertisement
import com.example.swimeet.data.model.Competition
import com.example.swimeet.data.model.Event
import com.example.swimeet.data.repository.AdvertisementRepository
import com.example.swimeet.data.repository.CompetitionRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val competitionRepository = CompetitionRepository()
    private val advRepository = AdvertisementRepository()

    private val _competitionList = MutableLiveData<List<Competition>>(emptyList())
    val competitionList: LiveData<List<Competition>> get() = _competitionList

    private val _eventList = MutableLiveData<List<Event>>(emptyList())
    val eventList: LiveData<List<Event>> get() = _eventList

    private val _advList = MutableLiveData<List<Advertisement>>()
    val advList: LiveData<List<Advertisement>> get() = _advList

    private val _loadCompleted = MutableLiveData(false)
    val loadCompleted: LiveData<Boolean> get() = _loadCompleted

    fun init() {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            _advList.value = advRepository.getAdvertisements()
            _eventList.value = competitionRepository.getEvents()
            _competitionList.value = competitionRepository.getCompetitions()

            _loadCompleted.value = true
        }
    }
/*
    private fun getAdvertisements() {
        viewModelScope.launch {
            _loadingAdv.value = true
            _advList.value = advRepository.getAdvertisements()
            _loadingAdv.value = false
        }
    }

    private fun getEvents() {
        viewModelScope.launch {
            _loadingAdv.value = true
            _eventList.value = competitionRepository.getEvents()
            _loadingAdv.value = false
        }
    }

    fun getCompetitions() {
        viewModelScope.launch {
            _loading.value = true
            _competitionList.value = competitionRepository.getCompetitions()
            _loading.value = false
        }
    }

 */
}