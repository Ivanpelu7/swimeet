package com.example.swimeet.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.swimeet.R
import com.example.swimeet.adapter.MarksAdapter
import com.example.swimeet.databinding.ActivityPerfilBinding
import com.example.swimeet.util.FirebaseUtil
import com.example.swimeet.viewmodel.PerfilViewModel
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var marksAdapter: MarksAdapter
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageUri: Uri
    private val perfilViewModel: PerfilViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        setListeners()
        initRecyclerView()
        initObservers()
        loadPhoto(Firebase.auth.currentUser!!.photoUrl.toString())
        loadData()
    }

    private fun initRecyclerView() {
        marksAdapter = MarksAdapter()
        binding.rvLastMarks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = marksAdapter
        }
    }

    private fun initObservers() {
        perfilViewModel.markList.observe(this) {
            marksAdapter.updateList(it)
        }

        perfilViewModel.loading.observe(this) {
            if (it) {
                binding.progressCircular.visibility = View.INVISIBLE
                binding.mainLayout.visibility = View.INVISIBLE
            } else {
                binding.progressCircular.visibility = View.INVISIBLE
                binding.mainLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun loadData() {
        FirebaseUtil.getUsersRef().document(FirebaseUtil.getCurrentUserID()).get()
            .addOnSuccessListener {
                val name = it.get("name").toString()
                binding.tvDisplayName.text = name
                binding.tvUsername.text = Firebase.auth.currentUser!!.displayName
            }

        perfilViewModel.getMarks()
    }

    private fun loadPhoto(uri: String) {
        Glide.with(this)
            .load(uri)
            .transform(CircleCrop())
            .into(binding.ivPhoto)
    }

    private fun setListeners() {

        binding.iconButton.setOnClickListener {
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

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnAddMark.setOnClickListener {
            showCustomDialogue()
        }
    }

    private fun showCustomDialogue() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialogue, null)
        val etCompetition = dialogView.findViewById<EditText>(R.id.etCompetition)
        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
        val etMark = dialogView.findViewById<EditText>(R.id.etMark)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()



        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            loadPhoto(imageUri.toString())
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