package com.example.swimeet.util

import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

object FirebaseUtil {

    fun getCurrentUserID(): String {
        return Firebase.auth.currentUser!!.uid
    }

    fun getCurrentUserDocumentRef() : DocumentReference {
        return Firebase.firestore.collection("users").document(getCurrentUserID())
    }

    fun getChatsRef() : CollectionReference {
        return Firebase.firestore.collection("chats")
    }

    fun getUsersRef() : CollectionReference {
        return Firebase.firestore.collection("users")
    }

    fun getMessagesRef(chatId: String) : CollectionReference {
        return Firebase.firestore.collection("chats").document(chatId).collection("messages")
    }

    fun timestampToString(timestamp: Timestamp): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp.toDate())
    }

    fun getOtherUserFromChat(userIds: List<String>): DocumentReference {
        if (userIds[0] == getCurrentUserID()) {
            return getUsersRef().document(userIds[1])

        } else {
            return getUsersRef().document(userIds[0])
        }
    }
}