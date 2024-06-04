package com.example.swimeet.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swimeet.R
import com.example.swimeet.adapter.MarksAdapter
import com.example.swimeet.data.model.Mark
import com.example.swimeet.databinding.ActivityAllMarksBinding
import com.example.swimeet.util.FirebaseUtil
import com.example.swimeet.viewmodel.PerfilViewModel

class AllMarksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllMarksBinding
    private lateinit var marksAdapter: MarksAdapter
    private val perfilViewModel: PerfilViewModel by viewModels()
    private lateinit var etCompetitions: AutoCompleteTextView
    private lateinit var etPrueba: AutoCompleteTextView
    private lateinit var marksList: List<Mark>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllMarksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        setListeners()
        setRecycler()
        setObservers()
        loadData()
    }

    private fun loadData() {
        perfilViewModel.getMarks()
    }

    private fun setObservers() {
        perfilViewModel.markList.observe(this) {
            marksAdapter.updateList(it)
            marksList = it
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
    }

    private fun setRecycler() {
        marksAdapter = MarksAdapter()
        binding.rvMisMarcas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = marksAdapter
        }
    }

    private fun showCustomDialogue() {
        val dialogView = layoutInflater.inflate(R.layout.filter_dialog, null)
        etCompetitions = dialogView.findViewById(R.id.etCompetition)
        etPrueba = dialogView.findViewById(R.id.etPrueba)
        val btnFilter = dialogView.findViewById<Button>(R.id.btnFiltrar)

        initDropDownMenu()

        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        btnFilter.setOnClickListener {
            val competitionName = etCompetitions.text.toString()
            val swimEvent = etPrueba.text.toString()

            marksAdapter.updateList(filter(competitionName, swimEvent))

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun filter(competitionName: String, swimEvent: String): List<Mark> {
        return marksList.filter { mark ->
            (competitionName.isEmpty() || mark.competition == competitionName) &&
                    (swimEvent.isEmpty() || mark.swimEvent == swimEvent)
        }
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

    private fun setListeners() {
        binding.btnFiltrar.setOnClickListener {
            showCustomDialogue()
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnDeleteFilters.setOnClickListener {
            marksAdapter.updateList(marksList)
        }

    }
}