package com.example.swimeet.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.swimeet.R
import com.example.swimeet.databinding.ActivityRegisterBinding
import com.example.swimeet.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var categorySelected: String
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        initDropDownMenu()
        initListeners()
        initObservers()

    }

    private fun initObservers() {
        registerViewModel.isCreated.observe(this) { isCreated ->
            if (isCreated) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            } else {
                Toast.makeText(
                    baseContext,
                    "Error al crear el usuario",
                    Toast.LENGTH_SHORT,
                ).show()

                binding.btnRegister.text = "REGISTRAR"
            }
        }

        registerViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.btnRegister.text = "REGISTRANDO USUARIO..."
            }
        }
    }

    private fun initListeners() {
        binding.btnRegister.setOnClickListener {
            if ((binding.etRegisterEmail.text.toString() != "") && (binding.etRegisterPassword.text.toString() != "")
                && (binding.etName.text.toString() != "") && (binding.etUsername.text.toString() != "")
                && (binding.spinner.text.toString() != "Seleccionar categoria")
            ) {

                val email = binding.etRegisterEmail.text.toString()
                val password = binding.etRegisterPassword.text.toString()
                val name = binding.etName.text.toString()
                val category = binding.spinner.text.toString()
                val username = binding.etUsername.text.toString()
                val genre = binding.etGenre.text.toString()

                registerViewModel.signUp(email, username, password, name, category, genre, applicationContext)

            } else {
                Toast.makeText(
                    baseContext,
                    "Rellene todos los campos",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        binding.tvIniciaSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initDropDownMenu() {
        val items = resources.getStringArray(R.array.categorias_edades_natacion).toList()
        val genres = resources.getStringArray(R.array.genres).toList()
        val adapter = ArrayAdapter(applicationContext, R.layout.list_item, items)
        val adapterGenres = ArrayAdapter(applicationContext, R.layout.list_item, genres)
        binding.spinner.setAdapter(adapter)
        binding.etGenre.setAdapter(adapterGenres)
    }
}