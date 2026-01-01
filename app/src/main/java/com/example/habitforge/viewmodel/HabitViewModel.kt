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

/**
 * HabitViewModel - Business logic and state management for habit tracking
 *
 * This ViewModel manages all habit-related operations and state for the application.
 * It follows the MVVM architecture pattern, serving as the intermediary between
 * the UI layer and the data layer.
 *
 * Key Responsibilities:
 * - Manage habit list state (CRUD operations)
 * - Calculate and update habit streaks
 * - Fetch and cache daily motivational quotes
 * - Coordinate between UI and database
 * - Handle asynchronous operations with coroutines
 *
 * State Management:
 * - Uses StateFlow for reactive UI updates
 * - Survives configuration changes (screen rotation)
 * - Scoped to activity lifecycle
 *
 * Dependencies:
 * @param dao HabitDao for database operations
 * @param application Application context for SharedPreferences
 *
 * API Requirements:
 * - Requires API 26+ for LocalDate functionality
 */
class HabitViewModel(
    private val dao: HabitDao,
    application: Application
) : AndroidViewModel(application) {

    // ═══════════════════════════════════════════════════════════
    // STATE MANAGEMENT
    // ═══════════════════════════════════════════════════════════

    /**
     * Private mutable state for habit list
     * Only the ViewModel can modify this state
     */
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    
    /**
     * Public read-only state for habit list
     * UI observes this to display habits reactively
     */
    val habits = _habits.asStateFlow()

    /**
     * SharedPreferences for caching daily quotes
     * Stores quote text and date to avoid unnecessary API calls
     */
    private val prefs =
        getApplication<Application>().getSharedPreferences("daily_quote", Context.MODE_PRIVATE)

    /**
     * Private mutable state for daily quote
     */
    private val _quote = MutableStateFlow("Loading motivational quote...")
    
    /**
     * Public read-only state for daily quote
     * UI observes this to display the quote
     */
    val quote = _quote.asStateFlow()

    // ═══════════════════════════════════════════════════════════
    // HABIT CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════

    /**
     * Loads all habits from the database
     *
     * Fetches the complete list of habits and updates the StateFlow.
     * The UI automatically refreshes when the state changes.
     *
     * Called:
     * - On app launch (HomeScreen)
     * - After adding/updating/deleting habits
     * - When returning to home screen from other screens
     *
     * Runs on: IO dispatcher (coroutine)
     */
    fun loadHabits() {
        viewModelScope.launch {
            _habits.value = dao.getAllHabits()
        }
    }

    /**
     * Creates and saves a new habit to the database
     *
     * Initializes a new habit with:
     * - User-provided title and description
     * - isCompleted = false (not completed yet)
     * - streak = 0 (no streak on first day)
     * - lastCompletedDate = null (never completed)
     *
     * After insertion, refreshes the habit list to show the new habit.
     *
     * @param title Name of the habit (required, validated in UI)
     * @param description Optional details about the habit
     *
     * Example:
     * ```kotlin
     * addHabit("Morning Exercise", "30 minutes cardio")
     * ```
     */
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
            loadHabits()  // Refresh list to show new habit
        }
    }

    /**
     * Updates an existing habit in the database
     *
     * Used when user edits habit details (title, description) from the details screen.
     * Also used internally by setCompleted() to update streak and completion status.
     *
     * @param habit The Habit object with updated values (must have valid ID)
     */
    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            dao.updateHabit(habit)
            loadHabits()  // Refresh list to show updated habit
        }
    }

    /**
     * Permanently deletes a habit from the database
     *
     * This action cannot be undone. All habit data including streak history is lost.
     *
     * @param habit The Habit object to delete (identified by ID)
     */
    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            dao.deleteHabit(habit)
            loadHabits()  // Refresh list to remove deleted habit
        }
    }

    // ═══════════════════════════════════════════════════════════
    // STREAK CALCULATION & COMPLETION LOGIC
    // ═══════════════════════════════════════════════════════════

    /**
     * Marks a habit as completed/uncompleted and calculates the new streak
     *
     * This is the core streak tracking algorithm. It determines whether to:
     * - Increment the streak (consecutive day)
     * - Reset the streak (gap in completion)
     * - Maintain the streak (same day re-check or uncheck)
     *
     * STREAK CALCULATION ALGORITHM:
     * ────────────────────────────────────────────────────────────
     * 1. Retrieve habit from database by ID
     * 2. Get today's date and yesterday's date (ISO format)
     * 3. Compare lastCompletedDate with today/yesterday:
     *
     *    Case 1: First Time Completion (lastCompletedDate == null)
     *    → Set streak = 1
     *
     *    Case 2: Consecutive Day (lastCompletedDate == yesterday)
     *    → Increment streak (e.g., 5 → 6)
     *
     *    Case 3: Same Day Re-check (lastCompletedDate == today)
     *    → Keep current streak (no change)
     *
     *    Case 4: Gap in Days (lastCompletedDate is older than yesterday)
     *    → Reset streak = 1 (streak broken)
     *
     *    Case 5: Unchecking (!isCompleted)
     *    → Keep current streak (don't penalize unchecking)
     *
     * 4. Update habit with new completion status and streak
     * 5. Save to database and refresh UI
     *
     * DATE EXAMPLES:
     * ────────────────────────────────────────────────────────────
     * Today: 2026-01-03
     * Yesterday: 2026-01-02
     *
     * Scenario A: lastCompletedDate = "2026-01-02" (yesterday)
     * → Consecutive! Increment streak: 5 → 6
     *
     * Scenario B: lastCompletedDate = "2025-12-31" (3 days ago)
     * → Gap detected! Reset streak: 5 → 1
     *
     * Scenario C: lastCompletedDate = "2026-01-03" (today)
     * → Already completed today! Keep streak: 5 → 5
     *
     * @param habitId Unique identifier of the habit to update
     * @param isCompleted True to mark complete, false to uncheck
     *
     * @RequiresApi 26 (Android 8.0) for LocalDate API
     */
    @RequiresApi(26)
    fun setCompleted(habitId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            // Step 1: Retrieve the habit from database
            val habit = dao.getHabitById(habitId) ?: return@launch

            // Step 2: Get today's and yesterday's dates in ISO format
            val today = LocalDate.now().toString()        // e.g., "2026-01-03"
            val yesterday = LocalDate.now().minusDays(1).toString()  // e.g., "2026-01-02"

            // Step 3: Calculate new streak based on date comparison
            val newStreak = when {
                // Case 1: First time completing this habit
                habit.lastCompletedDate == null && isCompleted -> 1

                // Case 2: Completed yesterday and completing today (consecutive days)
                habit.lastCompletedDate == yesterday && isCompleted -> habit.streak + 1

                // Case 3: Already completed today (re-checking same day)
                habit.lastCompletedDate == today && isCompleted -> habit.streak

                // Case 4: Gap in completion (streak broken, reset to 1)
                isCompleted -> 1

                // Case 5: Unchecking (keep current streak, don't penalize)
                else -> habit.streak
            }

            // Step 4: Create updated habit with new values
            val updatedHabit = habit.copy(
                isCompleted = isCompleted,
                streak = newStreak,
                // Only update lastCompletedDate if marking as completed
                lastCompletedDate = if (isCompleted) today else habit.lastCompletedDate
            )

            // Step 5: Save to database and refresh UI
            dao.updateHabit(updatedHabit)
            loadHabits()
        }
    }

    // ═══════════════════════════════════════════════════════════
    // DAILY MOTIVATIONAL QUOTE
    // ═══════════════════════════════════════════════════════════

    /**
     * Fetches and caches a daily motivational quote
     *
     * CACHING STRATEGY:
     * ────────────────────────────────────────────────────────────
     * 1. Check SharedPreferences for cached quote and date
     * 2. If cached date matches today → use cached quote (no API call)
     * 3. If new day → fetch fresh quote from ZenQuotes API
     * 4. Cache new quote with today's date for 24 hours
     * 5. If API fails → show fallback motivational quote
     *
     * BENEFITS:
     * - Saves battery (one API call per day)
     * - Works offline (shows cached quote)
     * - Reduces data usage
     * - Consistent quote throughout the day
     *
     * API: ZenQuotes (https://zenquotes.io/api/random)
     * Response: [{"q": "Quote text", "a": "Author"}]
     *
     * QUOTE REFRESH TIMING:
     * ────────────────────────────────────────────────────────────
     * The quote refreshes based on calendar date, not 24-hour timer.
     * Example:
     * - Jan 1, 10:00 AM → Fetch quote A
     * - Jan 1, 11:00 PM → Still show quote A (same day)
     * - Jan 2, 12:01 AM → Fetch quote B (new day)
     *
     * ERROR HANDLING:
     * - Network failure → Show fallback quote
     * - Empty response → Show fallback quote
     * - JSON parsing error → Show fallback quote
     *
     * @RequiresApi 26 (Android 8.0) for LocalDate API
     */
    @RequiresApi(26)
    fun loadDailyQuote() {
        viewModelScope.launch {
            // Step 1: Check cache for existing quote
            val savedQuote = prefs.getString("quote_text", null)
            val savedDate = prefs.getString("quote_date", null)
            val today = LocalDate.now().toString()

            // Step 2: If cached quote is from today, reuse it
            if (savedQuote != null && savedDate == today) {
                _quote.value = savedQuote
                return@launch  // Exit early, no API call needed
            }

            // Step 3: Fetch fresh quote from API (new day or no cache)
            try {
                val response = QuoteApiClient.api.getQuote()
                // Extract quote text from first item, or use fallback
                val newQuote = response.firstOrNull()?.q ?: "Stay consistent. Small steps matter 🚀"
                _quote.value = newQuote

                // Step 4: Cache the new quote with today's date
                prefs.edit()
                    .putString("quote_text", newQuote)
                    .putString("quote_date", today)
                    .apply()

            } catch (e: Exception) {
                // Step 5: Show fallback quote if API fails
                _quote.value = "Believe in yourself. Progress is progress 💪"
            }
        }
    }
}
