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

@Composable
fun AppNavGraph(viewModel: HabitViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // 🏠 Home Screen
        composable("home") {
            HomeScreen(navController, viewModel)
        }

        // ➕ Add Habit Screen
        composable("add") {
            AddHabitScreen(navController, viewModel)
        }

        // 📌 Habit Details Screen
        composable(
            route = "details/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getInt("habitId") ?: 0
            HabitDetailsScreen(navController, habitId, viewModel)
        }
    }
}
