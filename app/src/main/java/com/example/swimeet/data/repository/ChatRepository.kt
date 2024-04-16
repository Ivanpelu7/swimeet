package com.example.swimeet.data.repository

import android.util.Log
import com.example.swimeet.data.model.Chat
import com.example.swimeet.util.FirebaseUtil
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatRepository {

    suspend fun getMyChats(onChatsUpdated: (List<Chat>) -> Unit) {
        withContext(Dispatchers.IO) {
            FirebaseUtil.getChatsRef()
                .whereArrayContains("usersId", FirebaseUtil.getCurrentUserID())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { value, _ ->
                    val chatList = mutableListOf<Chat>()

                    if (value != null) {
                        for (document in value!!.documents) {
                            chatList.add(document.toObject(Chat::class.java)!!)
                        }

                        onChatsUpdated(chatList)
                    }
                }
        }
    }

    suspend fun restartUnreadMessages(chatId: String) {
        withContext(Dispatchers.IO) {
            FirebaseUtil.getChatsRef()
                .document(chatId)
                .update("unreadMessages", 0)
                .await()
        }
    }

    suspend fun checkIfChatExists(chatUsersId: List<String>): String {
        var chatId: String

        withContext(Dispatchers.IO) {
            val result = FirebaseUtil.getChatsRef()
                .whereEqualTo("usersId", chatUsersId)
                .get().await()

            chatId = if (result.isEmpty) {
                createChat(chatUsersId)
            } else {
                result.documents[0].id
            }
        }

        return chatId
    }

    suspend fun createChat(chatUsersId: List<String>): String {
        var newChatId: String
        withContext(Dispatchers.IO) {
            newChatId = FirebaseUtil.getChatsRef().document().id
            Log.d("newchat", newChatId)
            val newChat = Chat(newChatId, chatUsersId)
            FirebaseUtil.getChatsRef().document(newChatId).set(newChat).await()
        }

        return newChatId
    }

    suspend fun updateChat(chatId: String, updates: Map<String, Any>) {
        withContext(Dispatchers.IO) {
            FirebaseUtil.getChatsRef()
                .document(chatId)
                .update(updates)
                .await()
        }
    }
}