package com.example.runningapp.ui.fragments

import android.content.SharedPreferences
import android.os.Binder
import android.os.Bundle
import android.view.View
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.ActivityMainBinding
import com.example.runningapp.databinding.FragmentSetupBinding
import com.example.runningapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningapp.other.Constants.KEY_NAME
import com.example.runningapp.other.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {
    lateinit var binding: FragmentSetupBinding

    @Inject
    lateinit var sharedPref : SharedPreferences

    @set:Inject
    var isFirstAppOpen = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSetupBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        if(!isFirstAppOpen){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment,true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )
        }

        binding.apply {
            tvContinue.setOnClickListener {
                val success = writePersonalDataToSharedPref()
                if (success){
                    findNavController().navigate(R.id.action_setupFragment_to_runFragment)
                }else{
                    Snackbar.make(requireView(),"Please fill all the fields",Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun writePersonalDataToSharedPref(): Boolean{
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()

        if (name.isEmpty() || weight.isEmpty()){
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME,name)
            .putFloat(KEY_WEIGHT,weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE,false)
            .apply() // this is asynchronous where is .commit() is synchronous

        val toolbarText = "Let's go, $name!"
        (requireActivity() as AppCompatActivity).supportActionBar?.title = toolbarText
        return true
    }
}