package com.example.runningapp.ui.fragments

import android.os.Binder
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentSetupBinding

class SetupFragment : Fragment(R.layout.fragment_setup) {
    lateinit var binding: FragmentSetupBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSetupBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvContinue.setOnClickListener {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }
        }
    }
}