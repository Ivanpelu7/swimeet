package com.example.swimeet.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swimeet.R
import com.example.swimeet.adapter.SwimEventsAdapter
import com.example.swimeet.data.model.SwimEvent
import com.example.swimeet.databinding.FragmentMainBinding
import com.example.swimeet.databinding.FragmentRecordsBinding
import java.util.Locale.filter

class RecordsFragment : Fragment() {

    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!
    private lateinit var swimAdapter: SwimEventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecordsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    private fun initUI() {
        setupRecyclerView()
        setListeners()
    }

    private fun setListeners() {
        binding.etFiltrarPruebas.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }

        })
    }

    private fun filter(s: String) {
        val filteredList = getSwimEventsList().filter { it.name.contains(s, ignoreCase = true) }
        swimAdapter.updateList(filteredList)
    }

    private fun setupRecyclerView() {
       swimAdapter = SwimEventsAdapter(getSwimEventsList())
       binding.rvSwimEvents.apply {
           layoutManager = LinearLayoutManager(context)
           adapter = swimAdapter
       }
    }

    private fun getSwimEventsList(): List<SwimEvent> {
        return listOf(
            SwimEvent("50 Libre"),
            SwimEvent("100 Libre"),
            SwimEvent("200 Libre"),
            SwimEvent("400 Libre"),
            SwimEvent("800 Libre"),
            SwimEvent("1500 Libre"),
            SwimEvent("50 Mariposa"),
            SwimEvent("100 Mariposa"),
            SwimEvent("200 Mariposa"),
            SwimEvent("50 Espalda"),
            SwimEvent("100 Espalda"),
            SwimEvent("200 Espalda"),
            SwimEvent("50 Braza"),
            SwimEvent("100 Braza"),
            SwimEvent("200 Braza"),
            SwimEvent("100 Estilos"),
            SwimEvent("200 Estilos"),
            SwimEvent("400 Estilos")
        )
    }

}