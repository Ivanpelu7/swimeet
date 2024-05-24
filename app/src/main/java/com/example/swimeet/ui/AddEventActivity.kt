package com.example.swimeet.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.viewModels
import com.example.swimeet.R
import com.example.swimeet.adapter.SpinnerAdapter
import com.example.swimeet.data.model.Competition
import com.example.swimeet.data.model.Event
import com.example.swimeet.databinding.ActivityAddEventBinding
import com.example.swimeet.viewmodel.AddEventViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

import java.util.Calendar
import java.util.logging.Logger

class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEventBinding
    private val addEventViewModel: AddEventViewModel by viewModels()
    private lateinit var calendar: Calendar
    private lateinit var geoPoint: GeoPoint
    private lateinit var placeName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBfGyy-1MXE5I9Vpv4vFB08cxK5R_Kq9Sc")
        }

        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setHint("Ubicación")
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let {
                    val lat = place.latLng?.latitude
                    val long = place.latLng?.longitude
                    placeName = place.name!!
                    place.address
                    geoPoint = GeoPoint(lat!!, long!!)
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                //<--
            }
        })

        initUI()

    }

    private fun initUI() {
        setListeners()
        initObservers()
        initSpinner()
        binding.etDistance.visibility = View.GONE
    }

    private fun initSpinner() {
        val types = resources.getStringArray(R.array.types_events)
        val adapter = SpinnerAdapter(
            this,
            android.R.layout.simple_spinner_item,
            types,
            "poppins_medium.ttf",
            16f
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter

        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 1) binding.etDistance.visibility = View.VISIBLE else binding.etDistance.visibility = View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun initObservers() {
        addEventViewModel.competitionAdded.observe(this) {
            if (it) {
                Toast.makeText(applicationContext, "Competición añadida con éxito", Toast.LENGTH_SHORT).show()

                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()


            } else {
                binding.btnAddEvent.text = "AÑADIENDO..."
            }
        }

        addEventViewModel.eventAdded.observe(this) {
            if (it) {
                Toast.makeText(applicationContext, "Evento añadido con éxito", Toast.LENGTH_SHORT).show()

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
            val type = binding.spinnerType.selectedItem.toString()
            val name = binding.etEventName.text.toString()
            // val ubi = binding.etUbi

            val date = binding.etDate.text.toString()
            val dateArray = date.split("/")
            val day = dateArray[0].toInt()
            val month = dateArray[1].toInt() - 1
            val year = dateArray[2].toInt()

            val time = binding.etTime.text
            val timeArray = time.split(":")
            val hour = timeArray[0].toInt()
            val minutes = timeArray[1].toInt()

            var distance: Int? = null
            if (binding.etDistance.text.toString() != "") {
                distance = binding.etDistance.text.toString().toInt()
            }

            parseDateTime(day, hour, minutes, year, month)

            if ((type == "Travesía") || (type == "Competición")) {
                val competition = Competition(place = placeName, location = geoPoint, type = type, name = name, date = Timestamp(calendar.time), distance = distance)
                addEventViewModel.addCompetition(competition)
            } else {
                val event = Event(place = placeName, location = geoPoint, type = type, name = name, date = Timestamp(calendar.time))
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