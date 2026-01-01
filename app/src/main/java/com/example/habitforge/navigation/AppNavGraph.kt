package com.example.habitforge.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habitforge.viewmodel.HabitViewModel
import com.example.habitforge.ui.AddHabitScreen
import com.example.habitforge.ui.HabitDetailsScreen
import com.example.habitforge.ui.HomeScreen

/**
 * AppNavGraph - Navigation configuration for the entire application
 *
 * Defines all navigation routes and screen destinations using Jetpack Compose Navigation.
 * Implements type-safe navigation with route parameters.
 *
 * Navigation Routes:
 * ────────────────────────────────────────────────────────────
 * - "home"              → HomeScreen (start destination)
 * - "add"               → AddHabitScreen
 * - "details/{habitId}" → HabitDetailsScreen (with Int parameter)
 *
 * Navigation Flow:
 * ────────────────────────────────────────────────────────────
 * Home → Add (FAB click) → Save → Back to Home
 * Home → Details (card click) → Update/Delete → Back to Home
 *
 * Shared ViewModel:
 * ────────────────────────────────────────────────────────────
 * All screens receive the same ViewModel instance, ensuring:
 * - Consistent data across screens
 * - No need to pass data between screens
 * - Automatic UI updates when data changes
 *
 * @param viewModel Shared HabitViewModel instance for all screens
 */
@Composable
fun AppNavGraph(viewModel: HabitViewModel) {
    // Create navigation controller (manages back stack and navigation)
    val navController = rememberNavController()

    // Define navigation host with routes
    NavHost(
        navController = navController,
        startDestination = "home"  // App opens to home screen
    ) {

        /**
         * Home Screen Route - Main habit list
         *
         * Displays:
         * - Daily motivational quote
         * - List of all habits with streaks
         * - FAB to add new habits
         *
         * Navigation Actions:
         * - FAB click → navigate to "add"
         * - Habit card click → navigate to "details/{id}"
         */
        composable("home") {
            HomeScreen(navController, viewModel)
        }

        /**
         * Add Habit Screen Route - Create new habit
         *
         * Displays:
         * - Title input field (required)
         * - Description input field (optional)
         * - Save button
         *
         * Navigation Actions:
         * - Save → popBackStack() to return to home
         * - Back button → popBackStack() to return to home
         */
        composable("add") {
            AddHabitScreen(navController, viewModel)
        }

        /**
         * Habit Details Screen Route - Edit/delete existing habit
         *
         * Route Pattern: "details/{habitId}"
         * Parameter: habitId (Int) - unique identifier of the habit
         *
         * Displays:
         * - Editable title and description
         * - Completion checkbox
         * - Update button
         * - Delete button (red)
         *
         * Navigation Actions:
         * - Update → popBackStack() to return to home
         * - Delete → popBackStack() to return to home
         * - Back button → popBackStack() to return to home
         *
         * Parameter Extraction:
         * The habitId is extracted from the route and passed to the screen.
         * Example: "details/5" → habitId = 5
         */
        composable(
            route = "details/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { backStackEntry ->
            // Extract habitId from navigation arguments
            val habitId = backStackEntry.arguments?.getInt("habitId") ?: 0
            HabitDetailsScreen(navController, habitId, viewModel)
        }
    }
}
