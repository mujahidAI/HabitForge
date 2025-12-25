package com.example.habitforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitforge.data.HabitDatabase
import com.example.habitforge.navigation.AppNavGraph
import com.example.habitforge.viewmodel.HabitViewModel
import com.example.habitforge.viewmodel.HabitViewModelFactory
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Build Room database + DAO
        val dao = HabitDatabase.getDatabase(applicationContext).habitDao()

        // Provide DAO to ViewModel
        val habitViewModel = HabitViewModelFactory(dao).create(HabitViewModel::class.java)

        setContent {
            MaterialTheme {
                AppNavGraph() // Start navigation and screens
            }
        }
    }
}
