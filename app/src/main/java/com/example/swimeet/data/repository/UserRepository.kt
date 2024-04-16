package com.example.swimeet.data.repository

import com.example.swimeet.data.model.User
import com.example.swimeet.util.FirebaseUtil
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
}