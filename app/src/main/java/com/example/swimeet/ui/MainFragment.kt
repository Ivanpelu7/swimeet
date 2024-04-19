package com.example.swimeet.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.swimeet.adapter.ViewPagerAdapter
import com.example.swimeet.data.model.Advertisement
import com.example.swimeet.databinding.FragmentMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {
        initListeners()
        initViewPager()
    }

    private fun initViewPager() {
        val advertisementList = mutableListOf<Advertisement>()
        advertisementList.add(Advertisement(authorUsername = "Ivanpelu7", message = "Hola que tal", title = "Primer mensaje en el tablón"))
        advertisementList.add(Advertisement(authorUsername = "UsuarioInventado", message = "Que ganas tengo de irme a casa", title = "Segundo mensaje en el tablón"))

        val adapter = ViewPagerAdapter(advertisementList)
        binding.viewPager.adapter = adapter
    }

    private fun initListeners() {
        binding.buttonLogOut.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

}