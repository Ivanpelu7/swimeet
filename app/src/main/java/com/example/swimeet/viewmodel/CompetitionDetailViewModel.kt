package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.model.Comment
import com.example.swimeet.data.model.Competition
import com.example.swimeet.data.model.Event
import com.example.swimeet.data.repository.CompetitionRepository
import kotlinx.coroutines.launch

class CompetitionDetailViewModel : ViewModel() {

    private val compRepository = CompetitionRepository()

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> get() = _loading

    private val _competition = MutableLiveData<Competition>(null)
    val competition: LiveData<Competition> get() = _competition

    private val _event = MutableLiveData<Event>(null)
    val event: LiveData<Event> get() = _event

    private val _comments = MutableLiveData<List<Comment>>()
    val comment: LiveData<List<Comment>> get() = _comments

    fun getCompetitionInfo(id: String) {
        viewModelScope.launch {
            _competition.value = compRepository.getCompetitionInfo(id)
            _loading.value = false
        }
    }

    fun getEventInfo(id: String) {
        viewModelScope.launch {
            _event.value = compRepository.getEventInfo(id)
            _loading.value = false
        }
    }

    fun getComments(type: String, eventId: String) {
        viewModelScope.launch {
            compRepository.getComments(type, eventId) { comments ->
                _comments.value = comments
            }
        }

    }
}