package com.example.swimeet.util

import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FirebaseUtil {

    fun getCurrentUserID(): String {
        return Firebase.auth.currentUser!!.uid
    }

    fun getCurrentUserDocumentRef(): DocumentReference {
        return Firebase.firestore.collection("users").document(getCurrentUserID())
    }

    fun getChatsRef(): CollectionReference {
        return Firebase.firestore.collection("chats")
    }

    fun getUsersRef(): CollectionReference {
        return Firebase.firestore.collection("users")
    }

    fun getMessagesRef(chatId: String): CollectionReference {
        return Firebase.firestore.collection("chats").document(chatId).collection("messages")
    }

    fun timestampToString(timestamp: Timestamp): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp.toDate())
    }

    fun timestampToStringDate(timestamp: Timestamp): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp.toDate())
    }

    fun getOtherUserFromChat(userIds: List<String>): DocumentReference {
        if (userIds[0] == getCurrentUserID()) {
            return getUsersRef().document(userIds[1])

        } else {
            return getUsersRef().document(userIds[0])
        }
    }

    fun getCompetitionsRef(): CollectionReference {
        return Firebase.firestore.collection("competitions")
    }

    fun getEventsRef(): CollectionReference {
        return Firebase.firestore.collection("events")
    }

    fun getAdvertisementsRef(): CollectionReference {
        return Firebase.firestore.collection("advertisements")
    }

    fun getRecordsRef(): CollectionReference {
        return Firebase.firestore.collection("records")
    }

    fun milisecondsToSwimTime(ms: Long): String {
        val totalSegundos = ms / 1000
        val minutos = totalSegundos / 60
        val segundos = totalSegundos % 60
        val decimas = (ms % 1000) / 10

        return String.format("%02d:%02d.%02d", minutos, segundos, decimas)
    }

    fun parseAdvTime(date: Timestamp): String {
        val timeInMillis = date.toDate().time
        val currentDate = Timestamp.now().toDate().time
        val diferenciaMinutos = ((currentDate - timeInMillis) / (1000 * 60)).toInt()

        return if (diferenciaMinutos < 60) {
            // Mostrar en minutos si han pasado menos de 1 hora
            "hace $diferenciaMinutos m"
        } else {
            // Mostrar en horas si han pasado 1 hora o más
            val horas = diferenciaMinutos / 60
            "hace $horas h"
        }
    }

    fun parseFirebaseTimestamp(timestamp: Timestamp): String {
        // Convertir el timestamp a un objeto Date
        val date = timestamp.toDate()

        // Formatear la fecha
        val format = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
        return format.format(date)
    }
}