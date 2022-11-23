package com.example.runningapp.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningapp.db.Run
import com.example.runningapp.other.SortType
import com.example.runningapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Its job is to collect the data from our repository and provide it for all the fragments that will need this main view model
// That's why an instance of our main repository is needed inside our main view model
@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository //There is no provide function for this in the AppModule but it will still
    // work because the only parameter required for creating this is runDao and dagger already knows how to create runDao hence knows how to create MainRepository
):ViewModel(){

    private val runsSortedByDate = mainRepository.getAllRunsSortedByDate()
    private val runsSortedByDistance = mainRepository.getAllRunsSortedByDistance()
    private val runsSortedByCaloriesBurned = mainRepository.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByTimeInMillis = mainRepository.getAllRunsSortedByTimeInMillis()
    private val runsSortedByAvgSpeed = mainRepository.getAllRunsSortedByAvgSpeed()

    // This is a type of livedata that allows us to merge several livedata together
    val runs = MediatorLiveData<List<Run>>()
    var sortType = SortType.DATE

    init {
        runs.addSource(runsSortedByDate){ result ->
            if (sortType == SortType.DATE){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByAvgSpeed){ result ->
            if (sortType == SortType.AVG_SPEED){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByCaloriesBurned){ result ->
            if (sortType == SortType.CALORIES_BURNED){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByDistance){ result ->
            if (sortType == SortType.DISTANCE){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByTimeInMillis){ result ->
            if (sortType == SortType.RUNNING_TIME){
                result?.let { runs.value = it }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when (sortType){
        SortType.DATE -> runsSortedByDate.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let { runs.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }

    fun insertRun(run : Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }
}