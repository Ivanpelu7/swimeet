package com.example.swimeet.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swimeet.R
import com.example.swimeet.adapter.ParticipantsAdapter
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
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CompetitionDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityCompetitionDetailBinding
    private val compDetailViewModel = CompetitionDetailViewModel()
    private lateinit var id: String
    private lateinit var name: String
    private lateinit var longitude: String
    private lateinit var latitude: String
    private lateinit var type: String
    private var participants: MutableList<User> = mutableListOf()
    private var participantsAdapter = ParticipantsAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompetitionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.prgbar.visibility = View.VISIBLE
        getIntents()
        initRecyclerView()

        if (type == "0") {
            compDetailViewModel.getCompetitionInfo(id)
        } else if (type == "1") {
            compDetailViewModel.getEventInfo(id)
        }

        initObservers()
        initListeners()
        setMap()
    }

    private fun initRecyclerView() {
        binding.rvParticipantes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = participantsAdapter
        }
    }

    private fun initListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
                                    break
                                }
                            }

                        }
                }
            }
        }
    }

    private fun getIntents() {
        id = intent.getStringExtra("id").toString()
        name = intent.getStringExtra("name").toString()
        longitude = intent.getStringExtra("longitude").toString()
        latitude = intent.getStringExtra("latitude").toString()
        type = intent.getStringExtra("type").toString()
    }

    private fun initObservers() {
        if (type == "0") {
            compDetailViewModel.competition.observe(this) { competition ->
                if (competition != null) {
                    binding.tvCompetitionName.text = competition.name
                    binding.tvTitle.text = competition.name
                    binding.tvFecha.text = FirebaseUtil.parseFirebaseTimestamp(competition.date!!)
                    binding.tvLocation.text = competition.place

                    obtenerArrayDeFirebase(FirebaseUtil.getCompetitionsRef())

                    binding.constraintLayout.visibility = View.VISIBLE
                    binding.prgbar.visibility = View.INVISIBLE

                    binding.asistirSwitch.isChecked =
                        competition.participants.contains(FirebaseUtil.getCurrentUserID())


                } else {
                    binding.constraintLayout.visibility = View.INVISIBLE
                    binding.prgbar.visibility = View.VISIBLE
                }
            }
        } else if (type == "1") {
            compDetailViewModel.event.observe(this) { event ->
                if (event != null) {
                    binding.tvCompetitionName.text = event.name
                    binding.tvTitle.text = event.name
                    binding.tvFecha.text = FirebaseUtil.parseFirebaseTimestamp(event.date!!)
                    binding.tvLocation.text = event.place

                    obtenerArrayDeFirebase(FirebaseUtil.getEventsRef())

                    binding.constraintLayout.visibility = View.VISIBLE
                    binding.prgbar.visibility = View.INVISIBLE

                    binding.asistirSwitch.isChecked =
                        event.participants.contains(FirebaseUtil.getCurrentUserID())

                } else {
                    binding.constraintLayout.visibility = View.INVISIBLE
                    binding.prgbar.visibility = View.VISIBLE
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

                            participantsAdapter.updateList(participants)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Manejar errores
            }
        /*
                binding.asistirSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
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
                                            break
                                        }
                                    }

                                }
                        }

                    }
                }

         */
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