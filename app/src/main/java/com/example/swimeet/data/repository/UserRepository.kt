package com.example.swimeet.data.repository

import com.example.swimeet.data.model.Mark
import com.example.swimeet.data.model.User
import com.example.swimeet.util.FirebaseUtil
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {

    suspend fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()

        withContext(Dispatchers.IO) {
            val result = FirebaseUtil.getUsersRef()
                .whereNotEqualTo("userId", FirebaseUtil.getCurrentUserID())
                .get().await()

            for (document in result.documents) {
                userList.add(document.toObject(User::class.java)!!)
            }
        }

        return userList
    }

    fun getOtherUser(usersId: List<String>, onComplete: (User) -> Unit) {
        FirebaseUtil.getOtherUserFromChat(usersId)
            .get()
            .addOnSuccessListener { user ->
                val otherUser = user.toObject(User::class.java)!!
                onComplete(otherUser)
            }
    }

    suspend fun getUserMarks(onComplete: (List<Mark>) -> Unit) {
        val listMarks: MutableList<Mark> = mutableListOf()
        var count = 0

        withContext(Dispatchers.IO) {
            FirebaseUtil.getMarksRef().orderBy("date", Query.Direction.DESCENDING)
                .get().addOnSuccessListener {
                    if (!it.isEmpty) {
                        for (doc in it.documents) {
                            val mark = doc.toObject(Mark::class.java)
                            if (mark != null) {
                                listMarks.add(mark)
                                count++

                                if (count == 5) break
                            }
                        }
                    }

                    onComplete(listMarks)
                }


        }
    }
}