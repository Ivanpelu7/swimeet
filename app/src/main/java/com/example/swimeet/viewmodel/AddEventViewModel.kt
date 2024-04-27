package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.model.Chat
import com.example.swimeet.data.model.Competition
import com.example.swimeet.data.model.Event
import com.example.swimeet.data.repository.CompetitionRepository
import kotlinx.coroutines.launch

class AddEventViewModel: ViewModel() {

    private val competitionRepository = CompetitionRepository()

    private val _competitionAdded = MutableLiveData<Boolean>()
    val competitionAdded: LiveData<Boolean> get() = _competitionAdded

    fun addEvent(event: Event) {

    }

    fun addCompetition(competition: Competition) {
        viewModelScope.launch {
            competitionRepository.addCompetition(competition) { state ->
                _competitionAdded.value = state
            }
        }
    }
}