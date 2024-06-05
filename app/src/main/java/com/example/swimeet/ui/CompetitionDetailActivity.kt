package com.example.swimeet.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swimeet.R
import com.example.swimeet.adapter.CommentsAdapter
import com.example.swimeet.adapter.ParticipantsAdapter
import com.example.swimeet.data.model.Comment
import com.example.swimeet.data.model.User
import com.example.swimeet.databinding.ActivityCompetitionDetailBinding
import com.example.swimeet.util.FirebaseUtil
import com.example.swimeet.viewmodel.CompetitionDetailViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CompetitionDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityCompetitionDetailBinding
    private val compDetailViewModel: CompetitionDetailViewModel by viewModels()
    private lateinit var id: String
    private lateinit var name: String
    private lateinit var longitude: String
    private lateinit var latitude: String
    private lateinit var type: String
    private lateinit var link: String
    private lateinit var creatorId: String
    private var participants: MutableList<User> = mutableListOf()
    private var participantsAdapter = ParticipantsAdapter()
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var photo: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompetitionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.prgbar.visibility = View.VISIBLE
        binding.constraintLayout.visibility = View.INVISIBLE
        getIntents()
        initRecyclerView()
        initObservers()
        initListeners()
        setMap()
        getCreatorId()

        if (type == "0") {
            compDetailViewModel.getCompetitionInfo(id)
            compDetailViewModel.getComments("competitions", id)
        } else if (type == "1") {
            compDetailViewModel.getEventInfo(id)
            compDetailViewModel.getComments("events", id)
        }

        if (link == "") {
            binding.enlace.isEnabled = false
        }
    }

    private fun getCreatorId() {
        when (type) {
            "0" -> {
                FirebaseUtil.getCompetitionsRef().document(id).get().addOnSuccessListener {
                    creatorId = it.get("userId").toString()
                }
            }
            "1" -> {
                FirebaseUtil.getEventsRef().document(id).get().addOnSuccessListener {
                    creatorId = it.get("userId").toString()
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvParticipantes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = participantsAdapter
        }

        binding.rvComentarios.apply {
            commentsAdapter = CommentsAdapter()
            layoutManager = LinearLayoutManager(applicationContext).apply {
                reverseLayout = true
            }
            adapter = commentsAdapter
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(ContextThemeWrapper(v.context, R.style.CustomPopupMenu), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {
                    val dialogView = layoutInflater.inflate(R.layout.alert_dialog_delete, null)
                    val dialogBuilder = AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create()

                    val text = dialogView.findViewById<TextView>(R.id.text)
                    val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
                    val btnNo = dialogView.findViewById<Button>(R.id.btnNo)

                    btnYes.setOnClickListener {
                        deleteItem(id, type)
                        dialogBuilder.dismiss()
                    }

                    btnNo.setOnClickListener {
                        dialogBuilder.dismiss()
                    }


                    if (creatorId != FirebaseUtil.getCurrentUserID()) {
                        text.text = "Solo el creador puede eliminar el evento"
                        btnYes.visibility = View.GONE
                        btnNo.text = "Aceptar"
                    }

                    dialogBuilder.show()
                    true
                }

                else -> false
            }
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

    private fun deleteItem(id: String, type: String) {
        when (type) {
            "0" -> FirebaseUtil.getCompetitionsRef().document(id).delete()
            "1" -> FirebaseUtil.getEventsRef().document(id).delete()
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun initListeners() {
        binding.btnOptions.setOnClickListener {
            showMenu(it, R.menu.popup_menu)
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivSendComment.setOnClickListener {
            if (binding.etComment.text.toString() != "") {
                val message = binding.etComment.text.toString()

                for (participant in participants) {
                    if (participant.userId == FirebaseUtil.getCurrentUserID()) {
                        photo = participant.photo
                        break
                    }
                }

                val comment = Comment(
                    userId = FirebaseUtil.getCurrentUserID(),
                    message = message,
                    username = Firebase.auth.currentUser!!.displayName!!,
                    userPhoto = Firebase.auth.currentUser!!.photoUrl.toString()
                )

                FirebaseUtil.getCommentsRef(id, if (type == "0") "competitions" else "events")
                    .add(comment).addOnSuccessListener {
                        compDetailViewModel.getComments(
                            if (type == "0") "competitions" else "events",
                            id
                        )
                    }

                binding.etComment.setText("")
                hideKeyboard(binding.etComment)
            }
        }

        binding.asistirSwitch.setOnClickListener {
            if ((it as MaterialSwitch).isChecked) {
                if (type == "0") {
                    FirebaseUtil.getCompetitionsRef().document(id)
                        .update(
                            "participants",
                            FieldValue.arrayUnion(FirebaseUtil.getCurrentUserID())
                        )
                        .addOnSuccessListener {
                            FirebaseUtil.getCurrentUserDocumentRef().get()
                                .addOnSuccessListener {
                                    val user: User? = it.toObject(User::class.java)
                                    if (!participants.contains(user)) {
                                        val previousSize = participants.size
                                        participants.add(user!!)
                                        val newSize = participants.size
                                        participantsAdapter.updateList(participants)
                                        participantsAdapter.notifyItemRangeInserted(
                                            previousSize,
                                            newSize - previousSize
                                        )

                                        binding.tvNoParticipantes.visibility = View.GONE
                                        val layoutParams =
                                            binding.tvComentarios.layoutParams as ConstraintLayout.LayoutParams
                                        layoutParams.topToBottom = binding.rvParticipantes.id
                                    }
                                }
                        }
                } else {
                    FirebaseUtil.getEventsRef().document(id)
                        .update(
                            "participants",
                            FieldValue.arrayUnion(FirebaseUtil.getCurrentUserID())
                        )
                        .addOnSuccessListener {
                            FirebaseUtil.getCurrentUserDocumentRef().get()
                                .addOnSuccessListener {
                                    val user: User? = it.toObject(User::class.java)
                                    if (!participants.contains(user)) {
                                        val previousSize = participants.size
                                        participants.add(user!!)
                                        val newSize = participants.size
                                        participantsAdapter.updateList(participants)
                                        participantsAdapter.notifyItemRangeInserted(
                                            previousSize,
                                            newSize - previousSize
                                        )
                                        val layoutParams =
                                            binding.tvComentarios.layoutParams as ConstraintLayout.LayoutParams
                                        layoutParams.topToBottom = binding.rvParticipantes.id

                                        if (participants.size == 0) {
                                            binding.tvNoParticipantes.visibility = View.VISIBLE
                                        }
                                    }
                                }
                        }
                }
            } else {
                if (type == "0") {
                    FirebaseUtil.getCompetitionsRef().document(id)
                        .update(
                            "participants",
                            FieldValue.arrayRemove(FirebaseUtil.getCurrentUserID())
                        )
                        .addOnSuccessListener {
                            for (user in participants) {
                                if (user.userId == FirebaseUtil.getCurrentUserID()) {
                                    val position = participants.indexOf(user)
                                    participants.removeAt(position)
                                    participantsAdapter.updateList(participants)
                                    participantsAdapter.notifyItemRemoved(position)

                                    if (participants.size == 0) {
                                        binding.tvNoParticipantes.visibility = View.VISIBLE

                                        val layoutParams =
                                            binding.tvComentarios.layoutParams as ConstraintLayout.LayoutParams
                                        layoutParams.topToBottom = binding.tvNoParticipantes.id
                                    }
                                    break
                                }
                            }

                        }
                } else {
                    FirebaseUtil.getEventsRef().document(id)
                        .update(
                            "participants",
                            FieldValue.arrayRemove(FirebaseUtil.getCurrentUserID())
                        )
                        .addOnSuccessListener {
                            for (user in participants) {
                                if (user.userId == FirebaseUtil.getCurrentUserID()) {
                                    val position = participants.indexOf(user)
                                    participants.removeAt(position)
                                    participantsAdapter.updateList(participants)
                                    participantsAdapter.notifyItemRemoved(position)

                                    if (participants.size == 0) {
                                        binding.tvNoParticipantes.visibility = View.VISIBLE

                                        val layoutParams =
                                            binding.tvComentarios.layoutParams as ConstraintLayout.LayoutParams
                                        layoutParams.topToBottom = binding.tvNoParticipantes.id
                                    }
                                    break
                                }
                            }

                        }
                }
            }
        }

        binding.enlace.setOnClickListener {
            val webpage: Uri = Uri.parse(link)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        }
    }

    private fun getIntents() {
        id = intent.getStringExtra("id").toString()
        name = intent.getStringExtra("name").toString()
        longitude = intent.getStringExtra("longitude").toString()
        latitude = intent.getStringExtra("latitude").toString()
        type = intent.getStringExtra("type").toString()
        link = intent.getStringExtra("link").toString()
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun initObservers() {
        compDetailViewModel.comment.observe(this) { comments ->
            commentsAdapter.updateList(comments)
            binding.rvComentarios.scrollToPosition(comments.size - 1)
        }

        if (type == "0") {
            compDetailViewModel.competition.observe(this) { competition ->
                if (competition != null) {
                    binding.tvCompetitionName.text = competition.name
                    binding.tvTitle.text = competition.name
                    binding.tvFecha.text = FirebaseUtil.parseFirebaseTimestamp(competition.date!!)
                    binding.tvLocation.text = competition.place
                    binding.tvTime.text = FirebaseUtil.timestampToString(competition.date!!)

                    obtenerArrayDeFirebase(FirebaseUtil.getCompetitionsRef())

                    binding.asistirSwitch.isChecked =
                        competition.participants.contains(FirebaseUtil.getCurrentUserID())


                }
            }
        } else if (type == "1") {
            compDetailViewModel.event.observe(this) { event ->
                if (event != null) {
                    binding.tvCompetitionName.text = event.name
                    binding.tvTitle.text = event.name
                    binding.tvFecha.text = FirebaseUtil.parseFirebaseTimestamp(event.date!!)
                    binding.tvLocation.text = event.place
                    binding.tvTime.text = FirebaseUtil.timestampToString(event.date!!)

                    obtenerArrayDeFirebase(FirebaseUtil.getEventsRef())

                    binding.asistirSwitch.isChecked =
                        event.participants.contains(FirebaseUtil.getCurrentUserID())
                }
            }
        }
    }

    private fun obtenerArrayDeFirebase(competitionsRef: CollectionReference) {
        competitionsRef.document(id).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val idList = document.get("participants") as? List<String>
                    idList?.let {
                        // Por cada ID en el array, obtener el usuario correspondiente de otra colección
                        CoroutineScope(Dispatchers.Main).launch {
                            it.forEach { id ->
                                val user = obtenerUsuario(id)
                                participants.add(user)
                            }

                            if (participants.size == 0) {
                                binding.tvNoParticipantes.visibility = View.VISIBLE
                            }

                            participantsAdapter.updateList(participants)

                            if (participants.size == 0) {
                                val layoutParams =
                                    binding.tvComentarios.layoutParams as ConstraintLayout.LayoutParams
                                layoutParams.topToBottom = binding.tvNoParticipantes.id
                            }



                            binding.prgbar.visibility = View.INVISIBLE
                            binding.constraintLayout.visibility = View.VISIBLE

                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Manejar errores
            }
    }

    private suspend fun obtenerUsuario(userId: String): User {
        return withContext(Dispatchers.IO) {
            val doc = FirebaseUtil.getUsersRef().document(userId).get().await()
            val user = doc.toObject(User::class.java)
            user!!
        }
    }

    private fun setMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val ubi = LatLng(latitude.toDouble(), longitude.toDouble())
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubi, 15f))
        googleMap.addMarker(MarkerOptions().position(ubi))

    }
}