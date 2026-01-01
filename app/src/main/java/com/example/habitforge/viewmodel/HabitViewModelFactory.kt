package com.example.habitforge.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habitforge.data.HabitDao

/**
 * HabitViewModelFactory - Factory for creating HabitViewModel instances
 *
 * This factory is required because HabitViewModel has constructor dependencies
 * (HabitDao and Application) that cannot be provided by the default ViewModel creation.
 *
 * Why Factory Pattern?
 * ────────────────────────────────────────────────────────────
 * - ViewModels with dependencies need custom creation logic
 * - Ensures proper dependency injection
 * - Maintains single ViewModel instance per activity/fragment
 * - Survives configuration changes (screen rotation)
 *
 * Usage:
 * ```kotlin
 * val factory = HabitViewModelFactory(dao, application)
 * val viewModel = ViewModelProvider(this, factory).get(HabitViewModel::class.java)
 * ```
 *
 * @param dao HabitDao instance for database operations
 * @param application Application context for SharedPreferences
 */
class HabitViewModelFactory(
    private val dao: HabitDao,
    private val application: Application
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given ViewModel class
 *
     * Called by ViewModelProvider when a ViewModel is requested.
     * Checks if the requested class is HabitViewModel and creates it with dependencies.
     *
     * @param modelClass The class of the ViewModel to create
     * @return A new instance of HabitViewModel with injected dependencies
     * @throws IllegalArgumentException if modelClass is not HabitViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(dao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
