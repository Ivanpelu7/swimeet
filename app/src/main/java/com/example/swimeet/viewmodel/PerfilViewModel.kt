package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.model.Mark
import com.example.swimeet.data.repository.RecordsRepository
import com.example.swimeet.data.repository.UserRepository
import com.example.swimeet.util.FirebaseUtil
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val recordRepository = RecordsRepository()

    private val _markList = MutableLiveData<List<Mark>>()
    val markList: LiveData<List<Mark>> get() = _markList

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> get() = _loading

    private val _recordData = MutableLiveData<Map<String, Any?>>()
    val recordData: LiveData<Map<String, Any?>> get() = _recordData


    fun getMarks() {
        viewModelScope.launch {
            userRepository.getUserMarks {
                _markList.value = it
                _loading.value = false
            }
        }
    }

    fun getRecord(category: String?, genre: String?, mark: Mark) {
        viewModelScope.launch {
            recordRepository.getRecord(category!!, mark.swimEvent, genre!!) {
                _recordData.value = mapOf(
                    "time" to it,
                    "mark" to mark,
                    "category" to category,
                    "genre" to genre
                )
            }
        }
    }

    fun getUserCategory(onComplete: (Map<String, String>) -> Unit) {
        viewModelScope.launch {
            FirebaseUtil.getCurrentUserDocumentRef().get().addOnSuccessListener {
                onComplete(
                    mapOf(
                        "category" to it.data!!["category"] as String,
                        "genre" to it.data!!["genre"] as String,
                        "username" to it.data!!["username"] as String,
                        "userId" to it.data!!["userId"] as String
                    )
                )
            }
        }
    }

    fun addMark(mark: Mark, genre: String, category: String, username: String, userId: String) {
        viewModelScope.launch {
            recordRepository.addMark(mark, genre, category, username, userId)
        }
    }

    fun updateRecord(mark: Mark, genre: String, category: String, username: String, userId: String) {
        viewModelScope.launch {
            recordRepository.updateRecord(mark, genre, category, username, userId)
        }
    }
}