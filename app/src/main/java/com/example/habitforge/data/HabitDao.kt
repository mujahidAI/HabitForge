package com.example.habitforge.data

import androidx.room.*

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits")
    suspend fun getAllHabits(): List<Habit>

    @Insert
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    // Toggle complete state
    @Query("UPDATE habits SET isCompleted = :completed WHERE id = :habitId")
    suspend fun setHabitCompleted(habitId: Int, completed: Boolean)
}
