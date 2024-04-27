package com.example.swimeet.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
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
import com.example.swimeet.viewmodel.ChatRoomViewModel
import com.google.firebase.Timestamp

import java.util.Calendar

class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEventBinding
    private val addEventViewModel: AddEventViewModel by viewModels()
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            14f
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
    }

    private fun setListeners() {
        binding.ivCalendar.setOnClickListener {
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
                    binding.tvFecha.text = selectedDate
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        binding.ivTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->
                    // Aquí puedes manejar la hora seleccionada
                    var selectedTime = ""
                    selectedTime = if (minute == 0) {
                        val minutes = "00"
                        "$hourOfDay:$minutes"
                    } else {
                        "$hourOfDay:$minute"
                    }

                    binding.etTime.hint = ""
                    binding.tvHora.text = selectedTime
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
            val ubi = binding.etUbi

            val date = binding.tvFecha.text.toString()
            val dateArray = date.split("/")
            val day = dateArray[0].toInt()
            val month = dateArray[1].toInt() - 1
            val year = dateArray[2].toInt()

            val time = binding.tvHora.text
            val timeArray = time.split(":")
            val hour = timeArray[0].toInt()
            val minutes = timeArray[1].toInt()

            var distance: Int? = null
            if (binding.etDistance.text.toString() != "") {
                distance = binding.etDistance.text.toString().toInt()
            }

            parseDateTime(day, hour, minutes, year, month)

            if ((type == "Travesía") || (type == "Competición")) {
                val competition = Competition(type = type, name = name, date = Timestamp(calendar.time), distance = distance)
                addEventViewModel.addCompetition(competition)
            } else {
                val event = Event()
            }
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