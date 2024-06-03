package com.example.swimeet.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swimeet.R
import com.example.swimeet.adapter.PlaceAutocompleteAdapter
import com.example.swimeet.data.model.Competition
import com.example.swimeet.data.model.Event
import com.example.swimeet.databinding.ActivityAddEventBinding
import com.example.swimeet.viewmodel.AddEventViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import java.util.Calendar


class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEventBinding
    private val addEventViewModel: AddEventViewModel by viewModels()
    private lateinit var calendar: Calendar
    private lateinit var geoPoint: GeoPoint
    private lateinit var placeName: String
    private lateinit var placesClient: PlacesClient
    private lateinit var autocompleteText: TextInputEditText
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlaceAutocompleteAdapter

    private var selectedPlaceLatLng: Pair<Double, Double>? = null
    private var isSettingText: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBfGyy-1MXE5I9Vpv4vFB08cxK5R_Kq9Sc")
        }

        placesClient = Places.createClient(this)

        autocompleteText = binding.etUbi
        textInputLayout = binding.etUbiParent
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PlaceAutocompleteAdapter(this) { prediction ->
            fetchPlaceDetails(prediction)
        }
        recyclerView.adapter = adapter

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        autocompleteText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isSettingText) {
                    if (!s.isNullOrEmpty()) {
                        performAutocompleteQuery(s.toString())
                    } else {
                        adapter.setPredictions(emptyList())
                        recyclerView.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        initUI()

    }

    private fun fetchPlaceDetails(prediction: AutocompletePrediction) {
        val placeId = prediction.placeId
        val placeFields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                isSettingText = true
                autocompleteText.setText(place.name)
                selectedPlaceLatLng = place.latLng?.let { Pair(it.latitude, it.longitude) }
                recyclerView.visibility = View.GONE
                isSettingText = false
                placeName = place.name
                geoPoint = GeoPoint(selectedPlaceLatLng!!.first, selectedPlaceLatLng!!.second)
            }
    }

    private fun performAutocompleteQuery(query: String) {
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions
                adapter.setPredictions(predictions)
                recyclerView.visibility = if (predictions.isEmpty()) View.GONE else View.VISIBLE
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun initUI() {
        setListeners()
        initObservers()
        initDropDownMenu()
        binding.etDistance.visibility = View.GONE
    }

    private fun initDropDownMenu() {
        val items = resources.getStringArray(R.array.types_events).toList()
        val adapter = ArrayAdapter(applicationContext, R.layout.list_item, items)
        binding.spinner2.setAdapter(adapter)

        binding.spinner2.doOnTextChanged { text, _, _, _ ->
            if (text.toString() == "Travesía") {
                binding.etDistanceParent.visibility = View.VISIBLE
                binding.etDistance.visibility = View.VISIBLE
            } else {
                binding.etDistanceParent.visibility = View.GONE
                binding.etDistance.visibility = View.GONE
            }
        }
    }

    private fun initObservers() {
        addEventViewModel.competitionAdded.observe(this) {
            if (it) {
                Toast.makeText(
                    applicationContext,
                    "Competición añadida con éxito",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()


            } else {
                binding.btnAddEvent.text = "AÑADIENDO..."
            }
        }

        addEventViewModel.eventAdded.observe(this) {
            if (it) {
                Toast.makeText(applicationContext, "Evento añadido con éxito", Toast.LENGTH_SHORT)
                    .show()

                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                binding.btnAddEvent.text = "AÑADIENDO..."
            }
        }
    }

    private fun setListeners() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    // Aquí puedes manejar la fecha seleccionada
                    val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                    binding.etDate.hint = ""
                    binding.etDate.setText(selectedDate)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        binding.etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _: TimePicker, hourOfDay: Int, minute: Int ->
                    // Aquí puedes manejar la hora seleccionada
                    var selectedTime = ""
                    selectedTime = if (minute == 0) {
                        val minutes = "00"
                        "$hourOfDay:$minutes"
                    } else {
                        "$hourOfDay:$minute"
                    }

                    binding.etTime.hint = ""
                    binding.etTime.setText(selectedTime)
                },
                hour,
                minute,
                true
            )

            timePickerDialog.show()
        }

        binding.btnAddEvent.setOnClickListener {
            val type = binding.spinner2.text.toString()
            val name = binding.etEventName.text.toString()
            val link = binding.etLink.text.toString()

            val date = binding.etDate.text.toString()
            val dateArray = date.split("/")
            val day = dateArray[0].toInt()
            val month = dateArray[1].toInt() - 1
            val year = dateArray[2].toInt()

            val time = binding.etTime.text
            val timeArray = time!!.split(":")
            val hour = timeArray[0].toInt()
            val minutes = timeArray[1].toInt()

            var distance: Int? = null
            if (binding.etDistance.text.toString() != "") {
                distance = binding.etDistance.text.toString().toInt()
            }

            parseDateTime(day, hour, minutes, year, month)

            if ((type == "Travesía") || (type == "Competición")) {
                val competition = Competition(
                    place = placeName,
                    location = geoPoint,
                    type = type,
                    name = name,
                    date = Timestamp(calendar.time),
                    distance = distance,
                    link = link,
                    creatorUsername = Firebase.auth.currentUser!!.displayName!!
                )
                addEventViewModel.addCompetition(competition)
            } else {
                val event = Event(
                    place = placeName,
                    location = geoPoint,
                    type = type,
                    name = name,
                    date = Timestamp(calendar.time),
                    link = link,
                    creatorUsername = Firebase.auth.currentUser!!.displayName!!
                )
                addEventViewModel.addEvent(event)
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun parseDateTime(day: Int, hour: Int, minutes: Int, year: Int, month: Int) {
        calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minutes)
    }
}