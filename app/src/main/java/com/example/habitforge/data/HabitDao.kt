package com.example.habitforge.data

import androidx.room.*

/**
 * HabitDao - Data Access Object for habit database operations
 *
 * This interface defines all database operations for the Habit entity.
 * Room generates the implementation automatically at compile time.
 * All methods are suspend functions for use with Kotlin coroutines.
 *
 * Usage: Access through HabitDatabase.habitDao()
 */
@Dao
interface HabitDao {

    /**
     * Retrieves all habits from the database
     *
     * Returns all habit records ordered by insertion (ID ascending).
     * Used to populate the main habit list on the home screen.
     *
     * SQL: SELECT * FROM habits
     *
     * @return List of all Habit objects (empty list if no habits exist)
     */
    @Query("SELECT * FROM habits")
    suspend fun getAllHabits(): List<Habit>

    /**
     * Inserts a new habit into the database
     *
     * If a habit with the same ID already exists, it will be replaced.
     * The ID is auto-generated if not provided (default = 0).
     *
     * Conflict Strategy: REPLACE - overwrites existing habit with same ID
     *
     * @param habit The Habit object to insert (ID will be auto-generated)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    /**
     * Updates an existing habit in the database
     *
     * Updates all fields of the habit matching the provided ID.
     * Used when user edits habit details or when streak/completion status changes.
     *
     * @param habit The Habit object with updated values (must have valid ID)
     */
    @Update
    suspend fun updateHabit(habit: Habit)

    /**
     * Deletes a habit from the database
     *
     * Permanently removes the habit record. This action cannot be undone.
     * Deletion is based on the habit's ID.
     *
     * @param habit The Habit object to delete (must have valid ID)
     */
    @Delete
    suspend fun deleteHabit(habit: Habit)

    /**
     * Retrieves a single habit by its unique ID
     *
     * Used for streak calculation and habit detail screen.
     * Returns null if no habit with the given ID exists.
     *
     * SQL: SELECT * FROM habits WHERE id = :habitId LIMIT 1
     *
     * @param habitId The unique identifier of the habit to retrieve
     * @return The Habit object if found, null otherwise
     */
    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    suspend fun getHabitById(habitId: Int): Habit?
}
