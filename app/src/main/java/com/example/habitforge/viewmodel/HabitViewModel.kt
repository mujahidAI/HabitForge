package com.example.habitforge.viewmodel

import android.app.Application
import android.content.Context
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitforge.data.Habit
import com.example.habitforge.data.HabitDao
import com.example.habitforge.data.QuoteApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitViewModel(
    private val dao: HabitDao,
    application: Application
) : AndroidViewModel(application) {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits = _habits.asStateFlow()

    // -----------------------------------------------------------
    // 💬 DAILY QUOTE STATE (saved for 24 hours)
    // -----------------------------------------------------------
    private val prefs =
        getApplication<Application>().getSharedPreferences("daily_quote", Context.MODE_PRIVATE)

    private val _quote = MutableStateFlow("Loading motivational quote...")
    val quote = _quote.asStateFlow()

    // -----------------------------------------------------------
    // 📌 HABIT CRUD
    // -----------------------------------------------------------
    fun loadHabits() {
        viewModelScope.launch {
            _habits.value = dao.getAllHabits()
        }
    }

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

    // -----------------------------------------------------------
    // 🔥 STREAK + COMPLETION LOGIC
    // -----------------------------------------------------------
    @RequiresApi(26)
    fun setCompleted(habitId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            val habit = dao.getHabitById(habitId) ?: return@launch

            val today = LocalDate.now().toString()
            val yesterday = LocalDate.now().minusDays(1).toString()

            val newStreak = when {
                habit.lastCompletedDate == null && isCompleted -> 1
                habit.lastCompletedDate == yesterday && isCompleted -> habit.streak + 1
                habit.lastCompletedDate == today && isCompleted -> habit.streak
                isCompleted -> 1
                else -> habit.streak
            }

            val updatedHabit = habit.copy(
                isCompleted = isCompleted,
                streak = newStreak,
                lastCompletedDate = if (isCompleted) today else habit.lastCompletedDate
            )

            dao.updateHabit(updatedHabit)
            loadHabits()
        }
    }

    // -----------------------------------------------------------
    // ☀️ DAILY MOTIVATIONAL QUOTE (1 per day)
    // -----------------------------------------------------------
    @RequiresApi(26)
    fun loadDailyQuote() {
        viewModelScope.launch {
            val savedQuote = prefs.getString("quote_text", null)
            val savedDate = prefs.getString("quote_date", null)
            val today = LocalDate.now().toString()

            // Reuse saved quote for the day
            if (savedQuote != null && savedDate == today) {
                _quote.value = savedQuote
                return@launch
            }

            try {
                val response = QuoteApiClient.api.getQuote() // 👈 Correct function name
                val newQuote = response.firstOrNull()?.q ?: "Stay consistent. Small steps matter 🚀"
                _quote.value = newQuote

                // Save for 24h
                prefs.edit()
                    .putString("quote_text", newQuote)
                    .putString("quote_date", today)
                    .apply()

            } catch (e: Exception) {
                _quote.value = "Believe in yourself. Progress is progress 💪"
            }
        }
    }
}
