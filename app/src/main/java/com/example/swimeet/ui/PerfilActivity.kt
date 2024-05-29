package com.example.swimeet.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.swimeet.R
import com.example.swimeet.adapter.MarksAdapter
import com.example.swimeet.data.model.Mark
import com.example.swimeet.databinding.ActivityPerfilBinding
import com.example.swimeet.util.FirebaseUtil
import com.example.swimeet.viewmodel.PerfilViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var marksAdapter: MarksAdapter
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageUri: Uri
    private lateinit var etCompetitions: AutoCompleteTextView
    private lateinit var etPrueba: AutoCompleteTextView
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

    private fun initDropDownMenu() {
        val compList = mutableListOf<String>()
        FirebaseUtil.getCompetitionsRef().whereEqualTo("type", "Competición").get().addOnSuccessListener {
            for (doc in it.documents) {
                val name = doc.getString("name")
                if (name != null) {
                    compList.add(name)
                }
            }

            val adapter = ArrayAdapter(applicationContext, R.layout.list_item, compList)
            etCompetitions.setAdapter(adapter)

            val items = resources.getStringArray(R.array.pruebas_natacion).toList()
            val eventsAdapter = ArrayAdapter(applicationContext, R.layout.list_item, items)
            etPrueba.setAdapter(eventsAdapter)

        }
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
        Glide.with(this).load(uri).transform(CircleCrop()).into(binding.ivPhoto)
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
        etCompetitions = dialogView.findViewById(R.id.etCompetition)
        etPrueba = dialogView.findViewById(R.id.etPrueba)
        val etMark = dialogView.findViewById<EditText>(R.id.etMark)
        val btnAccept = dialogView.findViewById<Button>(R.id.btnAccept)

        initDropDownMenu()

        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        btnAccept.setOnClickListener {
            val competitionName = etCompetitions.text.toString()
            val mark = FirebaseUtil.convertirTiempoAMilisegundos(etMark.text.toString())
            val swimEvent = etPrueba.text.toString()
            saveData(competitionName, mark, swimEvent)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveData(competitionName: String, mark: Long, swimEvent: String) {
        val mark =
            Mark(competition = competitionName, mark = mark, swimEvent = swimEvent)
        val markId = FirebaseUtil.getMarksRef().document().id
        mark.idMark = markId
        FirebaseUtil.getMarksRef().document(markId).set(mark).addOnSuccessListener {
            perfilViewModel.getMarks()
        }
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
            fileRef.putFile(imageUri).addOnSuccessListener {
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
            val profileUpdates =
                UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(photoURL)).build()

            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
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