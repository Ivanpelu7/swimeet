package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.model.Competition
import com.example.swimeet.data.repository.CompetitionRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val competitionRepository = CompetitionRepository()

    private val _competitionList = MutableLiveData<List<Competition>>(emptyList())
    val competitionList: LiveData<List<Competition>> get() = _competitionList
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading

    init {
        getCompetitions()
    }

    fun getCompetitions() {
        viewModelScope.launch {
            _loading.value = true
            _competitionList.value = competitionRepository.getCompetitions()
            _loading.value = false
        }
    }
}