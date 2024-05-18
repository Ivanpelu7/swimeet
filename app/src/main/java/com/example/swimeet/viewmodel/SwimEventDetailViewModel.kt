package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.model.Record
import com.example.swimeet.data.repository.RecordsRepository
import kotlinx.coroutines.launch

class SwimEventDetailViewModel : ViewModel() {

    private val repo = RecordsRepository()
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading
    private val _records = MutableLiveData<List<Record>>(emptyList())
    val records: LiveData<List<Record>> get() = _records

    fun getRecords(event: String) {
        viewModelScope.launch {
            _loading.value = true
            _records.value = repo.getRecords(event)
            _loading.value = false
        }
    }
}