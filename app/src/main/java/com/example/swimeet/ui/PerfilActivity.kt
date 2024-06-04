package com.example.swimeet.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.swimeet.R
import com.example.swimeet.adapter.MarksAdapter
import com.example.swimeet.data.model.Mark
import com.example.swimeet.databinding.ActivityPerfilBinding
import com.example.swimeet.util.FirebaseUtil
import com.example.swimeet.viewmodel.PerfilViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var marksAdapter: MarksAdapter
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageUri: Uri
    private lateinit var etCompetitions: AutoCompleteTextView
    private lateinit var etPrueba: AutoCompleteTextView
    private lateinit var date: Timestamp
    private lateinit var username: String
    private lateinit var userId: String
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
        FirebaseUtil.getCompetitionsRef().whereEqualTo("type", "Competición").get()
            .addOnSuccessListener {
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
            if (it.isNotEmpty()) {
                marksAdapter.updateList(it)
                binding.btnVerTodas.visibility = View.VISIBLE
            } else
                binding.btnVerTodas.visibility = View.INVISIBLE
        }

        perfilViewModel.loading.observe(this) {
            if (it) {
                binding.progressCircular.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.INVISIBLE
            } else {
                binding.progressCircular.visibility = View.INVISIBLE
                binding.mainLayout.visibility = View.VISIBLE
            }
        }

        perfilViewModel.recordData.observe(this) {
            if (it.isNotEmpty()) {
                val time = it["time"] as Long
                val mark = it["mark"] as Mark
                val category = it["category"] as String
                val genre = it["genre"] as String

                if (time.toInt() == 0) {
                    addMarkToRecords(mark, genre, category, username, userId)
                    Toast.makeText(
                        this,
                        "Has batido el record en la prueba ${mark.swimEvent}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (time.toInt() > mark.mark) {
                        updateRecord(mark, genre, category, username, userId)

                        Toast.makeText(
                            this,
                            "Has batido el record en la prueba ${mark.swimEvent}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun updateRecord(
        mark: Mark,
        genre: String,
        category: String,
        username: String,
        userId: String
    ) {
        perfilViewModel.updateRecord(mark, genre, category, username, userId)
    }

    private fun addMarkToRecords(
        mark: Mark,
        genre: String,
        category: String,
        username: String,
        userId: String
    ) {
        perfilViewModel.addMark(mark, genre, category, username, userId)
    }

    private fun loadData() {
        FirebaseUtil.getUsersRef().document(FirebaseUtil.getCurrentUserID()).get()
            .addOnSuccessListener {
                val name = it.get("name").toString()
                binding.tvDisplayName.text = name
                binding.tvUsername.text = Firebase.auth.currentUser!!.displayName
            }

        perfilViewModel.getMarks(5)
    }

    private fun loadPhoto(uri: String) {
        Glide.with(this).load(uri).transform(CircleCrop()).into(binding.ivPhoto)
    }

    private fun setListeners() {
        binding.btnVerTodas.setOnClickListener {
            val intent = Intent(this, AllMarksActivity::class.java)
            startActivity(intent)
        }

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

        binding.btnDeleteUser.setOnClickListener {
            try {
                FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirebaseUtil.getCurrentUserDocumentRef().delete().addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Usuario eliminado correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error al eliminar el usuario", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showCustomDialogue() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialogue, null)
        etCompetitions = dialogView.findViewById(R.id.etCompetition)
        etPrueba = dialogView.findViewById(R.id.etPrueba)
        val etMark = dialogView.findViewById<EditText>(R.id.etMark)
        val btnAccept = dialogView.findViewById<Button>(R.id.btnAccept)

        etCompetitions.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String

            FirebaseUtil.getCompetitionsRef().whereEqualTo("name", selectedItem).get()
                .addOnSuccessListener {
                    date = it.documents[0].get("date") as Timestamp
                }
        }

        initDropDownMenu()

        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        btnAccept.setOnClickListener {
            val competitionName = etCompetitions.text.toString()
            val mark = FirebaseUtil.convertirTiempoAMilisegundos(etMark.text.toString())
            val swimEvent = etPrueba.text.toString()

            if (date != null) saveData(competitionName, mark, swimEvent, date)

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveData(competitionName: String, mark: Long, swimEvent: String, date: Timestamp) {
        val mark = Mark(
            competition = competitionName,
            mark = mark,
            swimEvent = swimEvent,
            date = date
        )
        val markId = FirebaseUtil.getMarksRef().document().id
        mark.idMark = markId
        FirebaseUtil.getMarksRef().document(markId).set(mark).addOnSuccessListener {
            perfilViewModel.getMarks(5)

            checkIfIsRecord(mark)
        }
    }

    private fun checkIfIsRecord(mark: Mark) {
        perfilViewModel.getUserCategory {
            val category = it["category"]
            val genre = it["genre"]
            userId = it["userId"].toString()
            username = it["username"].toString()

            perfilViewModel.getRecord(category, genre, mark)
        }
    }

    @Deprecated("Deprecated in Java")
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