package com.example.swimeet.data.repository


import android.content.Context
import android.net.Uri
import androidx.core.graphics.drawable.toDrawable
import com.example.swimeet.R
import com.example.swimeet.data.model.User
import com.example.swimeet.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
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
        context: Context,
        callback: (Boolean) -> Unit
    ) {

        withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = User(auth.uid, false, username, email, name, category)

                        var storage = FirebaseStorage.getInstance()
                        val storageRef = storage.reference.child("usuario.png")

                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .setPhotoUri(uri)
                                .build()

                            auth.currentUser?.updateProfile(profileUpdates)
                            saveUser(user)
                            callback(true)
                        }

                    } else {
                        callback(false)
                    }
                }
        }
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