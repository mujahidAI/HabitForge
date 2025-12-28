package com.example.habitforge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitforge.data.Habit
import com.example.habitforge.data.HabitDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate   // ⬅️ Required for streak dates

class HabitViewModel(private val dao: HabitDao) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits = _habits.asStateFlow()

    // Load all habits from DB
    fun loadHabits() {
        viewModelScope.launch {
            _habits.value = dao.getAllHabits()
        }
    }

    // Add new habit
    fun addHabit(title: String, description: String) {
        viewModelScope.launch {
            val habit = Habit(
                title = title,
                description = description,
                isCompleted = false,
                streak = 0,
                lastCompletedDate = null
            )
            dao.insertHabit(habit)
            loadHabits()
        }
    }

    // Update habit data
    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            dao.updateHabit(habit)
            loadHabits()
        }
    }

    // Delete habit
    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            dao.deleteHabit(habit)
            loadHabits()
        }
    }

    // 🔥 Toggle completion + streak logic
    fun setCompleted(habitId: Int, isCompleted: Boolean) {
        viewModelScope.launch {

            // Load existing habit
            val habit = dao.getHabitById(habitId) ?: return@launch

            val today = LocalDate.now().toString()
            val yesterday = LocalDate.now().minusDays(1).toString()

            // Calculate new streak
            val newStreak = when {
                habit.lastCompletedDate == null && isCompleted -> 1
                habit.lastCompletedDate == yesterday && isCompleted -> habit.streak + 1
                habit.lastCompletedDate == today && isCompleted -> habit.streak
                isCompleted -> 1
                else -> habit.streak
            }

            // Create updated habit
            val updatedHabit = habit.copy(
                isCompleted = isCompleted,
                streak = newStreak,
                lastCompletedDate = if (isCompleted) today else habit.lastCompletedDate
            )

            // Save changes
            dao.updateHabit(updatedHabit)
            loadHabits()
        }
    }
}
