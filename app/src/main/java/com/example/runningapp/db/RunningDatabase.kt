package com.example.runningapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Run::class],
    version = 1
)
// To tell the database where the type converters are so that they can be used when needed
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase(){
    //Behaviour of this function will be implemented by room so we don't have to take care of it
    abstract fun getRunDao() : RunDAO
}