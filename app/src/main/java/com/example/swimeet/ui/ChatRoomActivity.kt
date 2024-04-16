package com.example.swimeet.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swimeet.util.FirebaseUtil
import com.example.swimeet.data.model.Message
import com.example.swimeet.adapter.MessagesAdapter
import com.example.swimeet.databinding.ActivityChatRoomBinding
import com.example.swimeet.viewmodel.ChatRoomViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private val chatRoomViewModel: ChatRoomViewModel by viewModels()
    private lateinit var otherUserId: String
    private lateinit var otherUsername: String
    private lateinit var chatId: String
    private var isChatOpen: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        getIntents()
        initObservers()
        checkIfChatExists()
        binding.tvNombreUsuario.text = otherUsername
        initListeners()
        setUpRecyclerView()
    }

    private fun initObservers() {
        chatRoomViewModel.messages.observe(this) { messagesList ->
            messagesAdapter.updateList(messagesList)

            binding.rvMessages.scrollToPosition(0)

            if (messagesList.isNotEmpty()) {
                if (!checkLastSenderID(messagesList.first()) && isChatOpen)
                    restartUnreadMessages()
            }

        }

        chatRoomViewModel.chatId.observe(this) { idChat ->
            chatId = idChat
            chatRoomViewModel.loadMessages(chatId)
        }
    }

    private fun getIntents() {
        otherUsername = intent.getStringExtra("otherUsername").toString()
        otherUserId = intent.getStringExtra("otherUserId").toString()
    }

    private fun initListeners() {
        binding.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivSendMessage.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                binding.etMessage.text = null
            }
        }
    }

    private fun checkIfChatExists() {
        val chatUsersId = listOf(
            FirebaseUtil.getCurrentUserID(),
            otherUserId
        ).sorted()

        chatRoomViewModel.checkIfChatExists(chatUsersId)
    }

    private fun setUpRecyclerView() {
        messagesAdapter = MessagesAdapter()
        val manager = LinearLayoutManager(this).apply {
            reverseLayout = true
        }

        binding.rvMessages.apply {
            layoutManager = manager
            adapter = messagesAdapter
        }
    }

    private fun checkLastSenderID(lastMessage: Message): Boolean {
        return lastMessage.idSender == FirebaseUtil.getCurrentUserID()
    }

    private fun restartUnreadMessages() {
        chatRoomViewModel.restartUnreadMessages(chatId)
    }

    private fun sendMessage(message: String) {
        val chatMap = mapOf(
            "lastMessageSenderId" to FirebaseUtil.getCurrentUserID(),
            "lastMessageTimestamp" to Timestamp.now(),
            "lastMessage" to message,
            "unreadMessages" to FieldValue.increment(1)
        )

        // Actualizamos los campos del chat con los datos del chatMap
        chatRoomViewModel.updateChat(chatId, chatMap)

        // Añadimos el mensaje a la coleccion de mensajes del chat actual
        val newMessage = Message(message, FirebaseUtil.getCurrentUserID())
        chatRoomViewModel.addMessage(chatId, newMessage)
    }

    override fun onStop() {
        super.onStop()
        isChatOpen = false
    }
}



