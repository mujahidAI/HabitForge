package com.example.habitforge.data

import androidx.room.*

@Dao
interface HabitDao {

    // Get all habits
    @Query("SELECT * FROM habits")
    suspend fun getAllHabits(): List<Habit>

    // Insert habit
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    // Update habit
    @Update
    suspend fun updateHabit(habit: Habit)

    // Delete habit
    @Delete
    suspend fun deleteHabit(habit: Habit)

    // 🔥 NEW — Get single habit by ID (required for streak calculation)
    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    suspend fun getHabitById(habitId: Int): Habit?
}
