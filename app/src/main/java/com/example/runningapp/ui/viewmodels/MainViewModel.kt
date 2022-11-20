package com.example.runningapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.runningapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Its job is to collect the data from our repository and provide it for all the fragments that will need this main view model
// That's why an instance of our main repository is needed inside our main view model
@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository //There is no provide function for this in the AppModule but it will still
    // work because the only parameter required for creating this is runDao and dagger already knows how to create runDao hence knows how to create MainRepository
):ViewModel(){
}