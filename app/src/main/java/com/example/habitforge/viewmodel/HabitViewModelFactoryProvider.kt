package com.example.habitforge.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.habitforge.data.HabitDatabase

object HabitViewModelFactoryProvider {
    fun provide(application: Application): ViewModelProvider.Factory {
        val dao = HabitDatabase.getDatabase(application).habitDao()
        return HabitViewModelFactory(dao, application)
    }
}
