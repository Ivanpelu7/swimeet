package com.example.swimeet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swimeet.data.model.Message
import com.example.swimeet.data.repository.ChatRepository
import com.example.swimeet.data.repository.MessageRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ChatRoomViewModel : ViewModel() {

    private val messageRepository = MessageRepository()
    private val chatRepository = ChatRepository()

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val _chatId = MutableLiveData<String>()
    val chatId: LiveData<String> get() = _chatId


    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            messageRepository.getChatMessages(chatId) { messages ->
                _messages.value = messages
            }
        }
    }

    fun checkIfChatExists(
        chatUsersId: List<String>,
        otherUsername: String,
        otherUserImage: String
    ) {
        viewModelScope.launch {
            _chatId.value = chatRepository.checkIfChatExists(
                chatUsersId,
                otherUserImage,
                otherUsername,
                Firebase.auth.currentUser!!.displayName,
                Firebase.auth.currentUser!!.photoUrl.toString()
            )
        }
    }

    fun restartUnreadMessages(chatId: String) {
        viewModelScope.launch {
            chatRepository.restartUnreadMessages(chatId)
        }
    }

    fun updateChat(chatId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            chatRepository.updateChat(chatId, updates)
        }
    }

    fun addMessage(chatId: String, message: Message) {
        viewModelScope.launch {
            messageRepository.addMessage(chatId, message)
        }
    }
}