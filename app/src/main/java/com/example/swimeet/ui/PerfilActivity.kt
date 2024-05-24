package com.example.swimeet.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.swimeet.databinding.ActivityPerfilBinding
import com.example.swimeet.util.FirebaseUtil
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        loadPhoto(Firebase.auth.currentUser!!.photoUrl!!)
        setListeners()
    }

    private fun loadPhoto(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .transform(CircleCrop())
            .into(binding.ivPhoto)
    }

    private fun setListeners() {

        binding.ivPhoto.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.btnCloseSesion.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            loadPhoto(imageUri)
            uploadImageToFirebase()
        }
    }

    private fun uploadImageToFirebase() {
        val user = Firebase.auth.currentUser
        if (user != null && ::imageUri.isInitialized) {
            val fileRef = Firebase.storage.reference.child("profile_images/${user.uid}.jpg")
            fileRef.putFile(imageUri)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        val photoURL = uri.toString()
                        updateUserProfile(photoURL)
                    }
                }
        }
    }

    private fun updateUserProfile(photoURL: String) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(photoURL))
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        updateUserFirestoreDocument(photoURL)
                    }
                }
        }
    }

    private fun updateUserFirestoreDocument(photoURL: String) {
        val userDocument = FirebaseUtil.getCurrentUserDocumentRef()
        userDocument.update("photo", photoURL)
    }
}