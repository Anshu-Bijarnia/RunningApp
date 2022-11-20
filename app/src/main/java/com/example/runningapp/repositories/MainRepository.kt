package com.example.runningapp.repositories

import com.example.runningapp.db.Run
import com.example.runningapp.db.RunDAO
import javax.inject.Inject

//Its main job is to provide functions of our database using the runDao
//Its job is to collect the data from all our data sources (in this case we only have one data source), if there would have been an api we would also get its data inside our reepository
class MainRepository @Inject constructor(
    val runDAO: RunDAO
){
    suspend fun insertRun(run: Run) = runDAO.insertRun(run)

    suspend fun deleteRun(run:Run) = runDAO.deleteRun(run)

    // For sorting the data
    // This is not a suspend function because this function returns a live data object and livedata is asynchronous by default so we dont need to launch this in a coroutine
    fun getAllRunsSortedByDate() = runDAO.getAllRunsSortedByDate()

    fun getAllRunsSortedByDistance() = runDAO.getAllRunsSortedByDistance()

    fun getAllRunsSortedByTimeInMillis() = runDAO.getAllRunsSortedByTimeInMillis()

    fun getAllRunsSortedByAvgSpeed() = runDAO.getAllRunsSortedByAvgSpeed()

    fun getAllRunsSortedByCaloriesBurned() = runDAO.getAllRunsSortedByCaloriesBurned()

    // For getting properties for statistics
    fun getTotalAvgSpeed() = runDAO.getTotalAvgSpeed()

    fun getTotalDistance() = runDAO.getTotalDistance()

    fun getTotalCaloriesBurned() = runDAO.getTotalCaloriesBurned()

    fun getTotalTimeInMillis() = runDAO.getTotalTimeInMillis()
}