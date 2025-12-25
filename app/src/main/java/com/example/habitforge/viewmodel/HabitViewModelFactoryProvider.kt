package com.example.habitforge.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitforge.data.HabitDatabase

object HabitViewModelFactoryProvider {
    @Composable
    fun provide(): HabitViewModelFactory {
        val context = androidx.compose.ui.platform.LocalContext.current
        val dao = HabitDatabase.getDatabase(context).habitDao()
        return HabitViewModelFactory(dao)
    }
}
