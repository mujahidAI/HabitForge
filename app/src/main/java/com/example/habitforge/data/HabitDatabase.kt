package com.example.habitforge.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * HabitDatabase - Room database for persistent habit storage
 *
 * This class provides the main database instance for the application using Room.
 * It follows the Singleton pattern to ensure only one database instance exists.
 *
 * Database Configuration:
 * - Entities: Habit (single table)
 * - Version: 2 (incremented when schema changes)
 * - Migration Strategy: Destructive (data is cleared on schema changes)
 *
 * Schema History:
 * - Version 1: Initial schema (title, description, isCompleted)
 * - Version 2: Added streak tracking (streak, lastCompletedDate fields)
 *
 * Note: fallbackToDestructiveMigration() is used for development.
 * In production, implement proper migration strategies to preserve user data.
 *
 * @property habitDao Provides access to habit database operations
 */
@Database(
    entities = [Habit::class],
    version = 2,
    exportSchema = false  // Set to true in production for schema versioning
)
abstract class HabitDatabase : RoomDatabase() {

    /**
     * Provides access to the HabitDao for database operations
     *
     * @return HabitDao instance for CRUD operations
     */
    abstract fun habitDao(): HabitDao

    companion object {
        /**
         * Singleton instance of the database
         * @Volatile ensures visibility across threads
         */
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        /**
         * Gets or creates the database instance (Singleton pattern)
         *
         * Uses double-checked locking to ensure thread-safe initialization.
         * The database is created only once and reused for all subsequent calls.
         *
         * Why Singleton?
         * - Database creation is expensive
         * - Prevents multiple database instances
         * - Ensures data consistency across the app
         *
         * @param context Application context (prevents memory leaks)
         * @return The singleton HabitDatabase instance
         */
        fun getDatabase(context: Context): HabitDatabase {
            // Return existing instance if available
            return INSTANCE ?: synchronized(this) {
                // Double-check inside synchronized block
                val instance = Room.databaseBuilder(
                    context.applicationContext,  // Use app context to avoid leaks
                    HabitDatabase::class.java,
                    "habit_database"  // Database file name
                )
                    // Destructive migration: Deletes and recreates tables on version change
                    // TODO: Implement proper migrations for production to preserve user data
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
