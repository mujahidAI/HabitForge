package com.example.habitforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.example.habitforge.navigation.AppNavGraph
import com.example.habitforge.viewmodel.HabitViewModel
import com.example.habitforge.viewmodel.HabitViewModelFactoryProvider

/**
 * MainActivity - Entry point and host activity for the HabitForge application
 *
 * This is the single activity that hosts all screens using Jetpack Compose.
 * It follows the single-activity architecture pattern recommended by Google.
 *
 * Responsibilities:
 * ────────────────────────────────────────────────────────────
 * - Initialize the HabitViewModel with factory pattern
 * - Set up Jetpack Compose UI with Material Theme
 * - Host the navigation graph for all screens
 * - Survive configuration changes (ViewModel persists)
 *
 * Architecture:
 * ────────────────────────────────────────────────────────────
 * MainActivity → ViewModel → Navigation → Screens (Composables)
 *
 * Why Single Activity?
 * - Simpler navigation with Compose
 * - Shared ViewModel across screens
 * - Better performance (no activity transitions)
 * - Easier state management
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created
     *
     * Initialization Flow:
     * 1. Get ViewModel factory from provider
     * 2. Create HabitViewModel instance (survives rotation)
     * 3. Set up Compose UI with Material Theme
     * 4. Pass ViewModel to navigation graph
     * 5. Navigation handles screen routing
     *
     * @param savedInstanceState Previous state (null on first launch)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 1 & 2: Get ViewModel through factory provider
        // This ensures proper dependency injection (DAO + Application context)
        val factory = HabitViewModelFactoryProvider.provide(application)
        val habitViewModel = ViewModelProvider(this, factory)
            .get(HabitViewModel::class.java)

        // Step 3 & 4: Set up Compose UI
        setContent {
            // Apply Material 3 theme (colors, typography, shapes)
            MaterialTheme {
                // Pass shared ViewModel into navigation
                // All screens will use the same ViewModel instance
                AppNavGraph(habitViewModel)
            }
        }
    }
}
