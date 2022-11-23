package com.example.runningapp.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.runningapp.R
import com.example.runningapp.db.RunningDatabase
import com.example.runningapp.other.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Dagger needs a manual on how it should create this running database so that it can be injected in our repository later on and they are put in the modules, manual is just a function
@Module
@InstallIn(SingletonComponent::class) //We tell dagger hilt that it should install this module inside this singleton component class, these components are used to determine when
// the objects in our app module are created and destroyed, all the modules that we define here will be created with the onCreate func in BaseApplication, so this means that they will exist
// for the whole lifetime of our app, when the user quits the app it will destroy all the dependencies
object AppModule {

    @Singleton // Whenever we want to inject this database in some classes, a new instance will be created each time, to avoid this singleton is used
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db : RunningDatabase) = db.getRunDao()
}