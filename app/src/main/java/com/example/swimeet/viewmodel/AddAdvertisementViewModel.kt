package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.model.Advertisement
import com.example.swimeet.data.repository.AdvertisementRepository
import kotlinx.coroutines.launch

class AddAdvertisementViewModel : ViewModel() {

    private val advRepository = AdvertisementRepository()

    private val _advAdded = MutableLiveData<Boolean>()
    val advAdded: LiveData<Boolean> get() = _advAdded

    fun addAdv(adv: Advertisement) {
        viewModelScope.launch {
            advRepository.addAdvertisement(adv) { state ->
                _advAdded.value = state
            }
        }
    }
}