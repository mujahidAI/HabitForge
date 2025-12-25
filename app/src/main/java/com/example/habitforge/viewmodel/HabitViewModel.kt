package com.example.habitforge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitforge.data.Habit
import com.example.habitforge.data.HabitDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HabitViewModel(private val dao: HabitDao) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits = _habits.asStateFlow()

    fun loadHabits() {
        viewModelScope.launch {
            _habits.value = dao.getAllHabits()
        }
    }

    fun addHabit(title: String, description: String) {
        viewModelScope.launch {
            val habit = Habit(title = title, description = description)
            dao.insertHabit(habit)
            loadHabits()
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            dao.updateHabit(habit)
            loadHabits()
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            dao.deleteHabit(habit)
            loadHabits()
        }
    }

    fun setCompleted(habitId: Int, completed: Boolean) {
        viewModelScope.launch {
            dao.setHabitCompleted(habitId, completed)
            loadHabits()
        }
    }
}
