package com.example.swimeet.data.repository


import com.example.swimeet.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    suspend fun signUp(
        email: String,
        username: String,
        password: String,
        name: String,
        category: String
    ): Boolean {
        var b = false
        withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, password).await()


            if (auth.currentUser != null) {
                val user = User(auth.uid, false, username, email, name, category)

                val storage = FirebaseStorage.getInstance()
                val storageRef = storage.reference.child("usuario.png")

                val url = storageRef.downloadUrl.await()
                user.photo = url.toString()
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(url)
                    .build()

                auth.currentUser?.updateProfile(profileUpdates)!!.await()
                saveUser(user)
                b = true
            }
        }
        return b
    }


    private fun saveUser(user: User) {
        Firebase.firestore.collection("users").document(user.userId!!).set(user)
    }

    suspend fun signIn(email: String, password: String, callback: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                }
        }
    }
}