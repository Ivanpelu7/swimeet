package com.example.swimeet.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.swimeet.data.model.Record
import com.example.swimeet.databinding.ActivitySwimEventDetailBinding
import com.example.swimeet.util.FirebaseUtil
import com.example.swimeet.viewmodel.SwimEventDetailViewModel

class SwimEventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySwimEventDetailBinding
    private val vm: SwimEventDetailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwimEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        val name = intent.getStringExtra("name")
        binding.tvCompetitionName.text = name
        setListeners()
        setObservers()
        loadRecords(name!!)

    }

    private fun setObservers() {
        vm.loading.observe(this) { loading ->
            if (loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                binding.mainLayout.visibility = View.VISIBLE
            }
        }

        vm.records.observe(this) { records ->
            for (record in records) {
                setRecordLayout(record)
            }
        }
    }

    private fun setRecordLayout(record: Record) {
        if (record.genre == "Masculino") setMascRecords(record) else setFemRecords(record)
    }

    private fun setMascRecords(record: Record) {
        when (record.category.lowercase()) {
            "benjamín" -> {
                binding.tvBenjaminTime.text = FirebaseUtil.milisecondsToSwimTime(record.time)
                binding.tvUsername.text = record.username
            }

            "alevín" -> {
                binding.tvAlevinTime.text = FirebaseUtil.milisecondsToSwimTime(record.time)
                binding.tvUsernameAlevin.text = record.username
            }

            "infantil" -> {
                binding.tvInfantilTime.text = FirebaseUtil.milisecondsToSwimTime(record.time)
                binding.tvUsernameInfantil.text = record.username
            }

            "junior" -> {
                binding.tvJuniorTime.text = FirebaseUtil.milisecondsToSwimTime(record.time)
                binding.tvUsernameJunior.text = record.username
            }

            "absoluto" -> {
                binding.tvAbsolutoTime.text = FirebaseUtil.milisecondsToSwimTime(record.time)
                binding.tvUsernameAbsoluto.text = record.username
            }
        }
    }

    private fun setFemRecords(record: Record) {
        when (record.category.lowercase()) {
            "benjamín" -> {
                binding.tvBenjaminTimeFem.text = FirebaseUtil.milisecondsToSwimTime(record.time)
                binding.tvUsernameFem.text = record.username
            }

            "alevín" -> {
                binding.tvAlevinTimeFem.text = FirebaseUtil.milisecondsToSwimTime(record.time)
                binding.tvUsernameAlevinFem.text = record.username
            }

            "infantil" -> {
                binding.tvInfantilTimeFem.text = FirebaseUtil.milisecondsToSwimTime(record.time)
                binding.tvUsernameInfantilFem.text = record.username
            }

            "junior" -> {
                binding.tvJuniorTimeFem.text = FirebaseUtil.milisecondsToSwimTime(record.time)
                binding.tvUsernameJuniorFem.text = record.username
            }

            "absoluto" -> {
                binding.tvAbsolutoTimeFem.text = FirebaseUtil.milisecondsToSwimTime(record.time)
                binding.tvUsernameAbsolutoFem.text = record.username
            }
        }
    }

    private fun loadRecords(record: String) {
        vm.getRecords(record)
    }

    private fun setListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}