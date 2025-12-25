package com.example.habitforge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habitforge.data.HabitDao

class HabitViewModelFactory(private val dao: HabitDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            return HabitViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
