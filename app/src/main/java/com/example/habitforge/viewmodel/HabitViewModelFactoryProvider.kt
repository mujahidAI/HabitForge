package com.example.habitforge.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.habitforge.data.HabitDatabase

/**
 * HabitViewModelFactoryProvider - Provides configured ViewModel factory instances
 *
 * This object simplifies ViewModel creation by encapsulating the factory setup logic.
 * It retrieves the database instance and creates a factory with all required dependencies.
 *
 * Why Provider Pattern?
 * ────────────────────────────────────────────────────────────
 * - Centralizes factory creation logic
 * - Reduces boilerplate in activities/fragments
 * - Ensures consistent factory configuration
 * - Makes testing easier (can mock the provider)
 *
 * Usage in MainActivity:
 * ```kotlin
 * val factory = HabitViewModelFactoryProvider.provide(application)
 * val viewModel = ViewModelProvider(this, factory).get(HabitViewModel::class.java)
 * ```
 */
object HabitViewModelFactoryProvider {
    /**
     * Provides a configured HabitViewModelFactory
     *
     * Creates a factory with:
     * 1. HabitDao from the singleton database instance
     * 2. Application context for SharedPreferences
     *
     * @param application Application context (prevents memory leaks)
     * @return Configured ViewModelProvider.Factory for creating HabitViewModel
     */
    fun provide(application: Application): ViewModelProvider.Factory {
        // Get DAO from singleton database instance
        val dao = HabitDatabase.getDatabase(application).habitDao()
        // Create and return factory with dependencies
        return HabitViewModelFactory(dao, application)
    }
}
