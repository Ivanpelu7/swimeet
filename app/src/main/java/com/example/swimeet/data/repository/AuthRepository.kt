package com.example.swimeet.data.repository


import com.example.swimeet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    suspend fun signUp(
        email: String,
        username: String,
        password: String,
        name: String,
        category: String,
        callback: (Boolean) -> Unit
    ) {

        withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = User(auth.uid, username, email, name, category)
                        saveUser(user)
                        callback(true)
                    } else {
                        callback(false)
                    }
                }
        }
    }

    private fun saveUser(user: User) {
        Firebase.firestore.collection("users").document(user.userId!!).set(user)
    }
}